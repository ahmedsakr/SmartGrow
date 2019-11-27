#define DELAY 1000

//Declare sensors
const int PRPin = A1; 

int PRValue = 0;
const double ADCValue = 0.0048828125; //Analog to Digital Converter value

void setup() {
    Serial.begin(115200); //baud rate
    pinMode(PRPin, INPUT); //initialize the PRPin to be input 
}

void loop() {
    PRValue = analogRead(PRPin);
    Serial.println(getLux(PRValue));
    delay(DELAY);//delay for 1 second
}   

//Gets the lux (luminous flux) of the light shining into the resistor.
double getLux(int PRValue){
    return (double)(250.0 / (ADCValue * (float)PRValue)) - 50.0;
}