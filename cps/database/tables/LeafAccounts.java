package cps.database.tables;

import java.sql.ResultSet;
import java.sql.SQLException;

import cps.database.DatabaseController;
import cps.database.DatabaseInfo;
import cps.database.exceptions.SmartgrowDatabaseException;


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

    // Constant for signalling that the account does not exist.
    public static final int ACCOUNT_DOES_NOT_EXIST = -1;

    // Object representation for manipulating the database.
    private DatabaseController database;

    /**
     * Initialize the LeafAccounts database table wrapper.
     *
     * @param database The Database connection for this instance
     */
    public LeafAccounts(DatabaseController database) {
        this.database = database;
    }

    /**
     * Query the leaf_accounts table to find a row that matches the provided
     * MAC address.
     *
     * @param macAddress The MAC address of the leaf
     * @return A unique id for the leaf
     */
    public int getLeafId(String macAddress) throws SmartgrowDatabaseException {

        // Don't bother trying anything if the provided macAddress is null.
        if (macAddress == null) {
            return ACCOUNT_DOES_NOT_EXIST;
        }

        // Fetch the corresponding leaf_id for the macAddress
        String sql = String.format("SELECT leaf_id FROM %s WHERE macAddress = '%s'",
            DatabaseInfo.DATABASE_LEAF_ACCOUNTS_TABLE, macAddress);
        ResultSet result = this.database.query(sql);

        try {
            if (!result.next()) {
                return ACCOUNT_DOES_NOT_EXIST;
            } else {
                return result.getInt("leaf_id");
            }
        } catch (SQLException ex) {
            throw new SmartgrowDatabaseException("SQL exception encountered during getLeafId");
        }
    }

    /**
     * Create a new entry in the leaf_accounts table for the provided MAC
     * address.
     *
     * @param macAddress The MAC address of the leaf
     */
    public void storeMacAddress(String macAddress) throws SmartgrowDatabaseException {

        // Don't bother trying anything if the provided macAddress is null.
        if (macAddress == null) {
            return;
        }

        // Update the SQL table with the macAddress
        String sql = String.format("INSERT INTO %s (macAddress) VALUES ('%s')",
            DatabaseInfo.DATABASE_LEAF_ACCOUNTS_TABLE, macAddress);
        this.database.update(sql);
    }
}