package network.leaf.threads;

import network.core.Packet;
import network.leaf.Leaf;
import logging.SmartLog;

/**
 * BroadcastHandlerThread is created to fill the need for offloading broadcast
 * processing overhead off from the leaf and onto some other thread. The leaf-invoking
 * thread should not waste valuable time on broadcasts when it could be processing whatever
 * it was working on.
 * 
 * As a result, the leaf would simply provide this thread with the broadcast packets and
 * move on. We will then pick that broadcast packet and invoke the broadcast handler on our
 * own time.
 * 
 * @author Ahmed Sakr
 * @since November 30, 2019
 */
public class BroadcastHandlerThread extends Thread {

    // The logger instance for this class.
    private static SmartLog logger = new SmartLog(BroadcastHandlerThread.class.getName());

    // The broadcast packet that we will be operating on.
    private Packet broadcastPacket;

    // The leaf that we are managing broadcast packets for.
    private Leaf leaf;

    /**
     * Initialize the BroadcastHandler thread and start its operation.
     * @param leaf
     */
    public BroadcastHandlerThread(Leaf leaf) {
        super(String.format("BroadcastHandler-%d", leaf.getPort()));
        this.leaf = leaf;

        // Immediately start the thread.
        this.start();
    }

    /**
     * Assign the broadcast packet that this thread should process.
     *
     * @param broadcast The packet that should be processed
     */
    public synchronized void setBroadcastPacket(Packet broadcast) {
        this.broadcastPacket = broadcast;
    }

    /**
     * The entry point for the BroadcastHandler thread.
     */
    @Override
    public void run() {

        try {
            while (true) {

                synchronized (this) {

                    /*
                     * This is a race condition guard.
                     *
                     * It is unlikely but possible that the leaf will hold the lock before we
                     * are able to wait. If this happens, the leaf will invoke notify but no one
                     * would be waiting to listen. This could lead to the broadcast packet never
                     * being processed.
                     * 
                     * The workaround is to check if the broadcastPacket is not null. If it is not null
                     * before we waited, that means the leaf got to it before we did. So we don't
                     * need to wait.
                     */
                    if (this.broadcastPacket == null) {

                        // wait until the leaf wakes us up when it receives a broadcast packet.
                        this.wait();
                    }

                    // While the leaf will not call us if there is no broadcast handler available,
                    // it doesn't hurt to check.
                    if (this.leaf.getBroadcastHandler() != null ) {

                        // The broadcast packet should have been loaded into our object state by
                        // the leaf. Invoke the broadcast handler with the packet.
                        this.leaf.getBroadcastHandler().handleBroadcast(this.broadcastPacket);
                    }

                    // It is imperative that we set the packet to null because this state variable
                    // is used to check if we have work to do.
                    this.broadcastPacket = null;
                }
            }
        } catch (InterruptedException ex) {
            logger.warn(Thread.currentThread().getName() + " has been unexpectedly interrupted!");
        }

    }
}