package network.core.packets;

import java.util.Arrays;
import java.util.HashMap;

import network.core.OpCodes;
import network.core.Packet;

/**
 * The SensorsData packet is sent by plant endpoints to the central
 * processing server containing the latest sensory information
 * it has retrieved.
 * 
 * @author Ahmed Sakr
 * @since October 10, 2019
 */
public class SensorsData extends Packet {

    private HashMap<Byte, Double> data;

    /**
     * Initialize a new SensorsData packet object.
     */
    public SensorsData() {
        super(OpCodes.SENSORS_DATA);
        this.data = new HashMap<>();
    }

    /**
     * Adds sensory information for the specified sensor.
     * 
     * @param sensorId The well-known id of the sensor
     * @param sensorData The data of that sensor.
     */
    public void addSensorData(byte sensorId, double sensorData) {
        this.data.put(sensorId, sensorData);
    }

    /**
     * Retrieves the sensor data of the specified sensor.
     * 
     * @param sensorId The well-known id of the sensor
     * 
     * @return The sensor data
     */
    public double getSensorData(byte sensorId) {
        return this.data.get(sensorId);
    }

    /**
     * Retrieves all information for this SensorsData packet by reading the
     * provided payload.
     * 
     * This method is invoked by Packet::fromPayload when the caller wishes to
     * create an instance of SensorsData through a given payload.
     * 
     * @param payload A prepopulated 512-byte payload used to get information from
     */
    @Override
    protected void extract(byte[] payload) {

        // A sensor data block consists of the sensor id (1 byte) and sensor value (8 bytes)
        int sensorDataLength = 1 + Double.BYTES;

        for (int i = 1; payload[i] != 0; i += sensorDataLength) {
            this.data.put(payload[i],
                this.convertBytesToDouble(Arrays.copyOfRange(payload, i + 1, i + sensorDataLength)));
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
        for (Byte sensorId : this.data.keySet()) {
            super.addByte(sensorId);
            super.addDouble(this.data.get(sensorId));
        }
    }
}