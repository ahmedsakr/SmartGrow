package network.examples.branch;

import java.io.IOException;

import endpoint.sensors.SupportedSensors;
import network.core.Packet;
import network.core.exceptions.CorruptPacketException;
import network.core.packets.RegistrationResponse;
import network.core.packets.SensorsData;
import network.leaf.Identity;
import network.leaf.Leaf;

public class Client {

    public static void main(String[] args) {
        try {
            Leaf leaf = new Leaf(Identity.ANDROID_USER, 4444);
            
            SensorsData packet = (SensorsData) leaf.receive();
            packet.compile();
            System.out.println(packet.getSensorData(SupportedSensors.SOIL_MOISTURE));

        } catch (CorruptPacketException | IOException ex) { 
            System.out.println(ex);
        }
    }
}