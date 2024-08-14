package br.edu.uea.buri.domain.event

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

data class EnviromentEvent(
    @JsonProperty("id") val id: Long,
    @JsonProperty("type") val type: String,
    @JsonProperty("message") val message: String,
    @JsonProperty("date") val date: ZonedDateTime,
    @JsonProperty("equipmentId") val equipmentId: String?
)
