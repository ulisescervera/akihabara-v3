package com.gmail.uli153.akihabara3.utils.extensions

import com.gmail.uli153.akihabara3.domain.models.Poll
import com.gmail.uli153.akihabara3.domain.models.PollType

val List<Poll>.bestPlayerNumber: String? get() {
    val poll = this.find { it.type == PollType.SUGGESTED_NUMPLAYERS } ?: return null
     return poll.results.maxByOrNull {
         it.result.filter { it.value.lowercase() == "best" && it.votes > 0 }.maxOfOrNull { it.votes } ?: 0
     }?.players
}

val List<Poll>.bestPlayerAge: String? get() {
    val poll = this.find { it.type == PollType.SUGGESTED_PLAYERAGE } ?: return null
    return poll.results.flatMap { it.result }.filter { it.votes > 0 }.maxByOrNull { it.votes }?.value
}

val List<Poll>.languageDependency: String? get() {
    val poll = this.find { it.type == PollType.LANGUAGE_DEPENDENCE } ?: return null
    return poll.results.flatMap { it.result }.filter { it.votes > 0 }.maxByOrNull { it.votes }?.value
}
