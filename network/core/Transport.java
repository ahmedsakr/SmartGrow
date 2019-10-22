package network.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import network.core.exceptions.CorruptPacketException;
import network.core.exceptions.TransportInterruptedException;

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

    private static Logger logger = LogManager.getLogger(Transport.class);

    private DatagramSocket socket;
    private NodeLocation destination;

    /**
     * Allows the caller to allow the DatagramSocket implementation to choose
     * a random port.
     */
    public Transport(NodeLocation destination) throws SocketException {
        this.socket = new DatagramSocket();
        this.setDestination(destination);

        logger.debug("Initialized on port #" + this.socket.getLocalPort());
    }

    /**
     * Allows the caller to specify what port they want to listen on.
     * 
     * @param port The port desired to listen on.
     */
    public Transport(NodeLocation destination, int port) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.setDestination(destination);

        logger.debug("Initialized on port #" + this.socket.getLocalPort());
    }

    /**
     * Retrieve the local port that this transport is listening on.
     * 
     * @return The 16-bit port number being listened on.
     */
    public int getPort() {
        return this.socket.getLocalPort();
    }

    /**
     * Assign the destination address of this transport instance.
     * 
     * @param location The IPv4 location of the destination node.
     */
    public void setDestination(NodeLocation location) {
        this.destination = location;
    }

    /**
     * Retrieve the location of the destination node.
     * 
     * @return The IPv4 representation of the destination address
     */
    public NodeLocation getDestination() {
        return this.destination;
    }

    /**
     * Dispatch a packet to its intended destination.
     * @param packet
     * @throws IOException
     */
    public void send(Packet packet) throws TransportInterruptedException, IOException {
        
        // Finalize the packet payload.
        packet.compile();

        // Override the destination of the packet based on the transport destination
        packet.setDestination(this.destination.getIpAddress(), this.destination.getPort());

        // Dispatch the packet.
        try {
            this.socket.send(packet.getDatagram());
        } catch (SocketException ex) {
            throw new TransportInterruptedException("Transport thread interrupted");
        }
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
    public Packet receive() throws CorruptPacketException, TransportInterruptedException, IOException {

        byte[] payload = new byte[Packet.PACKET_SIZE];
        DatagramPacket udpPacket = new DatagramPacket(payload, payload.length);

        // Receive the payload on this socket.
        try {
            this.socket.receive(udpPacket);
        } catch (SocketException ex) {
            throw new TransportInterruptedException("Transport thread interrupted");
        }

        // Transform the UDP payload into a Packet object.
        Packet packet = Packet.fromPayload(payload);

        try {
            packet.setDestination(udpPacket.getAddress().getHostAddress(), udpPacket.getPort());
        } catch (UnknownHostException ex) {

            // This happening means there is a failure in the physical network.
            // We must terminate.
            logger.fatal("CRITICAL: Unable to resolve hostname: " + ex);
            System.exit(1);
        }

        return packet;
    }

    /**
     * Close the DatagramSocket, disallowing it from receiving or sending more messages.
     */
    public void close() {
        this.socket.close();
    }
}