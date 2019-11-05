package network.branch;

import java.io.IOException;
import java.lang.Runnable;
import java.net.SocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import network.core.NodeLocation;
import network.core.Packet;
import network.core.Transport;
import network.core.exceptions.CorruptPacketException;
import network.core.exceptions.TransportInterruptedException;
import network.core.packets.RegistrationResponse;

/**
 * DedicatedLeafServicer is an exclusive thread spawned by branches
 * for servicing the requests of a leaf in the SmartGrow system.
 * 
 * @author Ahmed Sakr
 * @since October 18, 2019
 */
public class DedicatedLeafServicer extends Transport implements Runnable {

    private static Logger logger = LogManager.getLogger(DedicatedLeafServicer.class);

    // The thread that this instance runs in.
    private Thread serviceThread;
    private boolean ready;
    private long lastReceivedTime;

    /**
     * Initialize the state of the DedicatedLeafServicer thread.
     * 
     * @param leafAddress The IPv4 address (and port) of the leaf
     */
    public DedicatedLeafServicer(NodeLocation leafAddress) throws SocketException {
        super(leafAddress);

        // Begin tracking when the last packet was received from the leaf.
        this.lastReceivedTime = System.currentTimeMillis();

        // Start the servicer once initialization is complete
        this.serviceThread = new Thread(this, "LeafServicer-" + leafAddress.getPort());
        this.serviceThread.start();
    }

    /**
     * Retrieve the time (in milliseconds) when this thread has last received a packet
     * from the leaf.
     *
     * @return Epoch time (in milliseconds) of the last time a packet was received.
     */
    public long getLastReceivedTime() {
        return this.lastReceivedTime;
    }

    /**
     * Forward the broadcast message to the destination.
     * 
     * @param broadcast The broadcast payload message
     */
    public synchronized void forwardBroadcast(Packet broadcast) throws IOException {
        
        // Any broadcasting attempts must be blocked until the servicer has had the chance
        // to respond to the leaf with a RegistrationResponse.
        while (!this.ready) {
            try {
                logger.debug("Waiting for " + this.serviceThread.getName() + " to be ready");
                this.wait();
            } catch (InterruptedException ex) {
                logger.error("CRITICAL: Interrupted while waiting for servicer to get ready.");
                return;
            }
        }

        this.send(broadcast);
    }

    /**
     * Stop the leaf servicer by interrupting it, causing it to terminate.
     */
    public void stop() {
        this.serviceThread.interrupt();
        this.close();
    }

    /**
     * The entry point of the thread.
     */
    @Override
    public void run() {

        // Inform the leaf that they have been registered
        RegistrationResponse response = new RegistrationResponse();
        response.setStatus(true);
        response.setRegistrationDetails("OK");
        
        try {
            this.send(response);
            logger.debug("Sent successful RegistrationRequest packet to leaf");
            
            // Set the state of this servicer to ready now that we have forwarded the
            // registration response to the client.
            synchronized (this) {
                this.ready = true;
                this.notifyAll();
            }

            // Begin the receive-respond loop of the servicer.
            //
            // For now, all it does is receive but it will not respond.
            // Once we have the implementation of the rest of the system,
            // this hsould be changed to fulfill leaf requests
            while (true) {
                Packet request = this.receive();
                logger.info("received packet from leaf: " + request);

                synchronized (this) {
                    this.lastReceivedTime = System.currentTimeMillis();
                }
            }
        } catch (TransportInterruptedException ex) {
            logger.info("Ending servicer for " + this.getDestination());
        } catch (CorruptPacketException ex) {
            logger.error("Received payload is invalid: " + ex);
        } catch (IOException ex) {
            logger.error("CRITICAL: failed network i/o when servicing " + this.getDestination());
        }
    }

}