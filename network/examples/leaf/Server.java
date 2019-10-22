package network.examples.leaf;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;

import network.core.exceptions.CorruptPacketException;
import network.Configuration;
import network.core.Packet;
import network.core.Transport;
import network.core.packets.LeafRegistration;
import network.core.packets.RegistrationResponse;
import network.leaf.Identity;
import network.leaf.Leaf;

public class Server {

    public static void main(String[] args) {
        try {
            Transport server = new Transport(Configuration.CPS_PORT);
            LeafRegistration registration = (LeafRegistration) server.receive();

            RegistrationResponse response = new RegistrationResponse();
            response.setStatus(true);
            response.setDestination(registration.getAddress(), registration.getPort());
            server.send(response);

            RegistrationResponse response1 = (RegistrationResponse) server.receive();
            System.out.println(response1.isRegistered());
        } catch (IOException | CorruptPacketException ex) { 
            System.out.println(ex);
        }
    }
}