package cona.twitter

import java.util.*


data class TwitterStatus(
        val id: Long,
        val text: String,
        val language: String?, // Language isn't always available
        val latitude: Double?,
        val longitude: Double?,
        val createdAt: Date
)