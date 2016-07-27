from kafka import KafkaProducer
import time
import math
producer = KafkaProducer(bootstrap_servers="192.168.99.100:9092")
while True:
    string = bytes('button-sensor-1,' + str(long(time.time()*1000)) + ',' + str(math.sin(time.time())))
    print(producer.send('button-event', string))
    time.sleep(0.005)
