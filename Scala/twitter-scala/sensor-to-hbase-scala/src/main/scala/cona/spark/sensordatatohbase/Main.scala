package cona.spark.sensordatatohbase

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


  val sparkStreamingContext = new StreamingContext(sparkConf, Seconds(1))
  sparkStreamingContext.checkpoint("checkpoint")

  val kafkaInputs = KafkaUtils.createStream(
    sparkStreamingContext,
    Util.envVariable("KAFKA_ZOOKEEPER_QUORUM"),
    props.getProperty("kafka.group"),
    Map(props.getProperty("kafka.topic") -> 1)
  ).map(_._2) // map to the second entry in the tuple (first is the topic name)

  val events = kafkaInputs.map(ButtonEvent.parseButtonEvent)
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
