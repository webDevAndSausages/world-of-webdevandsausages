package org.webdevandsausages.events.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> T.createLogger(): Logger {
    if (T::class.isCompanion) {
        return LoggerFactory.getLogger(T::class.java.enclosingClass)
    }
    return LoggerFactory.getLogger(T::class.java)
}