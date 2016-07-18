package cona.twitter.stream.connect

import cona.twitter.stream.connect.Constants.ACCESS_SECRET
import cona.twitter.stream.connect.Constants.ACCESS_TOKEN
import cona.twitter.stream.connect.Constants.CONSUMER_KEY
import cona.twitter.stream.connect.Constants.CONSUMER_SECRET
import cona.twitter.stream.connect.Constants.DEFAULT_EVENT_QUEUE_SIZE
import cona.twitter.stream.connect.Constants.DEFAULT_MESSAGE_QUEUE_SIZE
import cona.twitter.stream.connect.Constants.EVENT_KAFKA_TOPIC
import cona.twitter.stream.connect.Constants.MESSAGE_QUEUE_SIZE
import cona.twitter.stream.connect.Constants.TERMS_TO_TRACK
import cona.twitter.stream.connect.Constants.TWEET_KAFKA_TOPIC
import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.common.config.ConfigDef.Importance.HIGH
import org.apache.kafka.common.config.ConfigDef.Importance.LOW
import org.apache.kafka.common.config.ConfigDef.Type.INT
import org.apache.kafka.common.config.ConfigDef.Type.STRING
import org.apache.kafka.common.utils.AppInfoParser
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.source.SourceConnector
import org.slf4j.LoggerFactory

class TwitterStreamRawConnector : SourceConnector() {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private lateinit var properties: Map<String, String>

    // This is honestly just copied from
    // https://github.com/amient/hello-kafka-streams/blob/master/src/main/java/io/amient/kafka/connect/irc/IRCFeedConnector.java
    // I'm not sure what an AppInfoParser is. I think it gets info from a properties file at some point. Maybe.
    override fun version(): String = AppInfoParser.getVersion()

    override fun taskClass(): Class<out Task> = TwitterStreamRawTask::class.java

    override fun config(): ConfigDef = ConfigDef().apply {
        define(TERMS_TO_TRACK, STRING, HIGH, "Terms that the connector will filter by")

        define(TWEET_KAFKA_TOPIC, STRING, HIGH, "Kafka topic to send tweets to")
        define(EVENT_KAFKA_TOPIC, STRING, HIGH, "Kafka topic to send twitter connection events to")

        define(MESSAGE_QUEUE_SIZE, INT, DEFAULT_MESSAGE_QUEUE_SIZE, LOW, "Maximum length of the queue used for processing incoming tweets")
        define(Constants.EVENT_QUEUE_SIZE, INT, DEFAULT_EVENT_QUEUE_SIZE, LOW, "Maximum length of the queue used for processing twitter stream events")

        // These configuration elements are required for authentication with the twitter api
        define(CONSUMER_KEY, STRING, HIGH, "Twitter API Consumer Key")
        define(CONSUMER_SECRET, STRING, HIGH, "Twitter API Consumer Secret")
        define(ACCESS_TOKEN, STRING, HIGH, "Twitter Api Access Token")
        define(ACCESS_SECRET, STRING, HIGH, "Twitter Api Access Secret")
    }

    override fun taskConfigs(maxTasks: Int): List<Map<String, String>> {
        // We can only have one connection to the twitter api, so there can only be one task.
        // That task just gets the configuration that was given to the connector in start().
        // It's OK to share that configuration object because it's an immutable map.
        return listOf(properties)
    }

    override fun start(props: Map<String, String>) {
        logger.info("Starting TwitterStreamRawConnector")
        properties = props
    }

    override fun stop(): Unit {
        logger.info("Stopping TwitterStreamRawConnector")
    }

}