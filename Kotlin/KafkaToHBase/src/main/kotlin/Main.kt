import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaPairRDD
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.Time
import scala.Tuple2



object Main {
    @JvmStatic fun main(args: Array<String>) {
        val sparkConf = SparkConf().setAppName("SensorEventStorage")
        val spark = Spark(sparkConf)

        val hbaseConfig = HBaseConfiguration.create()
        hbaseConfig["hbase.zookeeper.quorum"] = environment.HBASE_ZOOKEEPER_QUORUM
        hbaseConfig[TableOutputFormat.OUTPUT_TABLE] = "sensor-event"
        val jobConf = JobConf(hbaseConfig, Main::class.java)
        jobConf.setOutputFormat(TableOutputFormat::class.java)
        jobConf[TableOutputFormat.OUTPUT_TABLE] = "sensor-event"
        jobConf["mapreduce.output.fileoutputformat.outputdir"] = "dummy"

        spark.withStreamingContext(Durations.seconds(1)) {
            kafka.withStream(
                    streamingContext = it,
                    group = "sensor-event-to-hbase-consumer",
                    topics = mapOf("sensor-event" to 1)
            ) {
                it.map { parseRawSensorData(it._2()) }
                        .mapToPair {
                            val rowKey = "${it.id}_${it.timestamp}".toByteArray()
                            val put = Put(rowKey)
                            put.addColumn("state-column-family".toByteArray(), "state".toByteArray(), Bytes.toBytes(it.state))
                            return@mapToPair Tuple2(ImmutableBytesWritable(rowKey), put)
                        }.foreachRDD(fun(javaPairRDD: JavaPairRDD<ImmutableBytesWritable, Put>, time: Time): Unit {
                    javaPairRDD.saveAsNewAPIHadoopDataset(jobConf)
                })
            }

            it.start()
            it.awaitTermination()
        }
    }
}
