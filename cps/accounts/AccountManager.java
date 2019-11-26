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

import cps.database.tables.LeafAccounts;

/**
 * AccountManager provides the SmartGrow server with an ability
 * to automatically create accounts for leaves persisted by virtue
 * of their physical (MAC) address.
 * 
 * @author Ahmed Sakr
 * @since November 25, 2019
 */
public class AccountManager {

    // The logging instance for this class
    private static final SmartLog logger = new SmartLog(AccountManager.class.getName());

    // The ARP command for deriving all MAC address entries for IPs
    private static final String ARP_COMMAND = "arp -a";

    // The LeafAccounts table wrapper
    private LeafAccounts leafAccounts;

    // Local addresses obtained from all discovered interfaces on the machine.
    private ArrayList<String> localAddresses;

    /**
     * Initialize an AccountManager for the SmartGrow server.
     *
     * @throws SocketException
     */
    public AccountManager(LeafAccounts leafAccounts) throws SocketException {

        this.leafAccounts = leafAccounts;

        // Discover all addresses on interfaces registered on this machine
        this.indexLocalAddresses();
    }

    /**
     * Check if the leaf identified by the IPv4 address already has an account
     * with SmartGrow.
     *
     * @param address The IPv4 address of the leaf
     * @return      true    if the leaf already has an account
     *              false   Otherwise
     */
    public boolean accountExists(String address) throws IOException {
        return this.leafAccounts.getLeafId(this.getMACAddress(address)) != -1;
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