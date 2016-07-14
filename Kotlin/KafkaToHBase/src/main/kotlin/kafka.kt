import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils

object kafka {

    fun withStream(
            streamingContext: JavaStreamingContext,
            group: String,
            topics: Map<String, Int>,
            func: (JavaPairReceiverInputDStream<String, String>) -> Unit
    ) = KafkaUtils.createStream(streamingContext, environment.KAFKA_ZOOKEEPER_QUORUM, group, topics).let(func)
}
