package cps.management.managers;

import cps.accounts.Account;
import cps.database.DatabaseController;
import cps.database.exceptions.SmartgrowDatabaseException;
import cps.database.tables.PlantData;
import cps.management.LeafManager;
import logging.SmartLog;
import network.core.Packet;
import network.core.packets.sensors.RequestSensors;

/**
 * AndroidUserManager provides the behaviour for interacting with
 * packets received from android users.
 * 
 * @author Ahmed Sakr
 * @since November 6, 2019
 */
public class AndroidUserManager implements LeafManager {

    // The logger instance for this class
    private static SmartLog logger = new SmartLog(AndroidUserManager.class.getName());

    // Object representation for connecting and updating the database.
    private DatabaseController database;
    private PlantData plantsData;

    /**
     * Initialize the manager for android users.
     *
     * @param database The object representation for connecting to the database.
     */
    public AndroidUserManager(DatabaseController database) {
        this.database = database;
        this.plantsData = new PlantData(database);
    }

    /**
     * The handle instance for android user packets. Unimplemented at the moment.
     */
    @Override
    public Packet handle(Account account, Packet packet) {
        if (packet == null || !(packet instanceof RequestSensors)) {
            return null;
        }

        try {

            // Retrieve the latest sensor values from the plant data.
            return this.plantsData.getLatestSensorData();

        } catch (SmartgrowDatabaseException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }
}