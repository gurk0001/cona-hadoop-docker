package cona.twitter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.text.SimpleDateFormat
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class Tweet(
        val text: String,
        @JsonProperty("lang") val language: String,
        @JsonProperty("created_at") val createdAt: Date
) {
    companion object {
        private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
                .setDateFormat(SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH))

        fun fromJson(json: String): Tweet? {
            try {
                return mapper.readValue(json)
            } catch (e: MissingKotlinParameterException) {
                return null
            }
        }
    }
}
