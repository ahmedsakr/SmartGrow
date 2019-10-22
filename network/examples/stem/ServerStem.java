package network.examples.stem;

import java.net.SocketException;

import network.Configuration;
import network.stem.Stem;

public class ServerStem {

    public static void main(String[] args) {
        try {
            Stem stem = new Stem(Configuration.CPS_PORT);
        } catch (SocketException ex) {
            System.err.println("Unable to create stem");
        }
    }
}