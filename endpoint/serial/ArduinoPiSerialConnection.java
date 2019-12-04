package endpoint.serial;

/**
Code from: http://rxtx.qbang.org/wiki/index.php/Two_way_communcation_with_the_serial_port
Edited by: Valerie Figuracion

 * RaspPi-Arduino Serial Communication
 **/

 

import endpoint.sensors.SupportedSensors;

import javax.comm.CommPortIdentifier;
import javax.comm.CommPort;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import logging.SmartLog;

import network.core.exceptions.CorruptPacketException;
import network.core.packets.Acknowledgement;
import network.core.packets.sensors.SensorsData;
import network.core.OpCodes;
import network.leaf.Identity;
import network.leaf.Leaf;

public class ArduinoPiSerialConnection extends Thread {

	//Creates a logger instance for the class
	private SmartLog logger = new SmartLog(ArduinoPiSerialConnection.class.getName());

	//Initialize the leaf
	private Leaf leaf;

	public ArduinoPiSerialConnection(){ super("ArduinoPiSerialConnection"); }

	//Opens the connection between the Arduino and the Pi
	public void SerialConnection() throws Exception{

		this.leaf = new Leaf(Identity.PLANT_ENDPOINT);

		//Initializes the port for the serial connection.
		CommPortIdentifier portID = CommPortIdentifier.getPortIdentifier("/dev/ttyACM0");

		//Will determine if it is already in use or not
		if( portID.isCurrentlyOwned() ) {
			logger.error("Error: Port is currently in use");
		}
		else {
			int timeout = 2000;

			//Opens the port of the serial connection
			CommPort port = portID.open(this.getClass().getName(), timeout);

			//SerialPort is subclass of CommPort. Will initialize if CommPort is a SerialPort.
			if(port instanceof SerialPort) {

				//Masks CommPort into SerialPort
				SerialPort serialPort = (SerialPort) port;

				//Initialize parameters for the SerialPort (on Arduino I think)
				serialPort.setSerialPortParams( 115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE );

				//Initializes input stream for the serial port
				InputStream in = serialPort.getInputStream();

				//SerialReader thread starts
				 (new Thread(new ArduinoPiSerialConnection())).start();

				//Will listen to the port to see if there is any changes and notifies if there's data available
				serialPort.addEventListener(new SerialReader(in));
				serialPort.notifyOnDataAvailable(true);
			}
			else{
				//Gives an error if port is not serial
				logger.error("Error: Must be a serial port");
			}
		}
	}

	/*
	 * Serial reader allows the Pi to receive data from the arduino (hopefully)
	 */
	public static class SerialReader implements SerialPortEventListener {

		//Creates a logger instance for the class
		private SmartLog logger = new SmartLog(SerialReader.class.getName());

		private Leaf leaf;
		private InputStream in; //port is already open
		private double[] msg = new double[512];

		public SerialReader(InputStream in) { this.in = in; }

		//Will handle the data that arrives in the port
		public void serialEvent(SerialPortEvent arg0){
			int arduinoData;

			while(true) {
				SensorsData data = new SensorsData();

				try {
					Thread.sleep(1000);
					data.clear();

					//If there is an input this code will execute
					try{
						int len = 0;

						//Will read as long as it doesn't reach the end of the packet
						while((arduinoData = in.read()) > -1){

							//If the OpCode is not that of SensorsData then the input is not from the arduino
							if ((double)in.read() != OpCodes.SENSORS_DATA){
								logger.error("Packet is not from the arduino.");
								break; //Does not continue to read the input
							}
							if (len > 512){ break; }

							//Data from the arduino is translated into bytes
							//and added to the buffer array
							msg[len++] = (double)arduinoData;
						}
					} catch (IOException e){
						e.printStackTrace();
						System.exit(-1);
					}

					//Byte arrangement corresponds with the arrangement in SensorData.ino
					//Hardcoded for now
					int lightData = (int)msg[2];
					int soilData = (int)msg[11];
					int tempData = (int)msg[20];
					int humidData = (int)msg[29];

					data.addSensorData(SupportedSensors.AIR_HUMIDITY, humidData);
					data.addSensorData(SupportedSensors.AIR_TEMPERATURE, tempData);
					data.addSensorData(SupportedSensors.LIGHT_INTENSITY, lightData);
					data.addSensorData(SupportedSensors.SOIL_MOISTURE, soilData);

					// Dispatch the packet to the server.
					this.leaf.send(data);

					// Wait for an acknowledgement packet for the sensor packet we sent.
					if (!(this.leaf.receive() instanceof Acknowledgement)) {
						logger.fatal("Received a packet that was not acknowledgement!");
						System.exit(1);
					} else {
						logger.info("Received acknowledgement packet from server!");
					}
				} catch (IOException | CorruptPacketException |InterruptedException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}

	//Main method so it can do stuff
	public static void main(String args[]){
		try{
			//connection will ideally connect the pi and the arduino through the correct port
			ArduinoPiSerialConnection apsc = new ArduinoPiSerialConnection();
			apsc.SerialConnection(); //Top right USB Port
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}

