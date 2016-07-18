package cona.twitter.spark

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import cona.twitter.TwitterStatus
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaPairRDD
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils

fun main(args: Array<String>): Unit {

	val objectMapper = jacksonObjectMapper()
	val sparkConf = SparkConf().setAppName("TweetProcessing")
	val streamingContext = JavaStreamingContext(sparkConf, Durations.seconds(1))
	val kafkaStream = KafkaUtils.createStream(streamingContext, System.getenv("KAFKA_ZOOKEEPER_QUORUM"), "tweet-processor", mapOf("tweet" to 1))

	kafkaStream.foreachRDD (fun (j: JavaPairRDD<String, String>): Unit {
		j.mapValues { objectMapper.readValue<TwitterStatus>(it) } // Deserialize the json
	})
}