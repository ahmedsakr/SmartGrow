package network.branch;

import java.lang.Runnable;

import network.core.NodeLocation;
import network.core.Packet;
import network.core.Transport;
import network.core.packets.RegistrationResponse;

/**
 * DedicatedLeafServicer is an exclusive thread spawned by branches
 * for servicing the requests of a leaf in the SmartGrow system.
 * 
 * @author Ahmed Sakr
 * @since October 18, 2019
 */
public class DedicatedLeafServicer extends Transport implements Runnable {

    private Thread serviceThread;

    /**
     * Initialize the state of the DedicatedLeafServicer thread.
     * 
     * @param leafAddress The IPv4 address (and port) of the leaf
     */
    public DedicatedLeafServicer(NodeLocation leafAddress) {
        super(leafAddress);

        // Start the servicer once initialization is complete
        this.serviceThread = new Thread(this).start();

    }

    /**
     * The entry point of the thread.
     */
    @Override
    public void run() {

        // Inform the leaf that they have been registered
        RegistrationResponse response = new RegistrationResponse();
        response.setStatus(true);
        this.send(response);
        
        // Begin the receive-respond loop of the servicer.
        //
        // For now, all it does is receive but it will not respond.
        // Once we have the implementation of the rest of the system,
        // this hsould be changed to fulfill leaf requests
        while (true) {
            Packet request = this.receive();
        }
        
    }

}