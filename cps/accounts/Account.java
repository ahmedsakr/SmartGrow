package cps.accounts;

/**
 * Account provides a wrapper for the entries in the SmartGrow
 * leaf accounts table. It provides an easy to use interface for
 * grabbing important information about a leaf.
 * 
 * @author Ahmed Sakr
 * @since November 27, 2019
 */
public class Account {

    // The id and address associated with this account entry
    private int id;
    private String macAddress;

    /**
     * Create an Account association.
     *
     * @param id The leaf id
     * @param macAddress The MAC address of the leaf
     */
    public Account(int id, String macAddress) {
        this.id = id;
        this.macAddress = macAddress;
    }

    /**
     * Retrieve the id for the account.
     *
     * @return The leaf id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Retrieve the MAC address for the account.
     *
     * @return The MAC address
     */
    public String getMACAddress() {
        return this.macAddress;
    }
}