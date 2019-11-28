package network.stem;

import java.io.IOException;

import cps.accounts.Account;
import cps.database.exceptions.SmartgrowDatabaseException;

/**
 * LeafAccountHandler provides an interface for implementing subclasses to
 * handle account scanning for detected leaf connections on the receiving
 * stem.
 * 
 * @author Ahmed Sakr
 * @since November 27, 2019
 */
public interface LeafAccountHandler {

    /**
     * Provides a subclass-dependent implementation of account management
     * for leaves in the SmartGrow system.
     *
     * @param address The IPv4 address of the leaf
     * @return An Account object for the database-backed entry for this leaf.
     */
    Account onLeafConnection(String address) throws SmartgrowDatabaseException, IOException;
}