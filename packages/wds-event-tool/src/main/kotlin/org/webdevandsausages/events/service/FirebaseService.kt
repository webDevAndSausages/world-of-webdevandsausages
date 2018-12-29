package org.webdevandsausages.events.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseOptions
import com.google.firebase.FirebaseApp.initializeApp
import com.google.firebase.cloud.FirestoreClient
import org.slf4j.Logger
import org.webdevandsausages.events.models.Event
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.Date

fun String.asResourceStream(): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(this)

object FirebaseService {
    var db: Firestore? = null
    var logger: Logger? = null
    val serviceAccount = "firebaseServiceAccountKey.json".asResourceStream()
    val options: FirebaseOptions = FirebaseOptions.Builder().run {
        setCredentials(GoogleCredentials.fromStream(serviceAccount))
        setDatabaseUrl("https://wds-event-dev.firebaseio.com")
        build()
    }

    init {
        initializeApp(options)
        logger = LoggerFactory.getLogger("firebase")
    }

    @Suppress("UNCHECKED_CAST")
    fun getAllEvents(): List<Event>? {
        val eventsQuery = FirestoreClient.getFirestore()?.collection("events")?.get()
        return try {
            eventsQuery?.get()?.documents?.toList()?.map {
                // FIXME: why does deserialization fail if I just pass the class: it.toObject(Event::class.java)
                Event(
                    id = it["id"] as String,
                    contact = it["contact"] as String,
                    details = it["details"] as String,
                    datetime = it["datetime"] as Date,
                    location = it["location"] as String,
                    registered = it["registered"] as List<Map<String, String>>,
                    waitListed = it["waitListed"] as List<Map<String, String>>,
                    maxParticipants = it["maxParticipants"] as Long,
                    volume = it["volume"] as Long,
                    sponsor = it["sponsor"] as String,
                    sponsorWWWLink = it["sponsorWWWLink"] as String,
                    registrationOpens = it["registrationOpens"] as Date,
                    registrationCloses = it["registrationCloses"] as Date,
                    feedback = it["feedback"] as List<String>
                    )
            }
        } catch (e: Exception) {
            logger?.error("Firebase failed to return events: ${e.message}")
            emptyList()
        }
    }
}