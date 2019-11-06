package cps.database.wrappers;

import cps.database.DatabaseInfo;
import cps.database.exceptions.SmartgrowDatabaseException;
import cps.database.DatabaseController;
import endpoint.sensors.SupportedSensors;
import network.core.packets.SensorsData;

/**
 * PlantData is a DatabaseController wrapper for the plant_data
 * table. It provides methods for inserting and retrieving
 * sensors data.
 * 
 * @author Ahmed Sakr
 * @since November 6, 2019
 */
public class PlantData {

    // Object representation for manipulating the database.
    private DatabaseController database;

    /**
     * Initialize the wrapper for the plant_data table in the database.
     *
     * @param database Object reppresentation for manipulating the database.
     */
    public PlantData(DatabaseController database) {
        this.database = database;
    }
    
    /**
     * Insert the sensors data into the plant_data table.
     *
     * @param data The SensorsData packet retrieved from a plant endpoint.
     * @throws SmartgrowDatabaseException
     */
    public void insertSensorsData(SensorsData data) throws SmartgrowDatabaseException {
        String sql = String.format(
            "INSERT INTO %s (plant_id, light_intensity, air_humidity, air_temperature, soil_moisture) " +
            "VALUES (%d, %f, %f, %f ,%f);",
             DatabaseInfo.DATABASE_SENSORS_TABLE, 1,
             data.getSensorData(SupportedSensors.LIGHT_INTENSITY),
             data.getSensorData(SupportedSensors.AIR_HUMIDITY),
             data.getSensorData(SupportedSensors.AIR_TEMPERATURE),
             data.getSensorData(SupportedSensors.SOIL_MOISTURE));

        // Update the database with the prepared SQL statement.
        this.database.update(sql);
    }
}