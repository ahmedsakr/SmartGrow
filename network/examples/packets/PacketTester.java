package network.examples;

import java.net.DatagramPacket;
import java.net.UnknownHostException;

import network.core.exceptions.CorruptPacketException;
import network.core.Packet;
import network.core.packets.LeafRegistration;
import network.leaf.Identity;

public class PacketTester {

    public static void main(String[] args) {
        try {
            LeafRegistration register = new LeafRegistration();
            try {
                register.setDestination("localhost", 5555);
            } catch (UnknownHostException ex) {
                System.out.println(ex);
            }
            register.setIdentity(Identity.ANDROID_USER);
            register.compile();

            LeafRegistration register2 = (LeafRegistration) Packet.fromPayload(register.getDatagram().getData());
            register2.compile();

            DatagramPacket dgram = register2.getDatagram();
            byte[] data = dgram.getData();

            System.out.print("[");
            for (int i = 0; i < data.length; i++) {
                System.out.printf("%d, ", data[i]);
            }
    
            System.out.println("]");
        } catch (CorruptPacketException ex) {
            System.out.println(ex);
        }
        
    }
}