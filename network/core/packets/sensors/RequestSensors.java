package network.core.packets.sensors;

import java.util.Arrays;

import network.core.OpCodes;
import network.core.Packet;
import network.leaf.Identity;

/**
 * The RequestSensors packet is a packet exclusively for the usage
 * by android users when they wish to retain the latest sensor
 * information for a a specific plant.
 * 
 * @author Ahmed Sakr
 * @since November 7, 2019
 */
public class RequestSensors extends Packet {

    public RequestSensors() {
        super(OpCodes.REQUEST_SENSORS);
    }

    /**
     * Retrieves all information for this RequestSensors packet by reading the
     * provided payload.
     *
     * This method is invoked by Packet::fromPayload when the caller wishes to
     * create an instance of RequestSensors through a given payload.
     * 
     * @param payload A prepopulated 512-byte payload used to get information from
     */
    @Override
    protected void extract(byte[] payload) {

    }

    /**
     * Builds the parent payload by moving the state information in this object
     * into the parent packet.
     * 
     * Order of addition matters. This defines the format of the packet.
     */
    @Override
    protected void build() {

    }
}