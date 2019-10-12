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
  LEDValue = getLEDValue(PRValue);
  LEDBrightness = getLEDBrightness(LEDValue);
  analogWrite(LEDPin, LEDValue);
  Serial.print("The LEB is operating at " + LEDBrightnes + "%.\n");
  delay(30);
}

int getLEDValue(int PRInput){
  return (int)(PRInput * (255/1023));
}

int getLEDBrightness(int LEDValue){
  return (int)(LEDValue * (100/255));
}
