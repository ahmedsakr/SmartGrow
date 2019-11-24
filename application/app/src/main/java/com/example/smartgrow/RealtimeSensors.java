package com.example.smartgrow;

import android.widget.TextView;

import java.io.IOException;
import java.net.SocketException;
import java.util.Locale;

import endpoint.sensors.SupportedSensors;
import network.core.exceptions.CorruptPacketException;
import network.core.packets.sensors.RequestSensors;
import network.core.packets.sensors.SensorsData;
import network.leaf.Identity;
import network.leaf.Leaf;

/**
 * RealtimeSensors provides
 */
public class RealtimeSensors extends Thread {

    private MainActivity activity;
    private Leaf leaf;

    public RealtimeSensors(MainActivity activity) {
        super("RealtimeSensors");
        this.activity = activity;
        this.start();
    }

    @Override
    public void run() {
        try {
            this.leaf = new Leaf(Identity.ANDROID_USER);
        } catch (SocketException ex) {
            ex.printStackTrace();
        }


        RequestSensors request = new RequestSensors();
        TextView temperature = activity.findViewById(R.id.textView2);
        TextView airHumidity = activity.findViewById(R.id.textView4);
        TextView lightIntensity = activity.findViewById(R.id.textView6);
        TextView soilMoisture = activity.findViewById(R.id.textView8);
        while (true) {
            try {

                this.leaf.send(request);

                SensorsData data = (SensorsData) this.leaf.receive();

                activity.runOnUiThread(() -> {
                    temperature.setText(String.format(Locale.CANADA, "%.1f", data.getSensorData(SupportedSensors.AIR_TEMPERATURE)));
                    airHumidity.setText(String.format(Locale.CANADA,"%.1f", data.getSensorData(SupportedSensors.AIR_HUMIDITY)));
                    lightIntensity.setText(String.format(Locale.CANADA,"%.1f", data.getSensorData(SupportedSensors.LIGHT_INTENSITY)));
                    soilMoisture.setText(String.format(Locale.CANADA,"%.1f", data.getSensorData(SupportedSensors.SOIL_MOISTURE)));
                });

                Thread.sleep(1000);

            } catch (IOException | InterruptedException | CorruptPacketException ex) {
                ex.printStackTrace();
            }
        }

    }
}
