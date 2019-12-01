package com.example.smartgrow;

import android.graphics.Color;
import android.widget.TextView;

import java.io.IOException;
import java.net.SocketException;
import java.util.Locale;

import endpoint.sensors.SupportedSensors;
import logging.SmartLog;
import network.branch.BroadcastHandler;
import network.core.Packet;
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
public class RealtimeSensors extends Thread implements BroadcastHandler {

    // The logger for this class
    private SmartLog logger = new SmartLog(RealtimeSensors.class.getName());

    // The leaf for servicing the android app
    private Leaf leaf;

    private MainActivity activity;

    // The data to be updated on the screen
    private TextView temperature, humidity, soilMoisture, lightIntensity;

    // The animation color to trigger when updating the values
    private int animationColor = Color.rgb(216, 27, 96);

    public RealtimeSensors(MainActivity activity) {
        super("RealtimeSensors");
        this.activity = activity;

        this.temperature = activity.findViewById(R.id.temperature_sensor_data);
        this.humidity = activity.findViewById(R.id.humidity_sensor_data);
        this.lightIntensity = activity.findViewById(R.id.light_intensity_data);
        this.soilMoisture = activity.findViewById(R.id.soil_moisture_data);

        // Immediately start the sensors thread
        this.start();
    }

    /**
     * Handle any broadcasts received from the server.
     * Invoked in the broadcast handler thread of the leaf.
     *
     * @param packet The broadcast packet to process
     */
    @Override
    public void handleBroadcast(Packet packet) {

    }

    /*
     * Request the latest sensors information from the server.
     */
    private SensorsData getSensorsData() throws IOException, CorruptPacketException {

        SensorsData data = null;
        RequestSensors request = new RequestSensors();

        /*
         * Keep sending a request for SensorsData until the server responds to us.
         */
        while (data == null) {
            // Send the request for fresh sensor data to the server.
            this.leaf.send(request);

            // Wait for the response from the server.
            data = (SensorsData) leaf.receiveWithTimeout();
        }

        return data;
    }

    /*
     * Update the sensors shown on the screen using the provided sensors.
     */
    private void updateSensorsOnScreen(SensorsData data) {


        // Update the sensor information on the screen
        activity.runOnUiThread(() -> {
            temperature.setText(String.format(Locale.CANADA, "%.1f",
                    data.getSensorData(SupportedSensors.AIR_TEMPERATURE)));
            humidity.setText(String.format(Locale.CANADA, "%.1f",
                    data.getSensorData(SupportedSensors.AIR_HUMIDITY)));
            lightIntensity.setText(String.format(Locale.CANADA, "%.1f",
                    data.getSensorData(SupportedSensors.LIGHT_INTENSITY)));
            soilMoisture.setText(String.format(Locale.CANADA, "%.1f",
                    data.getSensorData(SupportedSensors.SOIL_MOISTURE)));
        });
    }

    /*
     * Play a quick font change animation on the sensor data to visually signal that
     * they updated.
     */
    private void playUpdatedSensorAnimation() {
        int originalColor = this.temperature.getCurrentTextColor();

        activity.runOnUiThread(() -> {
            this.temperature.setTextColor(this.animationColor);
            this.humidity.setTextColor(this.animationColor);
            this.lightIntensity.setTextColor(this.animationColor);
            this.soilMoisture.setTextColor(this.animationColor);
        });

        try {
            Thread.sleep(400);
        } catch (InterruptedException ex) {
            logger.warn("Interrupted while sleeping for animation");
        }

        activity.runOnUiThread(() -> {
            this.temperature.setTextColor(originalColor);
            this.humidity.setTextColor(originalColor);
            this.lightIntensity.setTextColor(originalColor);
            this.soilMoisture.setTextColor(originalColor);
        });
    }

    @Override
    public void run() {

        // Initialize the leaf that will be used in communicating with the server
        try {
            this.leaf = new Leaf(Identity.ANDROID_USER);
        } catch (SocketException ex) {

            // Prematurely terminate if we can't get hold of a leaf object.
            logger.fatal("Unable to initialize the SmartGrow leaf: " + ex.getMessage());
            return;
        }

        // Set this object as the broadcast handler for the leaf.
        this.leaf.attachBroadcastHandler(this);

        try {

            while (true) {

                // Retrieve the latest sensors from the server
                SensorsData data = getSensorsData();

                // Update the sensors information on the interface to the latest ones
                updateSensorsOnScreen(data);

                // Play a quick animation to visually convey that sensors updated
                playUpdatedSensorAnimation();

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
