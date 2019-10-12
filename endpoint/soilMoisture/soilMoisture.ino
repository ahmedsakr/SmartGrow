//Initialize Pins
const int SMSensorPin = A0;

int SMSensorValue = 0;
int SMPercent = 0;

void setup() {
  Serial.begin(9600);
  pinMode(SMSensorPin, INPUT);
}

void loop() {
  SMSensorValue = analogRead(SMSensorPin);
  SMPercent = SMConvertToPercentage(SMSensorValue);
  printValues();
  delay(1000);
}

int SMConvertToPercentage(int value){
  int percentage = 0;
  percentage = value * (100/1023);
  return percentage;
}

void printValues(){
  Serial.print("Soil Moisture Analog Value: " + SMSensorValue);
  Serial.print("\nSoil Moisture Percentage: " + SMPercent + "%");
}
