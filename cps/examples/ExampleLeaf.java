package cps.examples;

import java.net.SocketException;

import network.core.packets.SensorsData;
import network.leaf.Identity;
import network.leaf.Leaf;

public class ExampleLeaf {

    public static void main(String[] args) {
        try {
            Leaf leaf = new Leaf(Identity.PLANT_ENDPOINT, 5555);
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }
}