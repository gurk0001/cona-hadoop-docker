package cona.twitter.stream.connect

import twitter4j.StallWarning
import twitter4j.Status
import twitter4j.StatusDeletionNotice
import twitter4j.StatusListener


interface NoOpTwitterStreamTaskListener : StatusListener {
    override fun onScrubGeo(userId: Long, upToStatusId: Long) { }

    override fun onTrackLimitationNotice(numberOfLimitedStatuses: Int) { }

    override fun onStallWarning(warning: StallWarning) { }

    override fun onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) { }

    override fun onException(ex: Exception) { }

    override fun onStatus(status: Status) { }
}