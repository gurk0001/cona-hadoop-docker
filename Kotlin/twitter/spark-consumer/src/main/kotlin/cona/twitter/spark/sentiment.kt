package cona.twitter.spark

import cona.twitter.spark.Sentiment.*
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import java.util.*

enum class Sentiment {
	VERY_NEGATIVE, NEGATIVE, NEUTRAL, POSITIVE, VERY_POSITIVE, UNKNOWN
}

fun calculateSentiment(message: String): Map<String, Sentiment> {
	val props = Properties()
	props.setProperty("annotators", "tokenize, ssplit, parse, sentiment")
	val pipeline = StanfordCoreNLP(props)
	val annotation = pipeline.process(message)
	val sentiments = hashMapOf<String, Sentiment>()
	for (sentence in annotation.get(CoreAnnotations.SentencesAnnotation::class.java)) {
		val tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree::class.java)
		val sentiment = RNNCoreAnnotations.getPredictedClass(tree)
		sentiments[sentence.toString()] = when(sentiment) {
			0 -> VERY_NEGATIVE
			1 -> NEGATIVE
			2 -> NEUTRAL
			3 -> POSITIVE
			4 -> VERY_POSITIVE
			-1 -> UNKNOWN
			else -> throw IllegalStateException("Bad sentiment value: $sentiment for sentence: $sentence")
		}
	}
	return sentiments
}