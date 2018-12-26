package org.webdevandsausages.events.utils

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid

val String.isValidEmail get() = contains("@") && length >= 3 && length < 250

abstract class Read<A> {
    abstract fun read(s: String): Option<A>
    companion object {
        val stringRead: Read<String> =
            object : Read<String>() {
                override fun read(s: String): Option<String> = Option(s)
            }
        val emailRead: Read<String> =
            object : Read<String>() {
                override fun read(s: String): Option<String> =
                    if (s.isValidEmail) Option(s) else None
            }
    }
}

sealed class RequestBodyError {
    data class MissingData(val field: String) : RequestBodyError()
    data class MalformedData(val field: String) : RequestBodyError()
}

fun <A> parse(read: Read<A>, value: String): Validated<RequestBodyError, A> {
    val v = Option.fromNullable(value)
    return when (v) {
        is Some -> {
            val s = read.read(v.t)
            when (s) {
                is Some -> s.t.valid()
                is None -> RequestBodyError.MalformedData(value).invalid()
            }
        }
        is None -> Validated.Invalid(RequestBodyError.MissingData(value))
    }
}
