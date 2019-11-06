package cps.management.managers;

import cps.database.DatabaseController;
import cps.management.LeafManager;
import network.core.Packet;

/**
 * AndroidUserManager provides the behaviour for interacting with
 * packets received from android users.
 * 
 * @author Ahmed Sakr
 * @since November 6, 2019
 */
public class AndroidUserManager implements LeafManager {

    // Object representation for connecting and updating the database.
    private DatabaseController database;

    /**
     * Initialize the manager for android users.
     *
     * @param database The object representation for connecting to the database.
     */
    public AndroidUserManager(DatabaseController database) {
        this.database = database;
    }

    /**
     * The handle instance for android user packets. Unimplemented at the moment.
     */
    @Override
    public boolean handle(Packet packet) {
        return false;
    }
}