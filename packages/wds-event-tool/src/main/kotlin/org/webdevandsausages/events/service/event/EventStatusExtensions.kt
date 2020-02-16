package org.webdevandsausages.events.service.event

import meta.enums.EventStatus

val EventStatus.isVisibleStatus get() = this == EventStatus.VISIBLE
val EventStatus.isNotFull get() = this == EventStatus.OPEN
val EventStatus.isWithWaitList get() = this == EventStatus.OPEN_WITH_WAITLIST
val EventStatus.canRegister get() = this == EventStatus.OPEN || this == EventStatus.OPEN_WITH_WAITLIST
val EventStatus.isOpenFeedbackStatus get() = this == EventStatus.CLOSED_WITH_FEEDBACK
val EventStatus.isInvisible get() = this == EventStatus.PLANNING || this == EventStatus.CLOSED || this == EventStatus.CANCELLED
