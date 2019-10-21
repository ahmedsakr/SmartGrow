#include <dht.h>
#define DELAY 10000

dht DHT;
const int SMSensorPin = A0;
const int PRPin = A1;
const int LEDPin = 13;
const int DHTPin = 4;

int SMSensorValue = 0;
int SMPercent = 0;
int PRValue = 0;
int LEDValue = 0;
int LEDBrightness = 0;

void setup() {
  Serial.begin(115200);
  pinMode(SMSensorPin, INPUT);
  pinMode(PRPin, INPUT);
  pinMode(LEDPin, OUTPUT);
}

void loop() {
  
  SMSensorValue = analogRead(SMSensorPin); //get soil moisture value
  PRValue = analogRead(PRPin); //get photo resistor value
  DTHValue = DHT.read11(DHTPin); //get DHT sensor value
  switch (DTHValue)
  {
    case DHTLIB_OK:  
                Serial.print("OK,\t"); 
                break;
    case DHTLIB_ERROR_CHECKSUM: 
                Serial.print("Checksum error,\t"); 
                break;
    case DHTLIB_ERROR_TIMEOUT: 
                Serial.print("Time out error,\t"); 
                break;
    default: 
                Serial.print("Unknown error,\t"); 
                break;
  }
  SMPercent = SMConvertToPercentage(SMSensorValue);
  LEDValue = getLEDValue(PRValue);
  LEDBrightness = getLEDBrightness(LEDValue);
  analogWrite(LEDPin, LEDValue); //set LED brightness to the corresponding value
  
  Serial.print(SMPercent);
  Serial.print("\t");
  Serial.print(LEDBrightness);
  Serial.print("\t");
  Serial.print(DHT.humidity,1);
  Serial.print("\t");
  Serial.print(DHT.temperature,1);
  Serial.print("\t");

  delay(DELAY);
}

//returns the soil moisture percentge given the analog input from the soil moisture sensor
int SMConvertToPercentage(int value){
  int percentage = 0;
  percentage = value * (100/1023);
  return percentage;
}

//returns the corresponding LED brightness value given the input from the photoresistor
int getLEDValue(int PRInput){
  return (int)(PRInput * (255/1023));
}

//returns the LED brightness percentage given the LED brightness value
int getLEDBrightness(int LEDValue){
  return (int)(LEDValue * (100/255));
}
