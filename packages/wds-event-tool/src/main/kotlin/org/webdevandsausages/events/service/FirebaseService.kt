package org.webdevandsausages.events.service

import arrow.core.Try
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.SetOptions
import com.google.firebase.FirebaseApp.initializeApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import org.slf4j.LoggerFactory
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.participantDetails
import java.io.InputStream

fun String.asResourceStream(): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(this)

class FirebaseService {
    val logger = LoggerFactory.getLogger("firebase")

    init {
        val serviceAccount = "firebaseServiceAccountKey.json".asResourceStream()

        val options: FirebaseOptions = FirebaseOptions.Builder().run {
            setCredentials(GoogleCredentials.fromStream(serviceAccount))
            setDatabaseUrl("https://wds-event-dev.firebaseio.com")
            build()
        }

        initializeApp(options)
    }

    fun upsertParticipantToMailingList(participant: ParticipantDto) {
        val participantsRef = FirestoreClient.getFirestore()?.collection("participants")
        val details = participant.participantDetails
        val email = details["email"]
        if (email != null) {
            Try { participantsRef?.document(email)?.set(details, SetOptions.merge()) }.fold({
                logger?.error("Firebase failed to save $email to mailing list: ${it.message}")
            },
            {
                logger?.info("Firebase saved $email to mailing list.")
            })
        }
    }
}