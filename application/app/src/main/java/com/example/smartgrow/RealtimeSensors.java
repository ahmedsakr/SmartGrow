package com.example.smartgrow;

import android.widget.TextView;

import java.io.IOException;
import java.net.SocketException;
import java.util.Locale;

import endpoint.sensors.SupportedSensors;
import logging.SmartLog;
import network.core.exceptions.CorruptPacketException;
import network.core.packets.sensors.RequestSensors;
import network.core.packets.sensors.SensorsData;
import network.leaf.Identity;
import network.leaf.Leaf;

/**
 * RealtimeSensors is responsible for establishing a connection with the server
 * and retrieving the sensors that are displayed on the application.
 *
 * @author Ahmed Sakr
 * @since November 23, 2019
 */
public class RealtimeSensors extends Thread {

    private SmartLog logger = new SmartLog(RealtimeSensors.class.getName());

    private MainActivity activity;

    public RealtimeSensors(MainActivity activity) {
        super("RealtimeSensors");
        this.activity = activity;
        this.start();
    }

    @Override
    public void run() {

        // Initialize a connection in the SmartGrow system.
        Leaf leaf;

        try {
            leaf = new Leaf(Identity.ANDROID_USER);
        } catch (SocketException ex) {

            // Prematurely terminate if we can't get hold of a leaf object.
            logger.fatal("Unable to initialize the SmartGrow leaf: " + ex.getMessage());
            return;
        }


        RequestSensors request = new RequestSensors();
        TextView temperature = activity.findViewById(R.id.textView2);
        TextView airHumidity = activity.findViewById(R.id.textView4);
        TextView lightIntensity = activity.findViewById(R.id.textView6);
        TextView soilMoisture = activity.findViewById(R.id.textView8);
        try {
            while (true) {

                // Send the request for fresh sensor data to the server.
                leaf.send(request);

                // Wait for the response from the server.
                SensorsData data = (SensorsData) leaf.receive();

                // Update the sensor information on the screen
                activity.runOnUiThread(() -> {
                    temperature.setText(String.format(Locale.CANADA, "%.1f", data.getSensorData(SupportedSensors.AIR_TEMPERATURE)));
                    airHumidity.setText(String.format(Locale.CANADA, "%.1f", data.getSensorData(SupportedSensors.AIR_HUMIDITY)));
                    lightIntensity.setText(String.format(Locale.CANADA, "%.1f", data.getSensorData(SupportedSensors.LIGHT_INTENSITY)));
                    soilMoisture.setText(String.format(Locale.CANADA, "%.1f", data.getSensorData(SupportedSensors.SOIL_MOISTURE)));
                });

                // Sleep for a second before requesting another fresh batch of sensors.
                Thread.sleep(1000);
            }
        } catch (IOException | CorruptPacketException ex) {

            // Inability to perform network operations or receiving incorrect packets is
            // grounds for exiting this thread.
            logger.error("Encountered exception during operation: " + ex.getMessage());
        } catch (InterruptedException ex) {

            // Interruption is our signal to close this thread.
            logger.debug("Exiting the RealtimeSensors due to normal interruption.");
        } finally {

            // Close the leaf before exiting the thread to relinquish the network resource.
            leaf.close();
        }
    }
}
