package cps.database.tables;

import cps.database.DatabaseInfo;
import cps.database.exceptions.SmartgrowDatabaseException;
import cps.database.DatabaseController;
import endpoint.sensors.SupportedSensors;
import network.core.packets.sensors.SensorsData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String sql = String.format(
            "INSERT INTO %s (plant_id, time_taken, light_intensity, air_humidity, air_temperature, soil_moisture) " +
            "VALUES (%d, '%s', %f, %f, %f, %f);",
             DatabaseInfo.DATABASE_SENSORS_TABLE, 1, formatter.format(LocalDateTime.now()),
             data.getSensorData(SupportedSensors.LIGHT_INTENSITY),
             data.getSensorData(SupportedSensors.AIR_HUMIDITY),
             data.getSensorData(SupportedSensors.AIR_TEMPERATURE),
             data.getSensorData(SupportedSensors.SOIL_MOISTURE));

        // Update the database with the prepared SQL statement.
        this.database.update(sql);
    }

    /**
     * Retrieve the latest sensor data for the plant.
     *
     * @return A SensorsData packet containing the latest sensor data
     * @throws SmartgrowDatabaseException
     */
    public SensorsData getLatestSensorData() throws SmartgrowDatabaseException {
        String sql = String.format(
            "SELECT * from %s ORDER BY time_taken DESC LIMIT 1", DatabaseInfo.DATABASE_SENSORS_TABLE);

        // Update the database with the prepared SQL statement.
        ResultSet results = this.database.query(sql);
        SensorsData data = new SensorsData();

        try {
            
            // The plant data table returned nothing.
            if (!results.next()) {
                return data;
            }

            // Append the returned sensor data to the packet.
            data.addSensorData(SupportedSensors.AIR_HUMIDITY, results.getDouble("air_humidity"));
            data.addSensorData(SupportedSensors.AIR_TEMPERATURE, results.getDouble("air_temperature"));
            data.addSensorData(SupportedSensors.SOIL_MOISTURE, results.getDouble("soil_moisture"));
            data.addSensorData(SupportedSensors.LIGHT_INTENSITY, results.getDouble("light_intensity"));
        } catch (SQLException ex) {
            throw new SmartgrowDatabaseException("Error parsing SQL ResultSet: " + ex.getMessage());
        }

        return data;
    }
}