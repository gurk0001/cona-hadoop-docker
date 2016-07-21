package cona.pi;
import com.pi4j.io.serial.*;

import java.io.IOException;

/**
 * This example code demonstrates how to perform serial communications using the Raspberry Pi.
 *
 * @author Robert Savage
 */
public class Ping extends Producer{
    private final String port;
    public Ping (String topic, String bootstrapServer, String clientID, Boolean isAsync, String port) {
        super(topic,bootstrapServer,clientID, isAsync);
        this.port = port;
    }
    public void run() {

        // create an instance of the serial communications class
        final Serial serial = SerialFactory.createInstance();

        // create and register the serial data listener
        serial.addListener(new SerialDataEventListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {

                // NOTE! - It is extremely important to read the data received from the
                // serial port.  If it does not get read from the receive buffer, the
                // buffer will continue to grow and consume memory.

                // print out the data received to the console
                try {
                    sendMessage(event.getAsciiString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            // create serial config object
            SerialConfig config = new SerialConfig();

            config.device(this.port)
                    .baud(Baud._9600)
                    .dataBits(DataBits._8)
                    .parity(Parity.NONE)
                    .stopBits(StopBits._1)
                    .flowControl(FlowControl.NONE);

            // display connection details
            System.out.println(" Connecting to: " + config.toString() +
                    "\n Data received on serial port will be displayed below.");

            // open the default serial device/port with the configuration settings
            serial.open(config);

            for (;;) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        catch(IOException ex) {
            System.out.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            return;
        }
    }
}