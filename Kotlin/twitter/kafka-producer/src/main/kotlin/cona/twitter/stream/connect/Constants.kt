package cona.twitter.stream.connect

/**
 * Constants used by twitter stream connectors
 */
object Constants {
    val TERMS_TO_TRACK = "twitter.terms"
    val CONSUMER_KEY = "twitter.consumer_key"
    val CONSUMER_SECRET = "twitter.consumer_secret"
    val ACCESS_TOKEN = "twitter.access_token"
    val ACCESS_SECRET = "twitter.access_secret"

    val TWEET_KAFKA_TOPIC = "twitter.topics.tweet"
    val EVENT_KAFKA_TOPIC = "twitter.topics.event"

    val MESSAGE_QUEUE_SIZE = "twitter.queue.message.size"
    val EVENT_QUEUE_SIZE = "twitter.queue.event.size"

    val DEFAULT_MESSAGE_QUEUE_SIZE = 10000
    val DEFAULT_EVENT_QUEUE_SIZE = 1000
}