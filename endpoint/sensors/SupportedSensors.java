package endpoint.sensors;

/**
 * SupportedSensors provides a list of all sensors recognized within the
 * SmartGrow network. Plant endpoints should use these well-known ids for the
 * sensors when transmitting sensory information.
 * 
 * @author Ahmed Sakr
 * @since October 21, 2019
 */
public class SupportedSensors {

    // Provided by photo resistor
    public static final byte LIGHT_INTENSITY = 1;

    // Water content in soil
    public static final byte SOIL_MOISTURE = 2;

    // Temperature of environment air
    public static final byte AIR_TEMPERATURE = 3;

    // Water content in the environment air
    public static final byte AIR_HUMIDITY = 4;

    /**
     * Provide a string representation for the sensors based on the
     * given id.
     *
     * @param sensorId The id of the sensor
     * @return The name of the sensor value
     */
    public static String getStringRepresentation(byte sensorId) {
        switch (sensorId) {
            case LIGHT_INTENSITY:
                return "Light Intensity";
            case SOIL_MOISTURE:
                return "Soil Moisture";
            case AIR_TEMPERATURE:
                return "Temperature";
            case AIR_HUMIDITY:
                return "Humidity";
            default:
                return "Unknown Sensor";
        }
    }
}