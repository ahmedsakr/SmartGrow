package cps;

import java.net.SocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cps.database.DatabaseController;
import cps.database.exceptions.SmartgrowDatabaseException;
import network.Configuration;
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

    private static Logger logger = LogManager.getLogger(CentralProcessingServer.class);

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