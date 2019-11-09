package com.example.sg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import endpoint.sensors.SupportedSensors;

import java.io.IOException;
import java.net.SocketException;

import network.core.Packet;
import network.core.exceptions.CorruptPacketException;
import network.leaf.Identity;
import network.leaf.Leaf;
import network.core.packets.sensors.*;

/**
 * Displays the sensors from the plant endpoints.
 *
 * @author Valerie Figuracion
 */

public class MainActivity extends AppCompatActivity {

    public Leaf leaf;
    public RequestSensors requestSensors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestInfo();
        sensorInfo();
    }

    protected void requestInfo(){
        new Thread(() -> requestSensors = new RequestSensors()).start();
    }

    protected void sensorInfo(){

        //Initialize a leaf for the user.
        try {
            leaf = new Leaf(Identity.ANDROID_USER);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        try {
            //Initialize a leaf for receiving the sensor information.
            Packet pkt = leaf.receive();
            SensorsData sd = ((SensorsData) pkt);

            //Obtains the area where the data will be shown.
            TextView light_Data = findViewById(R.id.light_data);
            TextView soil_Data = findViewById(R.id.soil_data);
            TextView temperature_Data = findViewById(R.id.temperature_data);
            TextView humidity_Data = findViewById(R.id.humidity_data);

            String light_reading = "Current reading: " + sd.getSensorData(SupportedSensors.LIGHT_INTENSITY);
            String soil_reading = "Current reading: " + sd.getSensorData(SupportedSensors.SOIL_MOISTURE);
            String temp_reading = "Current reading: " + sd.getSensorData(SupportedSensors.AIR_TEMPERATURE);
            String humid_reading = "Current reading: " + sd.getSensorData(SupportedSensors.AIR_HUMIDITY);

            light_Data.setText(light_reading);
            soil_Data.setText(soil_reading);
            temperature_Data.setText(temp_reading);
            humidity_Data.setText(humid_reading);

        } catch (CorruptPacketException | IOException e) {
            e.printStackTrace();
        }
    }
}