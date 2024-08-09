package br.edu.uea.buri.dto.event

import java.time.ZonedDateTime

data class EventView(
    val id: Long,
    val type: String,
    val message: String,
    val date: ZonedDateTime,
    val equipmentId: String?
)
