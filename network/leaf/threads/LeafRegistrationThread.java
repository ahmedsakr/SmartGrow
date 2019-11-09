package network.leaf.threads;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import network.core.NodeLocation;
import network.core.Packet;
import network.core.exceptions.CorruptPacketException;
import network.core.packets.GenericError;
import network.core.packets.registration.LeafRegistration;
import network.core.packets.registration.RegistrationResponse;
import network.leaf.Leaf;

/**
 * LeafRegistrationThread is a worker spawned upon creation of a
 * Leaf object to register the leaf with the central processing
 * server.
 * 
 * @author Ahmed Sakr
 * @since October 27, 2019
 */
public class LeafRegistrationThread extends Thread {

    private static Logger logger = LogManager.getLogger(LeafRegistrationThread.class);

    // Maximum amount of registration attempts before giving up.
    private final int REGISTRATION_ATTEMPTS = 3;

    // The leaf that we want to register with the server.
    private Leaf leaf;

    public LeafRegistrationThread(Leaf leaf) {
        super(String.format("LeafRegistration-%d", leaf.getPort()));
        this.leaf = leaf;

        // Immediately begin the worker to register the leaf.
        this.start();
    }

   /**
     * Attempt to register with the central processing server with multiple
     * attempts if necessary.
     * 
     * @return    A packet sent from the server
     */
    private Packet attemptRegistration() throws IOException, CorruptPacketException {

        // Initialize the LeafRegistration request packet with out identity.
        LeafRegistration registration = new LeafRegistration();
        registration.setIdentity(this.leaf.getIdentity());
        
        Packet response = null;
        for (int i = 0; i < REGISTRATION_ATTEMPTS; i++) {

            // Request registration by sending the LeafRegistration packet
            logger.info("Sending LeafRegistration packet");
            leaf.send(registration);

            // Wait for a response from the server
            response = leaf.receiveWithTimeout();
            if (response != null) {
                break;
            }

            // Try again to register.
            logger.info("Timed out with no response from server.");
        }

        return response;
    }

    /**
     * Starting point of the worker thread.
     */
    @Override
    public void run() {
        synchronized (this.leaf) {
            try {

                Packet response = this.attemptRegistration();

                if (response == null) {

                    // The maximum attempts to register have been exhausted. We give up now.
                    logger.fatal("No response after " + REGISTRATION_ATTEMPTS + " attempts.");
                } else {

                    if (response instanceof GenericError) {

                        // The server did not admit us on grounds of a serious error
                        logger.fatal("Failed to register, received GenericError: " + response);
                    } else if (response instanceof RegistrationResponse) {

                        RegistrationResponse registration = (RegistrationResponse) response;

                        if (!registration.isRegistered()) {
                            
                            // The server still did not admit us by not setting the registered status to true
                            logger.fatal("Failed to register with server response: " +
                                registration.getRegistrationDetails());
                        } else {

                            logger.info("Successfully registered with server response: " +
                                registration.getRegistrationDetails());

                            // The response should have been sent from a dedicated branch socket
                            // created for us. We must save it as this will be our communication
                            // point moving forward.
                            this.leaf.setDestination(new NodeLocation(response.getAddress(), response.getPort()));

                            // Set the leaf registration status to true so that the send() and receive() methods
                            // on the leaf no longer move the caller into the wait set.
                            this.leaf.setRegistered(true);
                        }
                    }
                }

            } catch (IOException | CorruptPacketException ex) {

                // Internal or packet error preventing us from successfully registering.
                logger.fatal("failed to register with CPS: " + ex);
            } finally {

                // Before we exit, we should wake up everyone waiting on us.
                this.leaf.notifyAll();
            }
        }
    }
}