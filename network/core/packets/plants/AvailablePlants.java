package network.core.packets.plants;

import java.util.Arrays;
import java.util.HashMap;

import network.core.OpCodes;
import network.core.Packet;

/**
 * AvailablePlants is a packet that is sent to all android users informing
 * them of all plants that are currently reporting to the SmartGrow system.
 * This packet allows the android users to discover all plants and view
 * their real-time sensory information.
 * 
 * @author Ahmed Sakr
 * @since November 30, 2019
 */
public class AvailablePlants extends Packet {

    // The structure holding the plant id-name pairs
    private HashMap<Byte, String> plants;

    /**
     * Initialize the AvailablePlants object.
     */
    public AvailablePlants() {
        super(OpCodes.AVAILABLE_PLANTS);

        this.plants = new HashMap<>();
    }

    /**
     * Add a plant id-name pair to the set of plants.
     *
     * @param plantId The unique id of the plant
     * @param plantName The name of the plant
     */
    public void addPlant(byte plantId, String plantName) {
        this.plants.put(plantId, plantName);
    }

    /**
     * Retrieve the whole set of plants in this packet.
     *
     * @return The HashMap composed of all plant id-name pairs.
     */
    public HashMap<Byte, String> getPlants() {
        return this.plants;
    }

    /**
     * Retrieves all information for this AvailablePlants packet by reading the
     * provided payload.
     * 
     * This method is invoked by Packet::fromPayload when the caller wishes to
     * create an instance of AvailablePlants through a given payload.
     * 
     * @param payload A prepopulated 512-byte payload used to get information from
     */
    @Override
    protected void extract(byte[] payload) {

        String plantName = null;
        for (int i = 0; payload[i] != 0 && i < payload.length; ) {
            
            // Get the plant name for the associated plant id
            plantName = super.getString(Arrays.copyOfRange(payload, i + 1, payload.length));

            // Add the plant to the set if its name is not null or empty.
            if (plantName != null && plantName.length() > 0) {
                this.plants.put(payload[i], plantName);

                // Move the index to the next entry
                // - 1 byte for the plant id
                // - x bytes for the plant name
                // - 1 byte for the null-byte.
                i += (1 + plantName.length() + 1);
            }
        }
    }

    /**
     * Builds the parent payload by moving the state information in this object
     * into the parent packet.
     * 
     * Order of addition matters. This defines the format of the packet.
     */
    @Override
    protected void build() {
        for (byte plantId : this.plants.keySet()) {
            super.addByte(plantId);
            super.addString(this.plants.get(plantId));
        }
    }
}