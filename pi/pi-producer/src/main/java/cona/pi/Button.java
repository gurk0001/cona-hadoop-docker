package cona.pi;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * Created by NA001094 on 7/21/2016.
 */
public class Button extends Producer{
    private static final String STATE_ON = "1";
    private static final String STATE_OFF = "0";
    public Button(String topic, String bootstrapServer, String clientID, Boolean isAsync) {
        super(topic, bootstrapServer, clientID, isAsync);
    }
    public void run() {
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
        myButton.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                sendMessage(stateToString(event.getState()));
                System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
            }
        });
        // Initialize in "closed" state
        sendMessage(stateToString(PinState.LOW));
        for (;;) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                gpio.shutdown();
            }
        }
    }
    public String stateToString(PinState state) {
        String stateMsg;
        if (state == PinState.HIGH) {
            stateMsg = STATE_ON;
        } else {
            stateMsg = STATE_OFF;
        }
        return stateMsg;
    }
}
