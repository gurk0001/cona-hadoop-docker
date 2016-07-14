package cona.twitter.stream

import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import kotlin.concurrent.thread


fun main(args: Array<String>) {
    val (client, queue) = buildTweetStreamClient(
            System.getenv("TWITTER_CONSUMER_KEY"),
            System.getenv("TWITTER_CONSUMER_SECRET"),
            System.getenv("TWITTER_ACCESS_TOKEN"),
            System.getenv("TWITTER_ACCESS_SECRET"),
            StatusesFilterEndpoint().trackTerms(listOf("coke,coca-cola,coca cola"))
    )

    thread(isDaemon = false) {
        while (!client.isDone) {
            println(queue.take())
        }
    }
    client.connect()
}