package cps.accounts;

import logging.SmartLog;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;

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
    private static final int MAC_ADDRESS_LENGTH = 6;

    // Local addresses obtained from all discovered interfaces on the machine.
    private ArrayList<String> localAddresses;

    /**
     * Initialize an AccountManager for the SmartGrow server.
     *
     * @throws SocketException
     */
    public AccountManager() throws SocketException {

        // Discover all addresses on interfaces registered on this machine
        this.indexLocalAddresses();
    }

    /**
     * Retrieve the MAC address of the local area network device discovered
     * through the specified address.
     *
     * @param address The IPv4 address of the device
     * @return
     * @throws SocketException
     */
    public byte[] getMACAddress(String address) throws IOException {

        // Use the NetworkInterface class if the address is a local one (i.e., on the same machine)
        if (this.localAddresses.contains(address)) {
            return NetworkInterface.getByInetAddress(InetAddress.getByName(address)).getHardwareAddress();
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
                    return this.convertMACAddressToByteArray(arpLine.split(" ")[3]);
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

    /*
     * Convert a string MAC address to its byte equivalent.
     */
    private byte[] convertMACAddressToByteArray(String macAddress) {
        byte[] result = new byte[MAC_ADDRESS_LENGTH];

        String[] values = macAddress.split(":");
        if (values.length == MAC_ADDRESS_LENGTH) {
            for (int i = 0; i < values.length; i++) {
                result[i] = (byte)Integer.parseInt(values[i], 16);
            }
        } else {

            // This is not normal: Whoever called this function screwed up.
            logger.error("Mac address provided does not contain 6 values!");
            return null;
        }

        return result;
    }
}