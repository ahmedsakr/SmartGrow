package network.leaf;

import java.io.IOException;
import java.net.SocketException;
import java.util.Random;

import network.Configuration;
import network.core.Packet;
import network.core.Transport;
import network.core.exceptions.CorruptPacketException;
import network.core.packets.LeafRegistration;
import network.core.packets.RegistrationResponse;

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

    // Synchronizing access to the socket and state variables
    private Object lock;

    // State information about this leaf instance
    private Identity identity;
    private boolean registered;

    // The assigned branch's networking address and port
    private String branchAddress;
    private int branchPort;

    /**
     * Checks if the leaf has registered with the central processing server.
     * 
     * @return  true    If the leaf has established a connection with the central
     *                  processing server
     *          false   Otherwise
     */
    public boolean isRegistered() {
        synchronized (this.lock) {
            return this.registered;
        }
    }

    /**
     * Block sending until registration with the central processing server is
     * complete.
     * 
     * @param packet The packet to send to the branch
     */
    @Override
    public void send(Packet packet) throws IOException {
        this.waitForRegistration();

        // Override the destination address and port to the branch assigned to this leaf.
        packet.setDestination(this.branchAddress, this.branchPort);

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
        this.waitForRegistration();

        return super.receive();
    }

    /**
     * Create a Leaf with a provided identity.
     * 
     * @param identity The identity of this leaf (Android user or Plant endpoint)
     */
    public Leaf(Identity identity) throws SocketException {
        super(new Random().nextInt(0xFFFF));
        this.identity = identity;
        this.lock = new Object();

        this.register();
    }


    /*
     * Join the lock wait set if registration has still not completed. 
     */
    private void waitForRegistration() {
        synchronized (this.lock) {
            if (!this.registered) {
                try {

                    // Once we exit the wait set, it is assumed that registration is now
                    // complete.
                    // TODO: Check if registration failed?
                    this.lock.wait();
                } catch (InterruptedException ex) {
                    System.err.println("CRITICAL: interrupted while waiting for leaf registration");
                }
            }
        }
    }

    /*
     * Spawn a thread to register this leaf with the central processing server. 
     */
    private void register() {
        new Thread(() -> {
            try {
                LeafRegistration registration = new LeafRegistration();
                registration.setIdentity(this.identity);
                registration.setDestination(Configuration.CPS_ADDRESS, Configuration.CPS_PORT);

                synchronized (this.lock) {
                    super.send(registration);
                    RegistrationResponse response = (RegistrationResponse) super.receive();

                    if (response.getStatus()) {
                        this.registered = true;
                    } else {

                        // TODO: how to recover from a failure to register with the server?
                        this.registered = false;
                    }
                    
                    this.branchAddress = response.getAddress();
                    this.branchPort = response.getPort();
                    this.lock.notifyAll();
                }

            } catch (IOException | CorruptPacketException ex) {
                System.err.printf("CRITICAL: failed to register with CPS: %s\n", ex);
            }
        }).start();
    }
}