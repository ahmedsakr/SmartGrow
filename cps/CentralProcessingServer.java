package cps;

import java.net.SocketException;

import cps.database.DatabaseController;
import cps.database.exceptions.SmartgrowDatabaseException;
import cps.management.managers.AndroidUserManager;
import cps.management.managers.PlantEndpointManager;
import logging.SmartLog;
import network.Configuration;
import network.leaf.Identity;
import network.stem.Stem;

/**
 * CentralProcessingServer is the main class for the Central Processing Server (cpm)
 * subsystem of SmartGrow. It will initialize all resources needed to carry
 * out the management of plant endpoints and android users.
 * 
 * @author Ahmed Sakr
 * @since October 25, 2019
 */
public class CentralProcessingServer {

    private static SmartLog logger = new SmartLog(CentralProcessingServer.class.getName());

    // The UDP abstraction layer allowing the server to handle multiple leaves
    private Stem stem;
    private DatabaseController controller;

    /**
     * Start up the server by initializing its UDP transport layer.
     */
    public CentralProcessingServer(int port) throws SocketException {
        this.stem = new Stem(port);

        try {
            this.controller = new DatabaseController();
        } catch (SmartgrowDatabaseException ex) {
            logger.fatal("Unable to create database controller: " + ex.getMessage());
            System.exit(1);
        }

        // Attach the leaves managers to the stem.
        this.stem.addManager(Identity.PLANT_ENDPOINT, new PlantEndpointManager(this.controller));
        this.stem.addManager(Identity.ANDROID_USER, new AndroidUserManager(this.controller));
    }

    public static void main(String[] args) {
        try {
            CentralProcessingServer server = new CentralProcessingServer(Configuration.CPS_PORT);
            logger.info("Successfully initialized cps on port " + Configuration.CPS_PORT);
        } catch (SocketException ex) {
            logger.fatal("Unable to initialize cps on port " + Configuration.CPS_PORT);
            ex.printStackTrace();
            System.exit(1);
        }
    }
}