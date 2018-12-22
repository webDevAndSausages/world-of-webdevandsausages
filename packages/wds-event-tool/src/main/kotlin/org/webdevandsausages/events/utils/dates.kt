package org.webdevandsausages.events.utils

import java.sql.Timestamp
import java.time.LocalDateTime

inline val Timestamp?.hasPassed get() =
    Timestamp.valueOf(LocalDateTime.now()).after(this)

inline val Timestamp?.threeDaysLater get() =
    Timestamp.valueOf(LocalDateTime.now().plusDays(3)).after(this)
