package cps.accounts;

import logging.SmartLog;
import util.MACAddress;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

import cps.accounts.Account;
import cps.database.tables.LeafAccounts;
import cps.database.exceptions.SmartgrowDatabaseException;
import network.stem.LeafAccountHandler;

/**
 * AccountManager provides the SmartGrow server with an ability
 * to automatically create accounts for leaves persisted by virtue
 * of their physical (MAC) address.
 * 
 * @author Ahmed Sakr
 * @since November 25, 2019
 */
public class AccountManager implements LeafAccountHandler {

    // The logging instance for this class
    private static final SmartLog logger = new SmartLog(AccountManager.class.getName());

    // The ARP command for deriving all MAC address entries for IPs
    private static final String ARP_COMMAND = "arp -a";

    // The LeafAccounts table wrapper
    private LeafAccounts accounts;

    // Local addresses obtained from all discovered interfaces on the machine.
    private ArrayList<String> localAddresses;

    /**
     * Initialize an AccountManager for the SmartGrow server.
     *
     * @throws SocketException
     */
    public AccountManager(LeafAccounts accounts) throws SocketException {

        this.accounts = accounts;

        // Discover all addresses on interfaces registered on this machine
        this.indexLocalAddresses();
    }

    /**
     * Implementation for the LeafAccountHandler interface method.
     * This is called when a leaf has sent a registration request to the server.
     *
     * @param address The IPv4 address of the leaf
     * @return The Account object for the leaf
     */
    @Override
    public Account onLeafConnection(String address) throws SmartgrowDatabaseException, IOException {

        // Create an account for the leaf if it does not already have one
        if (this.getAccount(address) == null) {
            this.createAccount(address);
        }

        // Return the account information for this leaf
        return this.getAccount(address);
    }

    /**
     * Retrieve the account information for the leaf identified by the IPv4 address.
     *
     * @param address The IPv4 address of the leaf
     * @return An account object if the leaf has an existing account entry. Otherwise, if the
     *         leaf has never been seen before, null is returned.
     */
    public Account getAccount(String address) throws SmartgrowDatabaseException, IOException {
        int leafId = this.accounts.getLeafId(this.getMACAddress(address));

        if (leafId == LeafAccounts.ACCOUNT_DOES_NOT_EXIST) {
            return null;
        } else {
            return new Account(leafId, address);
        }
    }

    /**
     * Creates a new account for the provided IP address using its MAC address.
     *
     * @param address The leaf identified by its IPv4 address
     */
    public void createAccount(String address) throws SmartgrowDatabaseException, IOException {
        this.accounts.storeMacAddress(this.getMACAddress(address));
    }

    /**
     * Retrieve the MAC address of the local area network device discovered
     * through the specified address.
     *
     * @param address The IPv4 address of the device
     * @return A string representation of the MAC address
     * @throws SocketException
     */
    private String getMACAddress(String address) throws IOException {

        // Use the NetworkInterface class if the address is a local one (i.e., on the same machine)
        if (this.localAddresses.contains(address)) {
            byte[] macAddr = NetworkInterface.getByInetAddress(InetAddress.getByName(address)).getHardwareAddress();

            if (macAddr == null) {
                return null;
            } else {
                return MACAddress.convertMacAddressToString(macAddr);
            }
        }
        
        /*
         * Query the ARP table for the MAC address.
         *
         * Using ARP for acquiring reliable MAC address should be sufficient for the SmartGrow
         * application as all nodes are in constant communication, keeping the ARP table
         * updated.
         */
        try (Scanner s = new Scanner(Runtime.getRuntime().exec(ARP_COMMAND).getInputStream())) {
            String arpLine = null;

            while (s.hasNext()) {
                arpLine = s.nextLine();

                // Return the MAC address of the matched ARP entry
                if (arpLine.contains(address)) {
                    return arpLine.split(" ")[3];
                }
            }
        }

        // No way to discover the MAC address of the device.
        return null;
    }

    /*
     * Indexes all addresses for interfaces registered on this machine for future quick
     * reference.
     */
    private void indexLocalAddresses() throws SocketException {

        // Initialize the list of local addresses
        this.localAddresses = new ArrayList<>();

        /*
         * Loop over all interfaces on this machine and register all discovered
         * IP addressed in the localAddresses list. 
         */
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {

            Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
            while (addresses.hasMoreElements()) {
                this.localAddresses.add(addresses.nextElement().getHostName());
            }
        }
    }
}