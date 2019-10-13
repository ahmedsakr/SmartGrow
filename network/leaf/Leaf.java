package network.leaf;

import java.io.IOException;
import java.net.SocketException;
import java.util.Random;

import network.Configuration;
import network.core.Packet;
import network.core.Transport;
import network.core.exceptions.CorruptPacketException;
import network.core.packets.LeafRegistration;

/**
 * A Leaf is a network node 
 */
public class Leaf extends Transport {

    private Identity identity;

    /**
     * Initialize a leaf node listening on the provided port.
     * 
     * @param port The port to listen on
     */
    public Leaf(Identity identity) throws SocketException {
        super(new Random().nextInt(0xFFFF));
        this.identity = identity;

        this.register();
    }

    private void register() {
        try {
            LeafRegistration registration = new LeafRegistration();
            registration.setIdentity(this.identity);
            registration.setDestination(Configuration.CPS_ADDRESS, Configuration.CPS_PORT);
            this.send(registration);
            Packet response = this.receive();
        } catch (IOException | CorruptPacketException ex) {
            System.err.printf("CRITICAL: failed to register with CPS: %s\n", ex);
        }
    }
}