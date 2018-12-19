package org.webdevandsausages.events.services

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseOptions
import com.google.firebase.FirebaseApp.initializeApp
import com.google.firebase.cloud.FirestoreClient
import java.io.InputStream
import java.util.*

fun String.asResourceStream(): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(this)

object FirebaseService {
    var db: Firestore? = null
    val serviceAccount = "firebaseServiceAccountKey.json".asResourceStream()
    val options: FirebaseOptions = FirebaseOptions.Builder().run {
        setCredentials(GoogleCredentials.fromStream(serviceAccount))
        setDatabaseUrl("https://wds-event-dev.firebaseio.com")
        build()
    }

    init {
        initializeApp(options)
        db = FirestoreClient.getFirestore()
    }

    data class Event(val details: String?, val date: Date?, val location: String?)

    fun getAllEvents(): List<Event>? {
        val eventsQuery = db?.collection("events")?.get()
        return eventsQuery?.get()?.documents?.toList()?.map {
            Event(it["details"] as String, it["datetime"] as Date, it["location"] as String)
        }
    }
}