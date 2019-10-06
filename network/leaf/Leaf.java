package network.leaf;

import java.net.SocketException;
import java.util.Random;

import network.core.Transport;

/**
 * A Leaf is a network node 
 */
public class Leaf extends Transport {

    /**
     * Initialize a leaf node listening on the provided port.
     * 
     * @param port The port to listen on
     */
    public Leaf() throws SocketException {
        super(new Random().nextInt());
    }
}