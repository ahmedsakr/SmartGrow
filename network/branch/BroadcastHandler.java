package network.branch;

import network.core.Packet;

/**
 * BroadcastHandler defines an interface for classes to implement
 * in order to manage broadcasts sent by branches.
 * 
 * @author Ahmed Sakr
 * @since November 30, 2019
 */
public interface BroadcastHandler {

    /**
     * Handles a broadcast packet according to the use case of the implementer.
     *
     * @param broadcast The packet that has been identified as a broadcast
     */
    void handleBroadcast(Packet broadcast);
}