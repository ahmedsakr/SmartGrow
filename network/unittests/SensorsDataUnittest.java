package network.unittests;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import endpoint.sensors.SupportedSensors;
import network.core.packets.sensors.SensorsData;
import network.core.OpCodes;

/**
 * The following class hosts several unit tests to verify that the
 * functionality of SensorsData meets the necessary requirements.
 * 
 * @author Ahmed Sakr
 * @since November 18, 2019
 */
public class SensorsDataUnittest {

    /**
     * Test: Check that the SensorsData packet has the correct opcode.
     */
    @Test
    public void sensorsDataPacketHasTheRightOpcode() {
        SensorsData data = new SensorsData();

        // Check that the opcode of the SensorsData packet is the expected one
        assertEquals(OpCodes.SENSORS_DATA, data.getOpCode());
    }

    /**
     * Test: Check that the sensor values are initialized to negative one
     * (i.e., the default value)
     */
    @Test
    public void sensorsValuesAreInitializedToNegativeOne() {

        SensorsData data = new SensorsData();

        // Check that all sensors are initialized to -1
        assertEquals(0.0, data.getSensorData(SupportedSensors.LIGHT_INTENSITY), 0.001); 
        assertEquals(0.0, data.getSensorData(SupportedSensors.SOIL_MOISTURE), 0.001);
        assertEquals(0.0, data.getSensorData(SupportedSensors.AIR_TEMPERATURE), 0.001);
        assertEquals(0.0, data.getSensorData(SupportedSensors.AIR_HUMIDITY), 0.001);
    }

    /**
     * Test: Check that the sensor values can be updated to new values.
     */
    @Test
    public void sensorValuesCanBeUpdated() {
        SensorsData data = new SensorsData();

        // Override the sensor values.
        data.addSensorData(SupportedSensors.LIGHT_INTENSITY, 5.0);
        data.addSensorData(SupportedSensors.SOIL_MOISTURE, 6.0);
        data.addSensorData(SupportedSensors.AIR_TEMPERATURE, 7.0);
        data.addSensorData(SupportedSensors.AIR_HUMIDITY, 8.0);

        // Check that the sensor values were overriden correctly.
        assertEquals(5.0, data.getSensorData(SupportedSensors.LIGHT_INTENSITY), 0.001);
        assertEquals(6.0, data.getSensorData(SupportedSensors.SOIL_MOISTURE), 0.001);
        assertEquals(7.0, data.getSensorData(SupportedSensors.AIR_TEMPERATURE), 0.001);
        assertEquals(8.0, data.getSensorData(SupportedSensors.AIR_HUMIDITY), 0.001);
    }

    /**
     * Test: Check that when creating a SensorsData packet, all supported sensors
     * are automatically appended to the packet.
     */
    @Test
    public void sensorsDataAutomaticallyInsertsTheDefaultSensorsOnCreation() {
        SensorsData data = new SensorsData();
        
        // Check that the 4 default sensors are available in the data packet at creation.
        assertEquals(SupportedSensors.SUPPORTED_SENSORS, data.getSize()); 
    }

    /**
     * Test: Check that clearing the SensorsData packet will get rid of all stored sensor
     * values in the packet.
     */
    @Test
    public void sensorValuesCanBeCleared() {
        SensorsData data = new SensorsData();

        data.clear();

        // Check that the clear operation emptied the set of sensor values.
        assertEquals(0, data.getSize());
    }

    /**
     * Test: Check that the addSensorData() method does not add unsupported sensor values.
     */
    @Test
    public void unableToAddUnrecognizedSensorsToTheSensorsDataPacket() {
        SensorsData data = new SensorsData();

        // Try to add an unsupported sensor id to the sensor values set.
        assertEquals(false, data.addSensorData((byte)10, 5.0));

        // Confirm that the size of sensor values sets remains 4.
        assertEquals(SupportedSensors.SUPPORTED_SENSORS, data.getSize());
    }

    /**
     * Test: check that attempting to retrieve an unstored sensor value will
     * result in a -1.0 value return.
     */
    @Test
    public void attemptingToRetrieveSensorIdNotStoredWillReturnNegativeOne() {
        SensorsData data = new SensorsData();

        // Try to add an unsupported sensor id to the sensor values set.
        assertEquals(-1.0, data.getSensorData((byte)10), 0.001);
    }

    /**
     * Test: check that adding a sensor value will take up 9 bytes of space:
     * 1 byte for the sensor id + 8 bytes for the sensor value.
     */
    @Test
    public void addingASensorValueAddsNineBytesToPacket() {
        SensorsData data = new SensorsData();
        data.clear();
        
        // Compute the free space in the packet before adding a sensor value.
        data.compile();
        int freeBeforeAddition = data.getFreeSpace();
        
        // Add the sensor value
        assertEquals(true, data.addSensorData(SupportedSensors.LIGHT_INTENSITY, 5));
        
        // Compute the free space in the packet after adding a sensor value.
        data.compile();
        int freeAfterAddition = data.getFreeSpace();

        // The difference should be 9 bytes: 1 byte for the sensor id + 8 bytes for the sensor value
        assertEquals(9, freeBeforeAddition - freeAfterAddition);
    }
}