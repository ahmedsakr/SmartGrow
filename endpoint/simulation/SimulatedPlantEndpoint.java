package endpoint.simulation;

import java.io.IOException;
import java.net.SocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import endpoint.sensors.SupportedSensors;
import network.core.packets.SensorsData;
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

    private static Logger logger = LogManager.getLogger(SimulatedPlantEndpoint.class);

    // The sinusoidal maxiumum parameters
    private final int SIMULATION_AMPLITUDE_MAX = 3;
    private final int SIMULATION_CYCLE_MAX = 30;

    // Start values for the sensors
    private final double AIR_HUMIDITY_START = 25;
    private final double AIR_TEMPERATURE_START = 25;
    private final double SOIL_MOISTURE_START = 25;
    private final double LIGHT_INTENSITY_START = 25;

    // The UDP abstraction that we will use to communicate with the server.
    private Leaf leaf;

    // The start of the simulation used to calculate simulated values.
    private long startTime;

    // How much the simiulated data can vary from the start value
    private double simulationAmplitude;

    // How long a full cycle of simulation takes (in seconds)
    private double simulationCycle;

    /**
     * Construct the simulated plant endpoint
     */
    public SimulatedPlantEndpoint() throws SocketException {
        super("SimulatedPlantEndpoint");
        this.leaf = new Leaf(Identity.PLANT_ENDPOINT);

        // Initialize random values for simulation amplitude and cycle time.
        this.simulationAmplitude = Math.random() * this.SIMULATION_AMPLITUDE_MAX;
        this.simulationCycle = Math.random() * this.SIMULATION_CYCLE_MAX;
        
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
    public double getSimulatedValue(double startValue) {
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
                data.addSensorData(SupportedSensors.AIR_HUMIDITY, getSimulatedValue(this.AIR_HUMIDITY_START));
                data.addSensorData(SupportedSensors.AIR_TEMPERATURE, getSimulatedValue(this.AIR_TEMPERATURE_START));
                data.addSensorData(SupportedSensors.LIGHT_INTENSITY, getSimulatedValue(this.LIGHT_INTENSITY_START));
                data.addSensorData(SupportedSensors.SOIL_MOISTURE, getSimulatedValue(this.SOIL_MOISTURE_START));

                logger.info("Sending SensorsData packet: " + data);
                // Dispatch the packet to the server.
                this.leaf.send(data);
            } catch (InterruptedException | IOException ex) {
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