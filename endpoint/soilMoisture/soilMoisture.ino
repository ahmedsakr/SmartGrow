//Initialize Pins
const int SMSensorPin = A0;

int SMSensorValue = 0;

void setup() {
  Serial.begin(9600);
  pinMode(SMSensorPin, INPUT);
}

void loop() {
  SMSensorValue = analogRead(SMSensorPin);
  Serial.print(SMSensorValue);
  Serial.print("\t");
  double SMPercent = SMConvertToPercentage(SMSensorValue);
  Serial.print(SMPercent);
  Serial.print("\n");
//  printValues();
  delay(100);
}

double SMConvertToPercentage(int value){
  double percentage;
  percentage = 100 - (double)value*((double)100/1023);
  return percentage;
}

void printValues(){
  Serial.print("Soil Moisture Analog Value: " + SMSensorValue);
//  Serial.print("\nSoil Moisture Percentage: " + SMPercent + "%");
}
