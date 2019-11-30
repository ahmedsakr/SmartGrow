package network.core.packets.sensors;

import java.util.Arrays;
import java.util.HashMap;

import endpoint.sensors.SupportedSensors;
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

        // Initialize the hashmap with the starting value of the sensors.
        this.initialize();
    }

    /**
     * Adds sensory information for the specified sensor.
     * 
     * @param sensorId The well-known id of the sensor
     * @param sensorData The data of that sensor.
     */
    public boolean addSensorData(byte sensorId, double sensorData) {
        if (sensorId > SupportedSensors.SUPPORTED_SENSORS) {
            
            // Unsupported sensors will not be appended to the set of sensor values.
            return false;
        } else {
            this.data.put(sensorId, sensorData);
            return true;
        }
        
    }

    /**
     * Retrieve the total number of sensors being stored.
     *
     * @return The size of the sensors set
     */
    public int getSize() {
        return this.data.size();
    }

    /**
     * Empty all sensors added to this packet.
     */
    public void clear() {
        this.data.clear();
    }

    /**
     * Retrieves the sensor data of the specified sensor.
     * 
     * @param sensorId The well-known id of the sensor
     * 
     * @return The sensor data
     */
    public double getSensorData(byte sensorId) {
        if (this.data.containsKey(sensorId)) {
            return this.data.get(sensorId);
        } else {

            // Return -1 if the sensor id is not contained in the sensor values set.
            return -1.0;
        } 
    }

    /**
     * Initialize the sensors value to -1.
     */
    private void initialize() {
        this.data.put(SupportedSensors.AIR_HUMIDITY, -1.0);
        this.data.put(SupportedSensors.AIR_TEMPERATURE, - 1.0);
        this.data.put(SupportedSensors.LIGHT_INTENSITY, -1.0);
        this.data.put(SupportedSensors.SOIL_MOISTURE, -1.0);
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

        for (int i = 0; payload[i] != 0; i += sensorDataLength) {
            this.data.put(payload[i], super.getDouble(payload, i + 1));
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
    
    /**
     * Override the default toString() implementation to provide a list of
     * sensor names and their values.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("");

        // Represent all sensors in one line of pairs: their name followed by their vlaue.
        this.data.keySet().stream()
            .forEach((sensor) -> 
                builder.append(String.format("%s: %f ",
                     SupportedSensors.getStringRepresentation(sensor), this.data.get(sensor)
                ))
            );

        return builder.toString();
    }
}