package network.leaf;

import java.io.IOException;
import java.net.SocketException;

import config.SmartGrowConfiguration;
import logging.SmartLog;
import network.branch.BroadcastHandler;
import network.core.NodeLocation;
import network.core.Packet;
import network.core.Transport;
import network.core.exceptions.CorruptPacketException;
import network.leaf.threads.BroadcastHandlerThread;
import network.leaf.threads.LeafRegistrationThread;

/**
 * A Leaf is a network endpoint that is a point of interest
 * for the SmartGrow system.
 * 
 * A leaf could be:
 * <p>
 * -    A plant endpoint: a node which provides sensory information on a plant
 * </p>
 * <p>
 * -    An android user: a mobile application that interacts with the SmartGrow system 
 *      by providing input and receiving sensory information.
 * </p>
 * 
 * Using this logical grouping, the Leaf entirely abstracts the
 * UDP networking logic by automatically registering with the
 * central processing server (CPS) and communicating with its
 * assigned branch.
 * 
 * @author Ahmed Sakr
 * @since October 10, 2019
 */
public class Leaf extends Transport {

    // The logging instance for this class.
    private static SmartLog logger = new SmartLog(Leaf.class.getName());

    // The worker responsible for registering us with the server.
    private LeafRegistrationThread worker;

    // The handler responsible for servicing broadcasts received on this leaf object.
    private BroadcastHandler broadcastHandler;

    // The worker responsible for processing broadcasts on behalf of this leaf.
    private BroadcastHandlerThread broadcastHandlerThread;

    // State information about this leaf instance
    private Identity identity;
    private boolean registered;

    /**
     * Create a Leaf with a provided identity.
     * 
     * @param identity The identity of this leaf (Android user or Plant endpoint)
     */
    public Leaf(Identity identity, int port) throws SocketException {
        super(new NodeLocation(SmartGrowConfiguration.CPS_ADDRESS, SmartGrowConfiguration.CPS_PORT), port);
        
        this.identity = identity;
        this.worker = new LeafRegistrationThread(this);
    }

    /**
     * Create a Leaf with a provided identity.
     * 
     * @param identity The identity of this leaf (Android user or Plant endpoint)
     */
    public Leaf(Identity identity) throws SocketException {
        super(new NodeLocation(SmartGrowConfiguration.CPS_ADDRESS, SmartGrowConfiguration.CPS_PORT));

        this.identity = identity;
        this.worker = new LeafRegistrationThread(this);
    }

    /**
     * Attach a broadcast handler for all packets received on this leaf that are broadcasts.
     *
     * @param handler The broadcast handler that will be called when broadcast packets are received.
     */
    public void attachBroadcastHandler(BroadcastHandler handler) {
        this.broadcastHandler = handler;
    }

    /**
     * Retrieve the registered broadcast handler for this leaf object.
     *
     * @return The registered broadcast handler, if it exists. Otherwise, null is returned.
     */
    public BroadcastHandler getBroadcastHandler() {
        return this.broadcastHandler;
    }

    /**
     * Retrieve the Leaf identity of this instance.
     *
     * @return  The LeafIdentity representation for this leaf
     */
    public Identity getIdentity() {
        return this.identity;
    }

    /**
     * Override the registration status of this leaf.
     *
     * @param registered The new registration status of this leaf.
     */
    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    /**
     * Checks if the leaf has registered with the central processing server.
     * 
     * @return  true    If the leaf has established a connection with the central
     *                  processing server
     *          false   Otherwise
     */
    public synchronized boolean isRegistered() {
        return this.registered;
    }

    /**
     * Block sending until registration with the central processing server is
     * complete.
     * 
     * @param packet The packet to send to the branch
     */
    @Override
    public void send(Packet packet) throws IOException {

        // By-pass these checks for the registration worker as this is only
        // intended to stop other callers from proceeding before registration
        // succeeds.
        if (!Thread.currentThread().equals(this.worker)) {
            this.waitForRegistration();

            // Return null if the leaf failed to register.
            if (!this.registered) {
                logger.warn("Unable to send packet because leaf is not registered with the cps");
                return;
            }

            // Override the destination address and port to the branch assigned to this leaf.
            packet.setDestination(this.getDestination());
        }

        super.send(packet);
    }

    /**
     * Block receiving until registration with the central processing server is
     * complete.
     * 
     * @return The verified packet received from the branch.
     */
    @Override
    public Packet receive() throws CorruptPacketException, IOException {

        // By-pass these checks for the registration worker as this is only
        // intended to stop other callers from proceeding before registration
        // succeeds.
        if (!Thread.currentThread().equals(this.worker)) {
            this.waitForRegistration();
            
            // Return null if the leaf failed to register.
            if (!this.registered) {
                logger.warn("Unable to receive packet because leaf is not registered with the cps");
                return null;
            }
        }

        Packet response = super.receive();

        /*
         * A broadcast packet being received means we got interrupted from our intended receive.
         * However, we can't ignore this packet and go back to receiving. We must pass off this
         * broadcast packet to the appropriate handler first.
         */
        if  (response.isBroadcast()) {
            if (this.getBroadcastHandler() != null) {
                synchronized (this.broadcastHandler) {

                    // Pass off the broadcast to the thread before waking it up.
                    this.broadcastHandlerThread.setBroadcastPacket(response);
                    
                    // Awake the thread so that it can process the broadcast, relinquishing us
                    // from this responsibility.
                    this.broadcastHandlerThread.notify();
                }
            }

            // Now that the broadcast processing has been delegated to someone else, we need to go
            // back into our receiving state to listen for our intended packet.
            return this.receive();
        }

        // Immediately return the response if it wasn't a broadcast. This means we got the packet
        // that we were waiting for.
        return response;
    }

    /*
     * Join the lock wait set if registration has still not completed.
     */
    private synchronized void waitForRegistration() {
        if (!this.registered) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
                logger.fatal("Interrupted while waiting for leaf registration");
            }
        }
    }
}