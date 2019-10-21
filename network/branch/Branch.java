package network.branch;

import network.core.Packet;
import network.core.NodeLocation;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Branch allows for a logical grouping of one or more
 * leaves to facilitate broadcasting of messages.
 * 
 * @since October 16, 2019
 * @author Ahmed Sakr
 */
public class Branch {

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
        this.servicers.add(new DedicatedLeafServicer(location));
    }

    public void removeLeaf(NodeLocation location) {
        this.servicers.removeIf((servicer) -> servicer.getDestination().equals(location));
    }

    /**
     * Send a message to all leaves on this branch.
     * 
     * @param packet The packet to broadcast to all leaves
     */
    public void broadcast(Packet packet) throws IOException {
        for (DedicatedLeafServicer servicer : this.servicers) {
            servicer.forwardBroadcast(packet);
        }
    }
}