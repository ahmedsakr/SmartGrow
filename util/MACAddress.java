package util;

import logging.SmartLog;

public class MACAddress {

    // The logging instance for this class
    private static final SmartLog logger = new SmartLog(MACAddress.class.getName());

    private static final String MAC_ADDRESS_FORMAT = "%x:%x:%x:%x:%x:%x";
    private static final int MAC_ADDRESS_LENGTH = 6;

    /**
     * Convert a byte-representation of a MAC address to its string representation
     *
     * @param macAddress The byte-array representation of the MAC address
     *
     * @return A string representation of the MAC address
     */
    public static String convertMacAddressToString(byte[] macAddress) {
        if (macAddress == null || macAddress.length != MAC_ADDRESS_LENGTH) {
            logger.error("Mac address provided does not contain 6 values!");
            return null;
        } else {
            return String.format(MAC_ADDRESS_FORMAT,
                macAddress[0], macAddress[1], macAddress[2], macAddress[3], macAddress[4], macAddress[5]);
        }
    }

    /**
     * Convert a string-representation of a MAC address to its byte representation.
     *
     * @param macAddress The String representation of the MAC Address
     *
     * @return A 6-element byte array containing the values of the MAC address
     */
    public static byte[] convertMACAddressToByteArray(String macAddress) {
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