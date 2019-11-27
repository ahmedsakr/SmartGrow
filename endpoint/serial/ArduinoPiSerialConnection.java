/**
Code from: http://rxtx.qbang.org/wiki/index.php/Two_way_communcation_with_the_serial_port
 Edited by:
 @author Valerie Figuracion

gnu.io library from http://rxtx.qbang.org to replace javax.comm
 
/*RaspPi-Arduino Serial Communication
 *ArduinoPiSerialConnection.java on the RPi
 *SensorsData.ino on the Arduino
 **/

package endpoint.serial;

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
import network.leaf.Identity;
import network.leaf.Leaf;

public class ArduinoPiSerialConnection extends Thread {

	//Creates a logger instance for the class
	private SmartLog logger = new SmartLog(ArduinoPiSerialConnection.class.getName());

	//Initialize the leaf
	private Leaf leaf;
	
	public ArduinoPiSerialConnection(){ super("ArduinoPiSerialConnection"); }
		
	//Opens the connection between the Arduino and the Pi
	public void SerialConnection(String portName) throws Exception{
		
		this.leaf = new Leaf(Identity.PLANT_ENDPOINT);
		
		//Initializes the port for the serial connection.
		CommPortIdentifier portID = CommPortIdentifier.getPortIdentifier(portName);
		
		//Will determine if it is already in use or not
		if( portID.isCurrentlyOwned() ) {
			logger.error("Error: Port is currently in use");
		} 
		else {
			int timeout = 2000;
			//Opens the port of the serial connection (RPi or Arduino?)
			CommPort port = portID.open(this.getClass().getName(), timeout);
				
			//SerialPort is subclass of CommPort. Will initialize if CommPort is a SerialPort.
			if(port instanceof SerialPort) {
				//Masks CommPort into SerialPort
				SerialPort serialPort = (SerialPort) port; 
				
				//Initialize parameters for the SerialPort (on Arduino I think)
				serialPort.setSerialPortParams( 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE );
				
				//Initializes input stream for the serial port (so the arduino can send data to the Pi)
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

	//Serial reader allows the Pi to receive data from the arduino (hopefully)
	public static class SerialReader implements SerialPortEventListener {

		InputStream in;
		
		public SerialReader(InputStream in) { this.in = in; }

		//TODO: Receive information from the arduino 
		//ID (1 BYTE) DATA (8 BYTES)
		
		public void serialEvent(SerialPortEvent arg0){
			byte[] buffer = new byte[ 512 ];
			int len = -1;

			int bytesForData = 9;	
		}
		//end todo
	}
	
	//Run method of the thread
	@Override
	public void run(){
		
		while(true) {
			SensorsData data = new SensorsData();
			
			try {
				Thread.sleep(1000);
				data.clear();
				
				/*
				data.addSensorData(SupportedSensors.AIR_HUMIDITY, );
				data.addSensorData(SupportedSensors.AIR_TEMPERATURE, );
				data.addSensorData(SupportedSensors.LIGHT_INTENSITY, );
				data.addSensorData(SupportedSensors.SOIL_MOISTURE, );
				*/
				
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

	//Main method so it can do stuff
	public static void main(String[] args){
		try{
			//connection will ideally connect the pi and the arduino through the correct port
			ArduinoPiSerialConnection apsc = new ArduinoPiSerialConnection();
			apsc.SerialConnection("COM10");
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
