package cps;

import java.net.SocketException;

import config.SmartGrowConfiguration;
import cps.accounts.AccountManager;
import cps.database.DatabaseController;
import cps.database.exceptions.SmartgrowDatabaseException;
import cps.database.tables.LeafAccounts;
import cps.management.managers.AndroidUserManager;
import cps.management.managers.PlantEndpointManager;
import logging.SmartLog;
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
    public CentralProcessingServer(int port) {

        // Initialize the server on the specified port
        try {
            this.stem = new Stem(port);
        } catch (SocketException ex) {
            logger.fatal("Unable to initialize cps on port " + SmartGrowConfiguration.CPS_PORT);
            System.exit(1);
        }

        logger.info("Successfully initialized cps on port " + SmartGrowConfiguration.CPS_PORT);

        // Establish a connection to the SmartGrow database
        try {
            this.controller = new DatabaseController();
        } catch (SmartgrowDatabaseException ex) {
            logger.fatal("Unable to create database controller: " + ex.getMessage());
            System.exit(1);
        }

        // Attach the leaves managers to the stem.
        this.stem.addManager(Identity.PLANT_ENDPOINT, new PlantEndpointManager(this.controller));
        this.stem.addManager(Identity.ANDROID_USER, new AndroidUserManager(this.controller));

        // Attach an account handler to this server instance.
        try {
            this.stem.addAccountHandler(new AccountManager(new LeafAccounts(this.controller)));
        } catch (SocketException ex) {
            logger.fatal("Failed to attach account manager to server.");
            System.exit(1);
        }
    }

    /**
     * Main method for creating a CentralProcessingServer instance.
     *
     * @param args Run-time arguments (None at the moment)
     */
    public static void main(String[] args) {
        new CentralProcessingServer(SmartGrowConfiguration.CPS_PORT);
    }
}