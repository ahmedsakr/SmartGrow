package network.core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Transport extends DatagramSocket {

    public Transport(int port) throws SocketException {
        super(port);
    }
}