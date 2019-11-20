const int PRPin = A1;

int PRValue = 0;

void setup() {
  Serial.begin(115200);
  pinMode(PRPin, INPUT);
}

void loop() {
  PRValue = analogRead(PRPin);
  Serial.print(PRValue);
  Serial.print("\n");
  delay(30);
}
