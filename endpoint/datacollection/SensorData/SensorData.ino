#include "dht11.h"
#define DELAY 10000 //5 seconds

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

byte msg[512];

void setup() {
    //Start listening to arduino at a specific bps
    Serial.begin(115200);
  
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
    
    for (int i = 0; i < sizeof(msg)/8; i++){
      Serial.print(msg[i]);
    }
    Serial.print("\n");
    Serial.println("End of Mesage");
    delay(DELAY);
}

/*
 * Separate each sensor into its own function 
 */
void lightSource(){
    int lightMsgPosition = 1;
    
    //Photoresistor Value//
    PRValue = analogRead(PRPin); //get photo resistor value
    //Convert resistor value into lux
    lux = getLux(PRValue);
    double luxAP = lux - (int)lux;
    
    msg[lightMsgPosition] = 1;
    msg[lightMsgPosition+1] = (byte) lux;
    msg[lightMsgPosition+2] = (byte) luxAP;
    
    Serial.println(lux);
    
}

void moistureContent(){
    int soilMsgPosition = 10;
    
    SMSensorValue = analogRead(SMSensorPin); //get soil moisture value
    //convert moisture value into moisture percentage
    SMPercent = SMConvertToPercentage(SMSensorValue); 
    double SMPercentAP = (SMPercent - (int)SMPercent) * 100;
    
    msg[soilMsgPosition] = 2;
    msg[soilMsgPosition+1] = (byte) SMPercent;
    msg[soilMsgPosition+2] = (byte) SMPercentAP;
 
    Serial.println(SMPercent);
}

void dhtValues(){
    int temperatureMsgPosition = 19;
    int humidityMsgPosition = 28;
     
    DHTValue = DHT.read(DHTPin); //get DHT sensor value

    double temperatureAP = (DHT.temperature - (int)DHT.temperature) * 100;
    double humidityAP = (DHT.humidity - (int)DHT.humidity) * 100;
    
    msg[temperatureMsgPosition] = 3;
    msg[temperatureMsgPosition + 1] = (byte) DHT.temperature;
    msg[temperatureMsgPosition + 2] = (byte) temperatureAP;
    
    msg[humidityMsgPosition] = 4;
    msg[humidityMsgPosition + 1] = (byte) DHT.humidity;
    msg[humidityMsgPosition + 2] = (byte) humidityAP;
    
    Serial.println(DHT.temperature);
    Serial.println(DHT.humidity);
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
  return (double)floor((250.0 / (ADCValue * (float)PRValue)) - 50.0);
}
