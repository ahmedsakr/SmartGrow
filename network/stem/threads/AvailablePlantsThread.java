package network.stem.threads;

import java.io.IOException;

import logging.SmartLog;
import network.core.packets.plants.AvailablePlants;
import network.stem.ActivePlantEndpoints;
import network.stem.Stem;

/**
 * AvailablePlantsThread is responsible for routinely
 * broadcasting to android users all available plants in the
 * SmartGrow system.
 * 
 * @author Ahmed Sakr
 * @since November 30, 2019
 */
public class AvailablePlantsThread extends Thread {
    
    // The logger instance for this class
    private static SmartLog logger = new SmartLog(AvailablePlantsThread.class.getName());

    // This thread is set to run every 5 seconds.
    private static int BROADCASTING_INTERVAL = 5000;

    // The stem that we will be servicing.
    private Stem stem;

    /**
     * Initialize the AvailablePlants broadcasting thread and immediately start it.
     *
     * @param stem The stem being serviced
     */
    public AvailablePlantsThread(Stem stem) {
        super("AvailablePlantsThread");
        this.stem = stem;

        // Immeditately start the thread.
        this.start();
    }

    /**
     * The entry point for the AvailablePlants thread.
     */
    @Override
    public void run() {
        try {

            ActivePlantEndpoints activePlants = new ActivePlantEndpoints(this.stem);
            while (true) {

                // Sleep for the required interval.
                Thread.sleep(BROADCASTING_INTERVAL);

                // Broadcast the available plants to all android users.
                AvailablePlants availablePlants = activePlants.getActivePlants();
                this.stem.getAndroidUsers().broadcast(availablePlants);
            }
        } catch (InterruptedException ex) {
            logger.error(Thread.currentThread().getName() + " has been unexpectedly interrupted!");
        } catch (IOException ex) {
            logger.error(Thread.currentThread().getName() + " encountered an error: " + ex.getMessage());
        }
    }
}