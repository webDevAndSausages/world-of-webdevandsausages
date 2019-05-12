package org.webdevandsausages.events.utils

import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

inline val Timestamp?.hasPassed
    get() =
        Timestamp.valueOf(LocalDateTime.now()).after(this)

inline val Timestamp?.threeDaysLater
    get() =
        Timestamp.valueOf(LocalDateTime.now().plusDays(3)).after(this)

inline val Timestamp?.prettified: String
    get() {
        val atOffset = this?.toLocalDateTime()?.atZone(ZoneId.of("Europe/Helsinki"))
        if (atOffset == null) return ""

        return SimpleDateFormat("EEE, d MMM yyyy, hh:mm aaa").format(
            LocalDateTime.of(
                atOffset.year,
                atOffset.month,
                atOffset.dayOfMonth,
                atOffset.hour,
                atOffset.minute,
                atOffset.second
            ).toInstant(ZoneOffset.UTC).toEpochMilli()
        )
    }
