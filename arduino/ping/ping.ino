#include <NewPing.h>

#define TRIGGER_PIN  3
#define ECHO_PIN     3
#define MAX_DISTANCE 200
 
NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE);
 
void setup() {
  Serial.begin(9600);
}

void loop() {
  delay(100);
  int uS = sonar.ping();
  Serial.println(max(1,uS / US_ROUNDTRIP_CM));
}
