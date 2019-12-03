package endpoint.simulation;

import java.io.IOException;
import java.net.SocketException;

import endpoint.sensors.SupportedSensors;
import logging.SmartLog;
import network.core.exceptions.CorruptPacketException;
import network.core.packets.Acknowledgement;
import network.core.packets.sensors.SensorsData;
import network.leaf.Identity;
import network.leaf.Leaf;

/**
 * SimulatedPlantEndpoint provides a simulation of sensors collection
 * and dispatching to the server. It aims to provide an accurate representation
 * of a real physical plant endpoint.
 * 
 * @author Ahmed Sakr
 * @since November 02, 2019
 */
public class SimulatedPlantEndpoint extends Thread {

    // The logger instance for this class
    private static SmartLog logger = new SmartLog(SimulatedPlantEndpoint.class.getName());

    // The sinusoidal maxiumum parameters
    private final int SIMULATION_AMPLITUDE_MAX = 3;
    private final int SIMULATION_CYCLE_MAX = 30;

    // Start values for the sensors
    private final double AIR_HUMIDITY_START = 60;
    private final double AIR_TEMPERATURE_START = 25;
    private final double SOIL_MOISTURE_START = 40;
    private final double LIGHT_INTENSITY_START = 700;

    // The UDP abstraction that we will use to communicate with the server.
    private Leaf leaf;

    // The start of the simulation used to calculate simulated values.
    private long startTime;

    // How much the simiulated data can vary from the start value
    private double simulationAmplitude;

    // How long a full cycle of simulation takes (in seconds)
    private double simulationCycle;

    // The values for this simulation
    private double air_humidity, air_temperature, soil_moisture, light_intensity;

    /**
     * Construct the simulated plant endpoint
     */
    public SimulatedPlantEndpoint() throws SocketException {
        super("SimulatedPlantEndpoint");

        // Initialize the leaf for the simulation
        this.leaf = new Leaf(Identity.PLANT_ENDPOINT);

        // Initialize random values for simulation amplitude and cycle time.
        this.simulationAmplitude = Math.random() * this.SIMULATION_AMPLITUDE_MAX;
        this.simulationCycle = Math.random() * this.SIMULATION_CYCLE_MAX;

        // Generate random starting leaves for this simulation
        this.air_temperature = this.AIR_TEMPERATURE_START * Math.max(Math.random(), Math.random());
        this.air_humidity = this.AIR_HUMIDITY_START * Math.max(Math.random(), Math.random());
        this.soil_moisture = this.SOIL_MOISTURE_START * Math.max(Math.random(), Math.random());
        this.light_intensity = this.LIGHT_INTENSITY_START * Math.max(Math.random(), Math.random());
        
        // Immediately start the simulation of a plant endpoint.
        this.start();
    }

    /**
     * Simulate a value based on the run time and the current position of the chosen
     * sinusoidal function.
     *
     * @param startValue The start value of the sensor
     * @return A simulated value based on an arbirtary sinusoidal function.
     */
    private double getSimulatedValue(double startValue) {
        double timeDifferenceSeconds = ((double)System.currentTimeMillis() - this.startTime) / 1000;
        double sinValue = Math.sin(2 * Math.PI * (timeDifferenceSeconds / this.simulationCycle));

        return startValue + (this.simulationAmplitude * sinValue);
    }

    /**
     * The entry point of the simulation.
     */
    @Override
    public void run() {

        logger.info("Starting plant endpoint simulation");
        this.startTime = System.currentTimeMillis();

        while (true) {

            SensorsData data = new SensorsData();

            try {
                
                // Sleep for a second (the interval between each sensor collection)
                Thread.sleep(1000);

                // Clear the existing values from the packet.
                data.clear();
                
                // Add new simulated values to the SensorsData packet.
                data.addSensorData(SupportedSensors.AIR_HUMIDITY, getSimulatedValue(this.air_humidity));
                data.addSensorData(SupportedSensors.AIR_TEMPERATURE, getSimulatedValue(this.air_temperature));
                data.addSensorData(SupportedSensors.LIGHT_INTENSITY, getSimulatedValue(this.light_intensity));
                data.addSensorData(SupportedSensors.SOIL_MOISTURE, getSimulatedValue(this.soil_moisture));

                logger.info("Sending SensorsData packet: " + data);

                // Dispatch the packet to the server.
                this.leaf.send(data);

                // Wait for an acknowledgement packet for the sensor packet we sent.
                if (!(this.leaf.receive() instanceof Acknowledgement)) {
                    logger.fatal("Received a packet that was not acknowledgement!");
                    System.exit(1);
                } else {
                    logger.info("Received acknowledgement packet from server!");
                }
                
            } catch (InterruptedException | CorruptPacketException | IOException ex) {
                logger.fatal("Plant endpoint simulation encountered error: " + ex.getMessage());
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        try {
            new SimulatedPlantEndpoint();
        } catch (SocketException ex) {
            logger.fatal("Plant endpoint simulation encountered error: " + ex.getMessage());
            System.exit(1);
        }
    }
}