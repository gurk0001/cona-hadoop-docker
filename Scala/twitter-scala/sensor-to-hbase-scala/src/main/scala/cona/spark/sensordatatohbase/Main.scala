package cona.spark.sensordatatohbase

import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.hadoop.hbase.client.{ConnectionFactory, Mutation}
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.{HBaseConfiguration, HColumnDescriptor, HTableDescriptor, TableName}
import org.apache.hadoop.mapred.{JobConf, OutputFormat}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import it.nerdammer.spark.hbase._

object Main extends App {
  val props = Util.loadProperties("config.properties")

  val hbaseConf = HBaseConfiguration.create()

  hbaseConf.set("hbase.zookeeper.quorum", Util.envVariable("HBASE_ZOOKEEPER_QUORUM"))
  hbaseConf.set(TableOutputFormat.OUTPUT_TABLE, props.getProperty("hbase.tableName"))
  hbaseConf.setClass("mapreduce.job.outputformat.class", classOf[TableOutputFormat], classOf[OutputFormat[String, Mutation]])

  val connection = ConnectionFactory.createConnection(hbaseConf)
  val admin = connection.getAdmin
  val tableName = TableName.valueOf(props.getProperty("hbase.tableName"))
  // Create the table if it doesn't exist.
  if(!admin.tableExists(tableName)) {
    println("Creating table!")
    val desc = new HTableDescriptor(tableName)
    desc.addFamily(new HColumnDescriptor(props.getProperty("hbase.columnFamily")))
    admin.createTable(desc)
  }

  val sparkConf = new SparkConf().setAppName("SensorToHbase")
  sparkConf.set("spark.hbase.host", Util.envVariable("HBASE_ZOOKEEPER_QUORUM"))

  val jobConf = new JobConf(hbaseConf, this.getClass)

  val brokers = Util.envVariable("KAFKA_BROKER")
  val groupId = props.getProperty("kafka.group")
  val topic = props.getProperty("kafka.topic")

  val sparkStreamingContext = new StreamingContext(sparkConf, Seconds(1))
  val kafkaParams = Map[String, String](
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
    ConsumerConfig.GROUP_ID_CONFIG -> groupId,
    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG ->
      "org.apache.kafka.common.serialization.StringDeserializer",
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG ->
      "org.apache.kafka.common.serialization.StringDeserializer",
    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> "false"
  )

  val kafkaInputs = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
    sparkStreamingContext,
    kafkaParams,
    Set(topic)
  )
  val events = kafkaInputs.map(_._2).map(ButtonEvent.parseButtonEvent)
  events.foreachRDD{ rdd =>
    rdd.map(buttonEvent =>
      (s"${buttonEvent.id}_${buttonEvent.timestamp}", buttonEvent.pressType)
    ).toHBaseTable(props.getProperty("hbase.tableName"))
      .toColumns(props.getProperty("hbase.stateColumn"))
      .inColumnFamily(props.getProperty("hbase.columnFamily"))
      .save()
  }

  sparkStreamingContext.start()
  sparkStreamingContext.awaitTermination()
}
