package org.webdevandsausages.events.utils
import java.io.InputStream

fun String.asResourceStream(): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(this)