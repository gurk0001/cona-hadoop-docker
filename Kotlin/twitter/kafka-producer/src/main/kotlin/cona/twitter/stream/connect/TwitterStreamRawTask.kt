package cona.twitter.stream.connect

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import cona.twitter.TwitterStatus
import cona.twitter.stream.TwitterStreamReceiver
import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.source.SourceRecord
import org.apache.kafka.connect.source.SourceTask
import org.slf4j.LoggerFactory
import twitter4j.Status
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

// we convert to our own TwitterStatus implementation so that we don't have to depend on Twitter4j in other parts of the app.
private fun Status.toTwitterStatus(): TwitterStatus = TwitterStatus(id, text, lang, geoLocation?.latitude, geoLocation?.longitude, createdAt)
private fun TwitterStatus.toSourceRecord(topic: String, jacksonMapper: ObjectMapper) = SourceRecord(
        mapOf("topic" to topic),
        mapOf("timestamp" to createdAt.time),
        topic,
        Schema.INT64_SCHEMA, createdAt.time,
        Schema.STRING_SCHEMA, jacksonMapper.writeValueAsString(this)
        )

class TwitterStreamRawTask: SourceTask() {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper = jacksonObjectMapper()

    private lateinit var twitterStreamReceiver: TwitterStreamReceiver

    private lateinit var messageQueue: BlockingQueue<Status>

    private lateinit var topic: String

    override fun version(): String = TwitterStreamRawConnector().version()

    override fun start(props: Map<String, String>) {
        logger.debug("${this.javaClass.simpleName} starting")
        val queueSize = props[Constants.MESSAGE_QUEUE_SIZE]?.toInt() ?: Constants.DEFAULT_MESSAGE_QUEUE_SIZE
        messageQueue = LinkedBlockingQueue()
        topic = props[Constants.TWEET_KAFKA_TOPIC]!!
        twitterStreamReceiver = TwitterStreamReceiver(
                props[Constants.CONSUMER_KEY]!!,
                props[Constants.CONSUMER_SECRET]!!,
                props[Constants.ACCESS_TOKEN]!!,
                props[Constants.ACCESS_SECRET]!!
        )
        twitterStreamReceiver.connect(listOf(props[Constants.TERMS_TO_TRACK]!!), queueSize, object : NoOpTwitterStreamTaskListener {
            override fun onStatus(status: Status) {
                logger.info("Got a status: ${status.text}")
                messageQueue.add(status)
            }
        })
    }

    override fun stop() {
        logger.info("${this.javaClass.simpleName} stopping")
        twitterStreamReceiver.disconnect()
    }

    override fun poll(): List<SourceRecord> {
        val statuses: MutableList<Status> = LinkedList() // We're only doing stack-like operations, so a linked list is faster
        // Wait until there's at least one status
        logger.info("poll starting")
        statuses.add(messageQueue.take())

        // take all the other statuses (if there are any)
        messageQueue.drainTo(statuses)
        logger.info("got ${statuses.size} messages")

        // convert those statuses to SourceRecords
        return statuses.map { it.toTwitterStatus().toSourceRecord(topic, mapper) }
    }
}