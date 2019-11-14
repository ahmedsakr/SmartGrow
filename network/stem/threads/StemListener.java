package network.stem.threads;

import java.io.IOException;

import logging.SmartLog;
import network.core.NetworkErrors;
import network.core.NodeLocation;
import network.core.Packet;
import network.core.exceptions.CorruptPacketException;
import network.core.exceptions.TransportInterruptedException;
import network.core.packets.GenericError;
import network.core.packets.registration.LeafRegistration;
import network.leaf.Identity;
import network.stem.Stem;

/**
 * StemListener is a worker thread utilized by the Stem class
 * to fulfill leaves registration requests being sent to the
 * Stem.
 *
 * @author Ahmed Sakr
 * @since November 8, 2019
 */
public class StemListener extends Thread {

    // The logger instance for this StemListener instance
    private static SmartLog logger = new SmartLog(StemListener.class.getName());

    // The stem that this StemListener thread is servicing.
    private Stem stem;

    /**
     * Initialize the StemListener thread.
     *
     * @param stemPort The port that the Stem is listening on
     */
    public StemListener(Stem stem) {
        super(String.format("StemListener-%d", stem.getPort()));
        this.stem = stem;

        // Immediately start the thread.
        this.start();
    }

    /**
     * The entry point for the StemListener thread.
     */
    @Override
    public void run() {
        try {
            while (true) {
                Packet packet = this.stem.receive();
                NodeLocation location = new NodeLocation(packet.getAddress(), packet.getPort());

                /*
                 * The following if-else if-else logic covers the following two (2) irregular
                 * scenarios and the expected scenario:
                 * 
                 * 1. (Irregular) A packet that is not LeafRegistration has been sent to the Stem. The stem
                 * is only supposed to receive LeafRegistration packets because everything else should go
                 * through the DedicatedLeafServicers.
                 * 2. (Irregular) A repeated LeafRegistration request packet from a leaf that is already
                 * being serviced.
                 * 3. (Expected) A LeafRegistration packet from a client that is not being serviced yet.
                 */
                if (!(packet instanceof LeafRegistration)) {
                    logger.warn("Received packet that is not LeafRegistration");

                    GenericError error = new GenericError(NetworkErrors.WRONG_PACKET,
                        "You are only allowed to send LeafRegistration packets to the main server.");
                    error.setDestination(location);

                    this.stem.send(error);
                } else if (this.stem.isExistingLeaf(location)) {
                    logger.warn("Repeated registration request from " + location);

                    GenericError error = new GenericError(NetworkErrors.LEAF_ALREADY_REGISTERED,
                        "You are already registered with the server.");
                    error.setDestination(location);

                    this.stem.send(error);
                } else {
                    logger.info("New client from " + location);
                    
                    // Register the leaf with the stem
                    LeafRegistration registration = (LeafRegistration) packet;
                    this.stem.registerLeaf(location, registration.getIdentity());
                }
            }
        } catch (CorruptPacketException ex) {
            logger.error("Received corrupt packet");
        } catch (TransportInterruptedException ex) {
            logger.error("Transport interrupted");
        } catch (IOException ex) {
            logger.error("I/O exception encountered during receive");
        }
    }
}