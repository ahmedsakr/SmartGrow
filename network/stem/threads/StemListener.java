package network.stem.threads;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import network.core.NodeLocation;
import network.core.exceptions.CorruptPacketException;
import network.core.exceptions.TransportInterruptedException;
import network.core.packets.LeafRegistration;
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
    private static Logger logger = LogManager.getLogger(StemListener.class);

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
                LeafRegistration packet = (LeafRegistration) this.stem.receive();
                NodeLocation location = new NodeLocation(packet.getAddress(), packet.getPort());
                logger.info("New client from " + location);

                if (this.stem.isExistingLeaf(location)) {
                    logger.warn("Repeated registration request from " + location);
                    continue;
                }

                if (packet.getIdentity() == Identity.ANDROID_USER) {
                    this.stem.getUsersBranch().addLeaf(location);
                } else {
                    this.stem.getPlantsBranch().addLeaf(location);
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