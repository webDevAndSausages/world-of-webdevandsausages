import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.fx.IODispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import meta.enums.ParticipantStatus
import org.webdevandsausages.events.dao.EventCRUD
import org.webdevandsausages.events.dao.ParticipantCRUD
import org.webdevandsausages.events.domain.EventError
import org.webdevandsausages.events.dto.EventDto
import org.webdevandsausages.events.dto.ParticipantDto
import org.webdevandsausages.events.dto.RegistrationInDto
import org.webdevandsausages.events.dto.getNextOrderNumber
import org.webdevandsausages.events.dto.getNextOrderNumberInStatusGroup
import org.webdevandsausages.events.service.EmailService
import org.webdevandsausages.events.service.FirebaseService
import org.webdevandsausages.events.service.event.canRegister
import org.webdevandsausages.events.utils.RandomWordsUtil
import org.webdevandsausages.events.utils.createLogger

class CreateRegistrationService(
    val eventCRUD: EventCRUD,
    val participantCRUD: ParticipantCRUD,
    val randomWordsUtil: RandomWordsUtil,
    val emailService: EmailService,
    val firebaseService: FirebaseService
) : CoroutineScope by CoroutineScope(Dispatchers.Default) {
    operator fun invoke(registration: RegistrationInDto): Either<EventError, ParticipantDto?> {
        val eventData: Option<EventDto> = eventCRUD.findByIdOrLatest(registration.eventId)

        val logger = createLogger()

        return when (eventData) {
            is None -> Either.left(EventError.NotFound)
            is Some -> when {
                !eventData.t.event.status.canRegister -> Either.left(EventError.NotFound)
                eventData.t.participants.find { it.email == registration.email && it.status != ParticipantStatus.CANCELLED } != null -> Either.left(
                    EventError.AlreadyRegistered
                )
                else -> {
                    val event = eventData.t.event
                    val numRegistered =
                        eventData.t.participants.filter { it.status == ParticipantStatus.REGISTERED }.size
                    // postgres trigger should flip status to full when limit is hit
                    val status =
                        if (numRegistered < event.maxParticipants) ParticipantStatus.REGISTERED else ParticipantStatus.WAIT_LISTED
                    val token = getVerificationToken()
                    val nextNumber = eventData.t.participants.getNextOrderNumber()
                    val registrationWithToken =
                        registration.copy(registrationToken = token, orderNumber = nextNumber, status = status)
                    val result = participantCRUD.create(registrationWithToken)

                    if (result is Some) {
                        launch(IODispatchers.CommonPool) {
                            emailService.sendRegistrationEmail(event, status, result.t)
                            firebaseService.upsertParticipantToMailingList(result.t)
                        }
                    }

                    return when (result) {
                        is Some -> {
                            val resultWithReadableOrderNumber = result.t.copy(
                                orderNumber = eventData.t.participants.getNextOrderNumberInStatusGroup(status)
                            )
                            Either.Right(resultWithReadableOrderNumber)
                        }
                        is None -> Either.Left(EventError.DatabaseError)
                    }
                }
            }
        }
    }

    fun getVerificationToken(): String {
        var token: String?
        do {
            token = runCatching { randomWordsUtil.getWordPair() }.getOrNull()
        } while (token !is String || participantCRUD.findByToken(token).isDefined())
        return token
    }
}