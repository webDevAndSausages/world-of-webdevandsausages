package org.webdevandsausages.events.services

import meta.enums.ParticipantStatus

val ParticipantStatus.toText get() = this.name.toLowerCase().replace("_", " ")