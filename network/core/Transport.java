package network.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import network.core.exceptions.CorruptPacketException;

/**
 * Transport is developed to abstract UDP intricacies away
 * from the workflow. Using Transport, All ends in the 
 * SmartGrow network will be capable of easily setting up
 * a connection and start communicating with other ends.
 * 
 * @author Ahmed Sakr
 * @since October 10, 2019
 */
public class Transport {

    private DatagramSocket socket;

    /**
     * Allows the caller to allow the DatagramSocket implementation to choose
     * a random port.
     */
    public Transport() throws SocketException {
        this.socket = new DatagramSocket();
    }

    /**
     * Allows the caller to specify what port they want to listen on.
     * 
     * @param port The port desired to listen on.
     */
    public Transport(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
    }

    /**
     * Dispatch a packet to its intended destination.
     * @param packet
     * @throws IOException
     */
    public void send(Packet packet) throws IOException {
        
        // Finalize the packet payload.
        packet.compile();

        // Dispatch the packet.
        this.socket.send(packet.getDatagram());
    }

    /**
     * Receive a UDP packet and transform it into a SmartGrow Packet.
     * 
     * @return A Packet interpretation of the payload
     * 
     * @throws CorruptPacketException The payload failed verification
     * 
     * @see network.core.Packet
     */
    public Packet receive() throws CorruptPacketException, IOException {

        byte[] payload = new byte[Packet.PACKET_SIZE];
        DatagramPacket udpPacket = new DatagramPacket(payload, payload.length);

        // Receive and transform the UDP payload into a Packet object.
        this.socket.receive(udpPacket);
        Packet packet = Packet.fromPayload(payload);

        try {
            packet.setDestination(udpPacket.getAddress().getHostAddress(), udpPacket.getPort());
        } catch (UnknownHostException ex) {

            // This happening means there is a failure in the physical network.
            // We must terminate.
            System.err.println("CRITICAL: Unable to resolve hostname: " + ex);
            System.exit(1);
        }

        return packet;
    }
}