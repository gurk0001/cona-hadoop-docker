import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.Duration
import org.apache.spark.streaming.api.java.JavaStreamingContext

class Spark(val config: SparkConf) {
    fun withStreamingContext(duration: Duration, func: (JavaStreamingContext) -> Unit) = JavaStreamingContext(config, duration).use(func)
}
