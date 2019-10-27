package network.branch;

import network.core.Packet;
import network.branch.threads.LeafPruningThread;
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
    private String name;
    private LeafPruningThread pruner;

    /**
     * Initializes the branch with a random port.
     */
    public Branch(String name) {
        this.servicers = new ArrayList<>();
        this.name = name;

        this.pruner = new LeafPruningThread(this);
    }
    
    /**
     * Retrieve the currently active leaf servicers.
     * 
     * @return A list of all DedicatedLeafServicers on this branch.
     */
    public synchronized ArrayList<DedicatedLeafServicer> getServicers() {
        return this.servicers;
    }

    /**
     * Retrieve the name of this branch.
     *
     * @return A string name of this branch.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Spawn a new servicer for the leaf specified by the location.
     * 
     * @param location The IPv4 address of the leaf
     */
    public synchronized void addLeaf(NodeLocation location) throws SocketException {
        logger.info("Adding a new leaf servicer for " + location);
        this.servicers.add(new DedicatedLeafServicer(location));
    }

    /**
     * Removes an existing servicer for the leaf speicified by the location.
     * 
     * @param location The IPv4 address of the leaf
     */
    public synchronized void removeLeaf(NodeLocation location) {
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
    public synchronized void broadcast(Packet packet) throws IOException {
        logger.debug("Broadcasting packet to all leaves");

        for (DedicatedLeafServicer servicer : this.servicers) {
            servicer.forwardBroadcast(packet);
        }
    }

    /**
     * Override the default Object toString() method to return the name of this branch.
     * 
     * @return The name of this branch.
     */
    @Override
    public String toString() {
        return this.name;
    }
}