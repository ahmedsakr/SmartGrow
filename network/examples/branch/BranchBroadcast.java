package network.examples.branch;

import network.core.NodeLocation;

import network.core.Transport;

import java.io.IOException;
import java.net.SocketException;

import endpoint.sensors.SupportedSensors;
import network.branch.Branch;
import network.core.packets.RegistrationResponse;
import network.core.packets.SensorsData;

public class BranchBroadcast {

    public static void main(String[] args) {
        Branch branch = new Branch();
        try {
            branch.addLeaf(new NodeLocation("localhost", 4444));
            branch.addLeaf(new NodeLocation("localhost", 5555));

            SensorsData response = new SensorsData();
            response.addSensorData(SupportedSensors.AIR_HUMIDITY, 15.1);
            response.addSensorData(SupportedSensors.AIR_TEMPERATURE, 23.7);
            response.addSensorData(SupportedSensors.LIGHT_INTENSITY, 45.6);
            response.addSensorData(SupportedSensors.SOIL_MOISTURE, 82.1);

            branch.broadcast(response);
            branch.removeLeaf(new NodeLocation("localhost", 4444));
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }
}