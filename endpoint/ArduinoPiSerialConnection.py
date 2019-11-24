#RaspPi-Arduino Serial Communication
#ArduinoPiSerialConnection.py on the RaspPi
#SensorsData.ino on the Arduino

import serial
import time

#Serial port where the Arduino is connected to the Pi
arduino = serial.Serial(*/dev/ttyACMO*)
#arduino.baudrate=9600

data = arduino.readline()
dataPieces = data.split("\t")

for x in range(len(dataPieces)):
	print dataPieces[x];