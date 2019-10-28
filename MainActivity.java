package com.example.smartgrow20;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import endpoint.sensors.SupportedSensors;
import java.io.IOException;

import network.core.Packet;
import network.core.exceptions.CorruptPacketException;
import network.core.packets.SensorsData;
import network.leaf.Leaf;

import static endpoint.sensors.SupportedSensors.AIR_HUMIDITY;
import static endpoint.sensors.SupportedSensors.AIR_TEMPERATURE;
import static endpoint.sensors.SupportedSensors.LIGHT_INTENSITY;
import static endpoint.sensors.SupportedSensors.SOIL_MOISTURE;

/**
 * Displays the sensors from the plant endpoints.
 *
 * @author Valerie Figuracion
 */

public class MainActivity extends AppCompatActivity {

    public Leaf leaf;
    public SensorsData sensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            Packet pkt = leaf.receive();
            SensorsData sd = ((SensorsData) pkt);
            
            //sensors.addSensorData(LIGHT_INTENSITY, ?? );
            //sensors.addSensorData(SOIL_MOISTURE, soil_data);
            //sensors.addSensorData(AIR_TEMPERATURE, temperature_data);
            //sensors.addSensorData(AIR_HUMIDITY, humidity_data);

            //Gets the values from the hash map using the sensorID as the key.
            double light_data = sensors.getSensorData(LIGHT_INTENSITY);
            double soil_data = sensors.getSensorData(SOIL_MOISTURE);
            double temperature_data = sensors.getSensorData(AIR_TEMPERATURE);
            double humidity_data = sensors.getSensorData(AIR_HUMIDITY);

            //The following corresponds with the strings.xml file
            String light = getString(R.string.lightIntensity, light_data);
            String soil = getString(R.string.soilMoisture, soil_data);
            String temp = getString(R.string.airTemperature, temperature_data);
            String humidity = getString(R.string.airHumidity, humidity_data);

        } catch(CorruptPacketException e){
            //log.error(e);
        } catch(IOException e){
            //log.error(e);
        }
    }
}
