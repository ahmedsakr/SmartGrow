package network.leaf;

import java.io.IOException;
import java.net.SocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import network.Configuration;
import network.core.NodeLocation;
import network.core.Packet;
import network.core.Transport;
import network.core.exceptions.CorruptPacketException;
import network.core.packets.LeafRegistration;
import network.core.packets.RegistrationResponse;
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

    private static Logger logger = LogManager.getLogger(Leaf.class);

    // The worker responsible for registering us with the server.
    private LeafRegistrationThread worker;

    // State information about this leaf instance
    private Identity identity;
    private boolean registered;

    // The assigned branch's networking address and port
    private NodeLocation branchLocation;

    /**
     * Create a Leaf with a provided identity.
     * 
     * @param identity The identity of this leaf (Android user or Plant endpoint)
     */
    public Leaf(Identity identity, int port) throws SocketException {
        super(new NodeLocation(Configuration.CPS_ADDRESS, Configuration.CPS_PORT), port);
        
        this.identity = identity;
        this.worker = new LeafRegistrationThread(this);
    }

    /**
     * Create a Leaf with a provided identity.
     * 
     * @param identity The identity of this leaf (Android user or Plant endpoint)
     */
    public Leaf(Identity identity) throws SocketException {
        super(new NodeLocation(Configuration.CPS_ADDRESS, Configuration.CPS_PORT));

        this.identity = identity;
        this.worker = new LeafRegistrationThread(this);
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
     * Override the branch location to a new destination.
     * 
     * @param location The IPv4 address and port of the branch.
     */
    public synchronized void setBranchLocation(NodeLocation location) {
        this.branchLocation = location;
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
            packet.setDestination(this.branchLocation.getIpAddress(), this.branchLocation.getPort());
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

        return super.receive();
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