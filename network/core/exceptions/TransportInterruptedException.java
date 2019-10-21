package network.core.exceptions;

import java.net.SocketException;

/**
 * TransportInterruptedException encompasses the network exception
 * where a transport has been prematurely ejected from its send / receive
 * blocked state due to the socket closing.
 * 
 * @author Ahmed Sakr
 * @since October 21, 2019
 */
public class TransportInterruptedException extends SocketException {

    public TransportInterruptedException(String message) {
        super(message);
    }
}