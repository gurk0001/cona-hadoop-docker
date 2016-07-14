package cona.twitter.stream

import com.twitter.hbc.core.processor.AbstractProcessor
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import cona.twitter.Tweet
import java.io.InputStream
import java.util.concurrent.BlockingQueue

private fun String.toTweet() : Tweet? = Tweet.fromJson(this)

class TweetProcessor(
        queue: BlockingQueue<Tweet>, offerTimeoutMillis: Long = AbstractProcessor.DEFAULT_OFFER_TIMEOUT_MILLIS
) : AbstractProcessor<Tweet>(queue, offerTimeoutMillis) {

    // Have to extend StringDelimitedProcessor to expose its processNextMessage() method
    private val realProcessor = object : StringDelimitedProcessor(null, offerTimeoutMillis) {
        // Need a way to call processNextMessage despite its' protected access modifier, so we reassign it here
        public override fun processNextMessage() = super.processNextMessage()
    }
    override fun setup(input: InputStream?) = realProcessor.setup(input)

    override fun processNextMessage() = realProcessor.processNextMessage()?.toTweet()

}