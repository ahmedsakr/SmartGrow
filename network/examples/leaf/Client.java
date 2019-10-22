package network.examples.leaf;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;

import network.core.exceptions.CorruptPacketException;
import network.core.Packet;
import network.core.packets.LeafRegistration;
import network.core.packets.RegistrationResponse;
import network.leaf.Identity;
import network.leaf.Leaf;

public class Client {

    public static void main(String[] args) {
        try {
            Leaf leaf = new Leaf(Identity.ANDROID_USER);
            
            RegistrationResponse response = new RegistrationResponse();
            response.setStatus(false);
            leaf.send(response);

        } catch (IOException ex) { 
            System.out.println(ex);
        }
    }
}