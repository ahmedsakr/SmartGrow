#include "dht11.h"
#define DELAY 5000 //5 seconds

//initialize the pins to the ports on the arduino uno
dht11 DHT;
const int SMSensorPin = A0;
const int PRPin = A1;
const int DHTPin = 4;

//analog to digital converter value
const double ADCValue = 0.0048828125;

//initialize values
double SMPercent = 0;
double lux = 0;
int DHTValue = 0;
int SMSensorValue = 0;
int PRValue = 0;


unsigned int sizeOfMsg = 512;
//byte msg[512];
byte msg[sizeOfMsg];

void setup() {
    //Start listening to arduino at a specific bps
    Serial.begin(9600);
  
    //Configure inputs
    pinMode(SMSensorPin, INPUT);
    pinMode(PRPin, INPUT);
    
    //start of the message to be sent to the Pi 
    msg[0] = 2; //OPCODE 2 for SensorData   
}

void loop(){   
      
    lightSource();
    moistureContent();
    dhtValues();
    
    for (int i = 0; i < sizeof(msg); i++){
      Serial.print(msg[i]);
    }
    delay(DELAY);
}

/*
 * Separate each sensor into its own function 
 */
void lightSource(){
    //Photoresistor Value//
    PRValue = analogRead(PRPin); //get photo resistor value
    //Convert resistor value into lux
    lux = getLux(PRValue);
  
    byte lightDataToByte = byte(lux);

    msg[1] = 1;
    msg[2] = lightDataToByte;    

//Code to print information to the serial monitor

    if (lux <= 1){
        Serial.println("Lux value is not available");
    }else{
        Serial.print(lux);
        Serial.println(" Lux Value");
    }
    
}

void moistureContent(){
    SMSensorValue = analogRead(SMSensorPin); //get soil moisture value
    //convert moisture value into moisture percentage
    SMPercent = SMConvertToPercentage(SMSensorValue); 

    byte soilDataToByte = byte(SMPercent);

    msg[10] = 2;
    msg[11] = soilDataToByte;
    Serial.println(soilDataToByte);

//Code to print information to the serial monitor

    Serial.print(SMPercent);
    Serial.println("% Moisture Level");
    
}

void dhtValues(){
   DHTValue = DHT.read(DHTPin); //get DHT sensor value
    
    //Cases will determine if the data read is what is expected.
    //Otherwise it will send an error.
/*  Code to print information to the serial monitor
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
*/

    byte temperatureDataToByte = byte(DHT.temperature);
    byte humidityDataToByte = byte(DHT.humidity);

    msg[19] = 3;
    msg[20] = temperatureDataToByte;
    msg[28] = 4;
    msg[29] = humidityDataToByte;
    
    //Serial.println(temperatureDataToByte);
   // Serial.println(humidityDataToByte);

//  Code to print information to the serial monitor
  
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
    Serial.println("END");

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
