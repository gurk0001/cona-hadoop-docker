package cona.spark.sensordatatohbase

import java.util.Properties

import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.util.Bytes

case class ButtonEvent(id: String, timestamp: Long, pressType: Int)

object ButtonEvent extends Serializable {
  def parseButtonEvent(raw: String): ButtonEvent = {
    System.out.println(raw);
    val s = raw.split(',')
    // TODO: error handling for mal-formed packets (or maybe kafka should handle it?)
    ButtonEvent(s(0), s(1).toLong, s(2).toInt)
  }

  def convertToPut(event: ButtonEvent, props: Properties): (ImmutableBytesWritable, Put) = {
    val rowKey = s"${event.id}_${event.timestamp}"
    val put = new Put(Bytes.toBytes(rowKey))
    val colFamily = Bytes.toBytes(props.getProperty("hbase.columnFamily"))
    put.addColumn(colFamily, Bytes.toBytes("id"), Bytes.toBytes(event.id))
    put.addColumn(colFamily, Bytes.toBytes("timestamp"), Bytes.toBytes(event.timestamp))
    put.addColumn(colFamily, Bytes.toBytes("pressType"), Bytes.toBytes(event.pressType))
    return (new ImmutableBytesWritable(Bytes.toBytes(rowKey)), put)
  }
}
