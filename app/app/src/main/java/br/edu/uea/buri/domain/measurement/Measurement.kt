package br.edu.uea.buri.domain.measurement

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class Measurement(
    @JsonProperty("id") val id: Long,
    @JsonProperty("carbonMonoxide") val co: BigDecimal?,
    @JsonProperty("airHumidity") val air: BigDecimal?,
    @JsonProperty("temperature") val tmp: BigDecimal?,
    @JsonProperty("collectionDate") val collectionDate: ZonedDateTime,
    @JsonProperty("equipmentId") val equipmentId: String
)