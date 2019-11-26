/**
Code from: http://rxtx.qbang.org/wiki/index.php/Two_way_communcation_with_the_serial_port
 Edited by:
 @author Valerie Figuracion

 
/*RaspPi-Arduino Serial Communication
 *ArduinoPiSerialConnection.java on the RPi
 *SensorsData.ino on the Arduino
 **/

package endpoint.serial;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import network.*;
import logging.SmartLog;

public class ArduinoPiSerialConnection extends Thread {

	//Creates a logger instance for the class
	private SmartLog logger = new SmartLog(ArduinoPiSerialConnection.class.getName());

	public ArduinoPiSerialConnection(){ super(); }
		
	//Opens the connection between the Arduino and the Pi
	public void SerialConnection(String portName) throws Exception{
		
		//Initializes the port for the serial connection.
		CommPortIdentifier portID = CommPortIdentifier.getPortIdentifier(portName);
		
		//Will determine if it is already in use or not
		if( portID.isCurrentlyOwned() ) {
			logger.error("Error: Port is currently in use");
		}else {
			int timeout = 2000;
			//Opens the port of the serial connection (RPi or Arduino?)
			CommPort port = portID.open(this.getClass().getName(), timeout);
		}
		
		//SerialPort is subclass of CommPort. Will initialize if CommPort is a SerialPort.
		if(port instanceof SerialPort) {
			SerialPort serialPort = (SerialPort)port; //Masks CommPort into SerialPort
			//Initialize parameters for the SerialPort (on Arduino I think)
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE );
			
			//Initializes input stream for the serial port (so the arduino can send data to the Pi)
			InputStream in = serialPort.getInputStream();
			
			//SerialReader thread starts
			SerialReader sr = new SerialReader(in);
			new Thread(sr).start();

			//Notifies if there's data available
			serialPort.addEventListener(new SerialReader(in));
            serialPort.notifyOnDataAvailable(true);
			
		}else{
			//gives an error if port is not serial
			logger.error("Error: Must be a serial port");
		}
	}

	//Serial reader allows the Pi to receive data from the arduino (hopefully)
	public static class SerialReader implements Runnable {

		InputStream in;
		
		//Method
		public SerialReader(InputStream in) {
			this.in = in;
		}

		//(Does this need to be overriden? I think there's already a method I can call. I have to find it though)
		public void run() {
			byte[] buffer = new byte[ 512 ];
			int len = -1;
			try {
				while( ( len = this.in.read( buffer ) ) > -1 ) {
				  System.out.print( new String( buffer, 0, len ) );
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//Run method of the thread
	@Override
	public void run(){
		
		//Initialize Leaf (port?)
		try {
			Leaf leaf = new Leaf(Identity.PLANT_ENDPOINT);

			//Get information from SerialReader (I think?)
			while(true){
				SensorsData data = (SensorsData) leaf.receive();
				SerialReader.sleep(1000);
			}
		} catch (SocketException | IOException | CorruptPacketException |InterruptedException e) {
			logger.fatal(e);
		} finally {
			//close leaf
			leaf.close();
		}
	}

	//Main method so it can do stuff
	public static void main(String[] args){
		try{
			//connection will ideally connect the pi and the arduino through the correct port
			ArduinoPiSerialConnection apsc = new ArduinoPiSerialConnection();
			apsc.SerialConnection("COM10");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

	//TODO: Recieve info from Arduino
	//TODO: Package sensor info

