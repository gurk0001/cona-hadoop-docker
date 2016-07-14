package cona.twitter.stream

import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.Client
import com.twitter.hbc.core.Constants
import com.twitter.hbc.core.Constants.STREAM_HOST
import com.twitter.hbc.core.HttpHosts
import com.twitter.hbc.core.endpoint.StreamingEndpoint
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.auth.OAuth1
import cona.twitter.Tweet
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue


fun buildTweetStreamClient(
        consumerKey: String,
        consumerSecret: String,
        accessToken: String,
        accessSecret: String,
        endpoint: StreamingEndpoint,
        tweetQueueSize: Int = 1000): Pair<Client, BlockingQueue<Tweet>> {
    val queue: BlockingQueue<Tweet> = LinkedBlockingQueue<Tweet>(tweetQueueSize)
    val client: Client = ClientBuilder()
            .hosts(HttpHosts(STREAM_HOST))
            .authentication(OAuth1(consumerKey, consumerSecret, accessToken, accessSecret))
            .endpoint(endpoint)
            .processor(TweetProcessor(queue))
            .build()
    return Pair(client, queue)
}