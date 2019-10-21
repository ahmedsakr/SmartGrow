package network.branch;

import java.io.IOException;
import java.lang.Runnable;
import java.net.SocketException;

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

    // The thread that this instance runs in.
    private Thread serviceThread;

    /**
     * Initialize the state of the DedicatedLeafServicer thread.
     * 
     * @param leafAddress The IPv4 address (and port) of the leaf
     */
    public DedicatedLeafServicer(NodeLocation leafAddress) throws SocketException {
        super(leafAddress);

        // Start the servicer once initialization is complete
        this.serviceThread = new Thread(this);
        this.serviceThread.start();
    }

    /**
     * Forward the broadcast message to the destination.
     * 
     * @param broadcast The broadcast payload message
     */
    public void forwardBroadcast(Packet broadcast) throws IOException {
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
        
        try {
            this.send(response);
            
            // Begin the receive-respond loop of the servicer.
            //
            // For now, all it does is receive but it will not respond.
            // Once we have the implementation of the rest of the system,
            // this hsould be changed to fulfill leaf requests
            while (true) {
                Packet request = this.receive();
            }
        } catch (TransportInterruptedException ex) {
            System.out.printf("Ending servicer for %s\n", this.getDestination());
        } catch (CorruptPacketException ex) {
            System.err.printf("Received payload is invalid: %s\n", ex);
        } catch (IOException ex) {
            System.err.printf("CRITICAL: failed network i/o when servicing %s\n", this.getDestination());
        }
    }

}