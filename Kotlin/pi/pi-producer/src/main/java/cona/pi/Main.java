package cona.pi;

import java.io.IOException;
import java.util.Properties;

/* A basic Kafka cona.pi.Producer that reads sensor data and sends to
* Kafka Broker*/
public class Main {
    static Properties props;

    public static void main(String[] args) throws IOException {
        readProps();
        Producer producer1 = new Producer(props.getProperty("topic"),props.getProperty("ip"),props.getProperty("sensorID"), true);
        producer1.start();
    }

    public static void readProps() throws java.io.IOException {
        props = new Properties();
        props.load(Main.class.getClassLoader().getResourceAsStream("config.properties"));
    }
}