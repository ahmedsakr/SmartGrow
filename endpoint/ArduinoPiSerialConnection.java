/*
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://www.pi4j.com/
 Code made by:
 https://github.com/Pi4J/pi4j/tree/master/pi4j-example/src/main/java/SerialExample.java

 Edited by:
 @author Valerie Figuracion

 
/*RaspPi-Arduino Serial Communication
 *ArduinoPiSerialConnection.java on the RaspPi
 *SensorsData.ino on the Arduino
 */

package endpoint;

import com.pi4j.io.serial.*;
import com.pi4j.util.Console;

import java.io.IOException;
import java.io.InputStream;

import javax.CommPort;

import network*;
import logging.SmartLog;


public class ArduinoPiSerialConnection extends Thread {

	//logger instance for the class
	private SmartLog logger = new SmartLog(ArduinoPiSerialConnection.class.getName());

	public ArduinoPiSerialConnection (Packet packet) throws Exception{
		this.packet = packet;

		CommPortIdentifier portID = CommPortIdentifier.getPortIdentifier( "/dev/ttyAMA0" );
		CommPort port = portID.open( this.getClass().getName(), 1000 );

		if( port instanceof SerialPort ) {
		SerialPort serialPort = ( SerialPort )port;
		serialPort.setSerialPortParams( 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE );
		
		InputStream in = serialPort.getInputStream();
		serialPort.notifyOnDataAvailable( true );

		( new Thread( new SerialReader( in ) ) ).start();
		}
	}

	public static class SerialReader implements Runnable {

		InputStream in;

		public SerialReader( InputStream in ) {
		  this.in = in;
		}

		public void run() {
		  byte[] buffer = new byte[ 1024 ];
		  int len = -1;
		  try {
			while( ( len = this.in.read( buffer ) ) > -1 ) {
			  System.out.print( new String( buffer, 0, len ) );
			}
		  } catch( IOException e ) {
			e.printStackTrace();
		  }
		}
	}

	@Override
	public void run(){

	try {
        private Leaf leaf = new Leaf(Identity.PLANT_ENDPOINT);

		while(true){
			SensorsData data = (SensorsData) leaf.receive();

			SerialReader.sleep(1000);
		}


		} catch (SocketException | IOException | CorruptPacketException |InterruptedException e) {
			logger.fatal(e);
		} finally{
			leaf.close();
		}
	}


	public static void main( String[] args ) {
    try {
      ( new ArduinoPiSerialConnection() ).connect( "/dev/ttyAMA0" );
    } catch( Exception e ) {
      e.printStackTrace();
    }
  }










	
    /**
     * @param args
     * @throws InterruptedException
     * @throws IOException
     */
    public static void main(String args[]) throws InterruptedException, IOException {

        // !! ATTENTION !!
        // By default, the serial port is configured as a console port
        // for interacting with the Linux OS shell.  If you want to use
        // the serial port in a software program, you must disable the
        // OS from using this port.
        //
        // Please see this blog article for instructions on how to disable
        // the OS console for this port:
        // https://www.cube-controls.com/2015/11/02/disable-serial-port-terminal-output-on-raspbian/

        // create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate code)
        final Console console = new Console();

        // print program title/header
        console.title("Plant Endpoint Sensors");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

		Leaf leaf;

		leaf.recieve(); 
		
        




        try {
            
            }

           

            // continuous loop to keep the program running until the user terminates the program
            while(console.isRunning()) {
                try {
                    // write a formatted string to the serial transmit buffer
                    serial.write("CURRENT TIME: " + new Date().toString());

                    // write a individual bytes to the serial transmit buffer
                    serial.write((byte) 13);
                    serial.write((byte) 10);

                    // write a simple string to the serial transmit buffer
                    serial.write("Second Line");

                    // write a individual characters to the serial transmit buffer
                    serial.write('\r');
                    serial.write('\n');

                    // write a string terminating with CR+LF to the serial transmit buffer
                    serial.writeln("Third Line");
                }
                catch(IllegalStateException ex){
                    ex.printStackTrace();
                }

                // wait 1 second before continuing
                Thread.sleep(1000);
            }

        }
        catch(IOException ex) {
            console.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            return;
        }
    }

	//Recieve info from Arduino
	//Package sensor info



}