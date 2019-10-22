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

public class TransportClient {

    public static void main(String[] args) {
        try {
            Transport transport = new Transport(5555);
            LeafRegistration register = new LeafRegistration();
            register.setDestination("localhost", 3333);
            register.setIdentity(Identity.ANDROID_USER);

            transport.send(register);
        } catch (IOException ex) { 
            System.out.println(ex);
        }
    }
}