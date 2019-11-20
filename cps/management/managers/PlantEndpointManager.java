package cps.management.managers;

import cps.database.DatabaseController;
import cps.database.exceptions.SmartgrowDatabaseException;
import cps.database.tables.PlantData;
import cps.management.LeafManager;
import logging.SmartLog;
import network.core.Packet;
import network.core.packets.Acknowledgement;
import network.core.packets.sensors.SensorsData;

/**
 * PlantEndpointManager defines the behaviour when interacting with
 * packets received from leaves that identify as plant endpoints.
 * 
 * @author Ahmed Sakr
 * @since November 6, 2019
 */
public class PlantEndpointManager implements LeafManager {

    // The logger instance for this class.
    private static SmartLog logger = new SmartLog(PlantEndpointManager.class.getName());

    // Object representations for accessing and updating the database
    private DatabaseController database;
    private PlantData plantsData;

    /**
     * Initialize a PlantEndpointManager object with the DatabaseController
     * object.
     *
     * @param database The DatabaseController object providing us with access to the database.
     */
    public PlantEndpointManager(DatabaseController database) {
        this.database = database;
        this.plantsData = new PlantData(database);
    }

    /**
     * The handling implementation for plant endpoints.
     */
    @Override
    public Packet handle(Packet packet) {
        if (packet == null || !(packet instanceof SensorsData)) {
            return null;
        }

        try {

            // Attempt to append the sensors data to the plant_data table.
            this.plantsData.insertSensorsData((SensorsData) packet);

        } catch (SmartgrowDatabaseException ex) {
            logger.fatal("Unable to append data: " + ex.getMessage());
            return null;
        }

        // No return value for now: What to do when a plant endpoint
        // gives us values?
        return new Acknowledgement();
    }
}