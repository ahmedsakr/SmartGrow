import com.pi4j.wiringpi.Serial;

public class ArduinoPiSerialCom {
	
	public static void main(String[] args) {
		int serialPort = Serial.serialOpen(Serial.DEFAULT_COM_PORT, 9600);
		if(serialPort == -1) {
			System.out.println("Serial port connection failed");
		}else {
			System.out.println("Serial port connection initialized");
		}
		if(Serial.serialDataAvail(serialPort) > 0) {
			byte info = (byte)Serial.serialGetByte(serialPort);
			System.out.println(info);
		}
		
	}
	
}
