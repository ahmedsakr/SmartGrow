#include "dht11.h"
#define DELAY 1000

dht11 DHT;
const int SMSensorPin = A0;
const int PRPin = A1;
const int LEDPin = 13;
const int DHTPin = 4;

int SMSensorValue = 0;
double SMPercent = 0;
int PRValue = 0;
double lux = 0, ADCValue = 0.0048828125;
int DTHValue;

void setup() {
  Serial.begin(9600);
  pinMode(SMSensorPin, INPUT);
  pinMode(PRPin, INPUT);
  pinMode(LEDPin, OUTPUT);
}

void loop() {
  
  SMSensorValue = analogRead(SMSensorPin); //get soil moisture value
  PRValue = analogRead(PRPin); //get photo resistor value
  DTHValue = DHT.read(DHTPin); //get DHT sensor value
  switch (DTHValue)
  {
    case DHTLIB_OK:  
                break;
    case DHTLIB_ERROR_CHECKSUM: 
                break;
    case DHTLIB_ERROR_TIMEOUT: 
                break;
    default: 
                break;
  }
  SMPercent = SMConvertToPercentage(SMSensorValue);
  lux = getLux(PRValue);
  
  Serial.print(SMSensorValue);
  Serial.print("\t");
  Serial.print(lux);
  Serial.print("\t");
  Serial.print(DHT.humidity,1);
  Serial.print("\t");
  Serial.print(DHT.temperature,1);
  Serial.print("\t");
  Serial.print("\n");

  delay(DELAY);
}

//returns the soil moisture percentge given the analog input from the soil moisture sensor
double SMConvertToPercentage(int value){
  double percentage = 0;
  percentage = (double)value * (100.0/1023.0);
  return percentage;
}

double getLux(int PRValue){
  return (double)(250.0 / (ADCValue * (float)PRValue)) - 50.0;
}
