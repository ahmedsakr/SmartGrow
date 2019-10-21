const int PRPin = A0;
const int LEDPin = 13;

int PRValue = 0;
int LEDValue = 0;
int LEDBrightness = 0;

void setup() {
  Serial.begin(9600);
  pinMode(PRPin, INPUT);
  pinMode(LEDPin, OUTPUT);
}

void loop() {
  PRValue = analogRead(PRPin);
  Serial.print(PRValue);
  Serial.print("\t");
  LEDValue = getLEDValue(PRValue);
  Serial.print(LEDValue);
  Serial.print("\n");
//  LEDBrightness = getLEDBrightness(LEDValue);
  analogWrite(LEDPin, LEDValue);
//  Serial.print("The LEB is operating at " + LEDBrightnes + "%.\n");
  delay(30);
}

int getLEDValue(int PRInput){
  double a = PRInput / 1023.0 * 225.0;
  return (int)a;
}

//int getLEDBrightness(int LEDValue){
//  return (int)(LEDValue * (100/255));
//}
