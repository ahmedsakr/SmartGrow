package cps.database.tables;

/**
 * LeafAccounts is a wrapper for the leaf_accounts table
 * in the SmartGrow database. The leaf_accounts table stores
 * the MAC addresses for all leaves that we have interacted with
 * in the past to provide an automatic personal experience.
 * 
 * @author Ahmed Sakr
 * @since November 26, 2019
 */
public class LeafAccounts {


    /**
     * Query the leaf_accounts table to find a row that matches the provided
     * MAC address.
     *
     * @param macAddress The MAC address of the leaf
     * @return A unique id for the leaf
     */
    public int getLeafId(String macAddress) {
        return 0;
    }

    /**
     * Create a new entry in the leaf_accounts table for the provided MAC
     * address.
     *
     * @param macAddress The MAC address of the leaf
     */
    public void storeMacAddress(String macAddress) {

    }
}