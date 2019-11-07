package com.example.sg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import endpoint.sensors.SupportedSensors;

import java.io.IOException;
import java.net.SocketException;

import network.core.Packet;
import network.core.exceptions.CorruptPacketException;
import network.core.packets.SensorsData;
import network.leaf.Identity;
import network.leaf.Leaf;

/**
 * Displays the sensors from the plant endpoints.
 *
 * @author Valerie Figuracion
 */

public class MainActivity extends AppCompatActivity {

    public Leaf leaf;
    //public SensorsData sensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            leaf = new Leaf(Identity.ANDROID_USER);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        try {
            Packet pkt = leaf.receive();
            SensorsData sd = ((SensorsData) pkt);

            double lightData = sd.getSensorData(SupportedSensors.LIGHT_INTENSITY);
            double soilData = sd.getSensorData(SupportedSensors.SOIL_MOISTURE);
            double temperatureData = sd.getSensorData(SupportedSensors.AIR_TEMPERATURE);
            double humidityData = sd.getSensorData(SupportedSensors.AIR_HUMIDITY);

            TextView light_Data = findViewById(R.id.light_data);
            TextView soil_Data = findViewById(R.id.soil_data);
            TextView temperature_Data = findViewById(R.id.temperature_data);
            TextView humidity_Data = findViewById(R.id.humidity_data);

            String LD = Double.toString(lightData);
            String SD = Double.toString(soilData);
            String TD = Double.toString(temperatureData);
            String HD = Double.toString(humidityData);

            light_Data.setText("Current reading: " + LD);
            soil_Data.setText("Current reading: " + SD);
            temperature_Data.setText("Current reading: " + TD);
            humidity_Data.setText("Current reading: " + HD);

        } catch (CorruptPacketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
