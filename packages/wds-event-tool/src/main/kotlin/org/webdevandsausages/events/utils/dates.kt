package org.webdevandsausages.events.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*


inline val OffsetDateTime?.hasPassed
    get() =
        OffsetDateTime.now().isAfter(this)

inline val OffsetDateTime?.threeDaysLater
    get() =
        OffsetDateTime.now().plusDays(3).isAfter(this)

inline val OffsetDateTime?.prettified: String
    get() {
        var timeformat = SimpleDateFormat("EEE, d MMM yyyy, hh:mm aaa")
        timeformat.timeZone = TimeZone.getTimeZone(ZoneId.of("Europe/Helsinki"))
        return timeformat.format(
            this?.toInstant()?.toEpochMilli()
        )
    }

inline val Timestamp?.hasPassed
    get() =
        Timestamp.valueOf(LocalDateTime.now()).after(this)

inline val Timestamp?.threeDaysLater
    get() =
        Timestamp.valueOf(LocalDateTime.now().plusDays(3)).after(this)

inline val Timestamp?.prettified: String
    get() {
        var timeformat = SimpleDateFormat("EEE, d MMM yyyy, hh:mm aaa")
        timeformat.timeZone = TimeZone.getTimeZone(ZoneId.of("Europe/Helsinki"))
        return timeformat.format(
            this?.toInstant()?.toEpochMilli()
        )
    }
