package network.branch;

import java.net.DatagramSocket;
import java.util.ArrayList;

import network.core.NodeLocation;

/**
 * Branch allows for a logical grouping of one or more
 * leaves to facilitate broadcasting of messages.
 * 
 * @since October 16, 2019
 * @author Ahmed Sakr
 */
public class Branch extends Transport {

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
    public void addLeaf(NodeLocation location) {
        this.servicers.add(new DedicatedLeafServicer(location));
    }

    /**
     * Send a message to all leaves on this branch.
     * 
     * @param packet The packet to broadcast to all leaves
     */
    public void broadcast(Packet packet) {
        this.servicers.forEach((servicer) -> servicer.forwardBroadcast(packet));
    }
}