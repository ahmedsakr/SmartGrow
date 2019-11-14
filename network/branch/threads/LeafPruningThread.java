package network.branch.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import logging.SmartLog;
import network.branch.Branch;
import network.branch.threads.DedicatedLeafServicer;

/**
 * LeafPruningThread is spawned when a branch is created to
 * clean up leaves that have stopped communicating.
 * 
 * @author Ahmed Sakr
 * @since October 27, 2019
 */
public class LeafPruningThread extends Thread {

    // The logger instance for this class.
    private static SmartLog logger = new SmartLog(LeafPruningThread.class.getName());

    // The period at which this thread kicks off to check for pruning validity.
    public static final int LEAF_PRUNING_INTERVAL = 2500;

    // The time that elapses after no packet has been received when pruning should kick in.
    public static final int LEAF_PRUNING_THRESHOLD = 5000;

    // The branch that we are pruning dead leaves from.
    private Branch branch;

    public LeafPruningThread(Branch branch) {
        super(String.format("%sBranch-PruningThread", branch));
        this.branch = branch;

        // Immediately start the pruning thread.
        this.start();
    }

    @Override
    public void run() {
        while (true) {

            ArrayList<DedicatedLeafServicer> servicers = null;

            try {

                // Sleep the interval time specified for pruning
                Thread.sleep(LEAF_PRUNING_INTERVAL);
                
                // This whole operation is a critical section.
                // No one should be able to modify the list of servicers until
                // we are done.
                synchronized (this.branch) {
                    servicers = this.branch.getServicers();
                    long currentTime = System.currentTimeMillis();

                    // Check and collect all servicers whose leaf has not communicated past the
                    // acceptable threshold.
                    List<DedicatedLeafServicer> deadLeaves = 
                        servicers.stream()
                            .filter((servicer) -> currentTime - servicer.getLastReceivedTime() >= LEAF_PRUNING_THRESHOLD)
                            .collect(Collectors.toList());

                    for (DedicatedLeafServicer servicer : deadLeaves) {
                        logger.info("Stopping servicer for " + servicer.getDestination());

                        // Interrupt and close the transport layer for the servicer.
                        servicer.stop();
                        servicers.remove(servicer);
                    }
                }
            } catch (InterruptedException ex) {
                logger.error("Interrupted while sleeping. Pruning for branch " + this.branch + " is disabled.");
            }
        }
    }
}