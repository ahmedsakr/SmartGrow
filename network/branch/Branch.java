package network.branch;

import network.core.Packet;
import network.core.NodeLocation;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Branch allows for a logical grouping of one or more
 * leaves to facilitate broadcasting of messages.
 * 
 * @since October 16, 2019
 * @author Ahmed Sakr
 */
public class Branch {

    private static Logger logger = LogManager.getLogger(Branch.class);

    // The list of live nodes connected to this stem.
    private ArrayList<DedicatedLeafServicer> servicers;

    /**
     * Initializes the branch with a random port.
     */
    public Branch() {
        this.servicers = new ArrayList<>();
    }

    /**
     * Spawn a new servicer for the leaf specified by the location.
     * 
     * @param location The IPv4 address of the leaf
     */
    public void addLeaf(NodeLocation location) throws SocketException {
        logger.info("Adding a new leaf servicer for " + location);
        this.servicers.add(new DedicatedLeafServicer(location));
    }

    /**
     * Removes an existing servicer for the leaf speicified by the location.
     * 
     * @param location The IPv4 address of the leaf
     */
    public void removeLeaf(NodeLocation location) {
        for (DedicatedLeafServicer servicer : this.servicers) {
            if (servicer.getDestination().equals(location)) {
                logger.info("Stop leaf servicer for " + location);
                servicer.stop();
                this.servicers.remove(servicer);
                break;
            }
        }
    }

    /**
     * Send a message to all leaves on this branch.
     * 
     * @param packet The packet to broadcast to all leaves
     */
    public void broadcast(Packet packet) throws IOException {
        logger.debug("Broadcasting packet to all leaves");

        for (DedicatedLeafServicer servicer : this.servicers) {
            servicer.forwardBroadcast(packet);
        }
    }
}