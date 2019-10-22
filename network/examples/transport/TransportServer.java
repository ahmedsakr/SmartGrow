package network.examples;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;

import network.core.exceptions.CorruptPacketException;
import network.core.Packet;
import network.core.Transport;
import network.core.packets.LeafRegistration;
import network.leaf.Identity;

public class TransportServer {

    public static void main(String[] args) throws InterruptedException{
        try {
            Transport transport = new Transport(3333);
            Thread.sleep(10000);
            LeafRegistration packet = (LeafRegistration) transport.receive();

            System.out.println("Packet received!");
            System.out.printf("Sender IP: %s\n", packet.getAddress());
            System.out.printf("Sender Port: %d\n", packet.getPort());

            packet = (LeafRegistration) transport.receive();

            System.out.println("Packet received!");
            System.out.printf("Sender IP: %s\n", packet.getAddress());
            System.out.printf("Sender Port: %d\n", packet.getPort());

            packet = (LeafRegistration) transport.receive();

            System.out.println("Packet received!");
            System.out.printf("Sender IP: %s\n", packet.getAddress());
            System.out.printf("Sender Port: %d\n", packet.getPort());

            packet = (LeafRegistration) transport.receive();

            System.out.println("Packet received!");
            System.out.printf("Sender IP: %s\n", packet.getAddress());
            System.out.printf("Sender Port: %d\n", packet.getPort());
        } catch (IOException | CorruptPacketException ex) {
            System.out.println(ex);
        }
    }
}