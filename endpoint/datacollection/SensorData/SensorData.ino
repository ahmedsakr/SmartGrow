#include "dht11.h"
#define DELAY 5000

dht11 DHT;
const int SMSensorPin = A0;
const int PRPin = A1;
const int DHTPin = 4;

const double ADCValue = 0.0048828125;

int SMSensorValue = 0;
double SMPercent = 0;
int PRValue = 0;
double lux = 0;
int DHTValue = 0;

void setup() {
    Serial.begin(9600);
  
    //configure inputs
    pinMode(SMSensorPin, INPUT);
    pinMode(PRPin, INPUT);
}

void loop() {
    //Soil Moisture Value//
    SMSensorValue = analogRead(SMSensorPin); //get soil moisture value
    //convert moisture value into moisture percentage
    SMPercent = SMConvertToPercentage(SMSensorValue); 
  
    //Photoresistor Value//
    PRValue = analogRead(PRPin); //get photo resistor value
    //Convert resistor value into lux
    lux = getLux(PRValue);
  
    DHTValue = DHT.read(DHTPin); //get DHT sensor value
  
    //Cases will determine if the data read is what is expected.
    //Otherwise it will send an error.
    switch (DHTValue)
    {
      case DHTLIB_OK:  
        Serial.print("OK, ");
        break;
      case DHTLIB_ERROR_CHECKSUM: 
        Serial.print("Checksum error, ");
        break;
      case DHTLIB_ERROR_TIMEOUT: 
        Serial.print("Time out error, ");
        break;
      default: 
        Serial.print("Unknown error, ");
        break;
    }

    //Display Values
    //LIGHT SENSOR
    if (lux <= 0){
        Serial.println("Lux value is not available");
    }else{
        Serial.print(lux);
        Serial.println(" Lux Value");
    }
    //TEMPERATURE
    if (DHT.temperature > 60 | DHT.temperature < -20){
        Serial.println("Temperature is not within the sensor's range. Reading will not be accurate.");
    }
    Serial.print(DHT.temperature);
    Serial.println(" Temperature Level");
    //HUMIDITY
    if (DHT.humidity > 95 | DHT.humidity < 5){
        Serial.println("Humidity is not within the sensor's range. Reading will not be accurate.");
    }
    Serial.print(DHT.humidity);
    Serial.println(" Humidity Level");
    //SOIL MOISTURE
    Serial.print(SMPercent);
    Serial.println("% Moisture Level");
    Serial.println("END");
  
    delay(DELAY);
}

/*Returns the soil moisture percentge given the analog input from the soil moisture sensor
 * 
 */
double SMConvertToPercentage(int value){
  double percentage = 0;
  percentage = (double)value * (100.0/1023.0);
  return percentage;
}

/*Returns the luminous flux (lux) of the light shone onto the light sensor
 *
 */
double getLux(int PRValue){
  return (double)(250.0 / (ADCValue * (float)PRValue)) - 50.0;
}
