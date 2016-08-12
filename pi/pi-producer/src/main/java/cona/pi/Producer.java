package cona.pi;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public abstract class Producer extends Thread {
    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final Boolean isAsync;
    private final String clientID;

    public Producer(String topic, String bootstrapServer, String clientID, Boolean isAsync) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServer);
        props.put("client.id", clientID);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
        this.topic = topic;
        this.clientID = clientID;
        this.isAsync = isAsync;
    }

    public void sendMessage(String message) {
        long startTime = System.currentTimeMillis();
        String messageStr = this.clientID + "," + startTime + "," + message;
        if (isAsync) { // Send asynchronously
            producer.send(new ProducerRecord<>(topic,
                    clientID,
                    messageStr), new DemoCallBack(startTime, clientID, messageStr));
        } else { // Send synchronously
            try {
                producer.send(new ProducerRecord<>(topic,
                        clientID,
                        messageStr)).get();
                System.out.println("Sent message: (" + clientID + ", " + messageStr + ")");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

    }
}

class DemoCallBack implements Callback {

    private final long startTime;
    private final String key;
    private final String message;

    public DemoCallBack(long startTime, String key, String message) {
        this.startTime = startTime;
        this.key = key;
        this.message = message;
    }

    /**
     * A callback method the user can implement to provide asynchronous handling of request completion. This method will
     * be called when the record sent to the server has been acknowledged. Exactly one of the arguments will be
     * non-null.
     *
     * @param metadata  The metadata for the record that was sent (i.e. the partition and offset). Null if an error
     *                  occurred.
     * @param exception The exception thrown during processing of this record. Null if no error occurred.
     */
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (metadata != null) {
            System.out.println(
                    "message(" + key + ", " + message + ") sent to partition(" + metadata.partition() +
                            "), " +
                            "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
        } else {
            exception.printStackTrace();
        }
    }
}