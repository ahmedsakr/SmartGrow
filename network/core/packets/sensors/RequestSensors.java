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

    // The plant id to request sensors data for
    private int plantId;

    /**
     * Initialize a RequestSensors packet.
     */
    public RequestSensors() {
        super(OpCodes.REQUEST_SENSORS);
    }

    /**
     * Set the plant id for this request.
     *
     * @param plantId The integer plant id
     */
    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    /**
     * Retrieve the plant id for this request.
     *
     * @return The integer plant id
     */
    public int getPlantId() {
        return this.plantId;
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
        this.plantId = super.getInt(payload, 0);
    }

    /**
     * Builds the parent payload by moving the state information in this object
     * into the parent packet.
     * 
     * Order of addition matters. This defines the format of the packet.
     */
    @Override
    protected void build() {
        super.addInt(this.plantId);
    }
}