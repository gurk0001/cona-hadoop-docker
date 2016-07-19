package cona.twitter.stream

import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.Constants.STREAM_HOST
import com.twitter.hbc.core.HttpHosts
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.auth.OAuth1
import com.twitter.hbc.twitter4j.Twitter4jStatusClient
import twitter4j.StatusListener
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue


class TwitterStreamReceiver (
        private val consumerKey: String,
        private val consumerSecret: String,
        private val accessToken: String,
        private val accessSecret: String
){
    private lateinit var termsToTrack: List<String>
    private lateinit var t4jClient: Twitter4jStatusClient

    var isConnected: Boolean = false
        private set

    private fun buildClient(msgQueue: BlockingQueue<String>) = ClientBuilder()
            .hosts(HttpHosts(STREAM_HOST))
            .authentication(authenticate())
            .endpoint(StatusesFilterEndpoint().trackTerms(termsToTrack))
            .processor(StringDelimitedProcessor(msgQueue))
            .build()

    private fun authenticate(): OAuth1 = OAuth1(consumerKey, consumerSecret, accessToken, accessSecret)

    fun connect(
            termsToTrack: List<String>,
            msgQueueSize: Int,
            listeners: List<StatusListener>
    ) {
        synchronized(this) {
            if (isConnected) throw IllegalStateException("Already connected")
            this.termsToTrack = termsToTrack

            val msgQueue = LinkedBlockingQueue<String>(msgQueueSize)
            val hbClient = buildClient(msgQueue)

            t4jClient = Twitter4jStatusClient(hbClient, msgQueue, listeners, Executors.newSingleThreadExecutor())
            t4jClient.connect()
            t4jClient.process()
            isConnected = true
        }
    }

    /**
     * Connect to twitter with the given callback listener
     */
    fun connect(
            termsToTrack: List<String>,
            msgQueueSize: Int,
            listener: StatusListener
    ) = connect(termsToTrack, msgQueueSize, listOf(listener))

    fun disconnect() {
        synchronized(this) {
            if(!isConnected) throw IllegalStateException("Not currently connected!")
            t4jClient.stop()
        }
    }
}