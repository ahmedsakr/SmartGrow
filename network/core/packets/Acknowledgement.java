package network.core.packets;

import java.util.Arrays;

import network.core.OpCodes;
import network.core.Packet;

/**
 * GenericError Packet provides all nodes in the
 * SmartGrow systems with a mechanism to communicate errors
 * or faults to other nodes in the system to facilitate recovery
 * if possible.
 * 
 * @author Ahmed Sakr
 * @since November 8, 2019
 */
public class Acknowledgement extends Packet {

    /**
     * Initialize the GenericError packet.
     */
    public Acknowledgement() {
        super(OpCodes.ACKNOWLEDGEMENT);
    }

    /**
     * Retrieves all information for this Acknowledgement packet by reading the
     * provided payload.
     * 
     * This method is invoked by Packet::fromPayload when the caller wishes to
     * create an instance of Acknowledgement through a given payload.
     * 
     * @param payload A prepopulated 512-byte payload used to get information from
     */
    @Override
    public void extract(byte[] data) {

    }

    /**
     * Builds the parent payload by moving the state information in this object
     * into the parent packet.
     * 
     * Order of addition matters. This defines the format of the packet.
     */
    @Override
    public void build() {

    }
}