package br.edu.uea.buri.dto.measurement.views

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class MeasurementViewDTO(
    val id: Long,
    val carbonMonoxide: BigDecimal?,
    val airHumidity: BigDecimal?,
    val temperature: BigDecimal?,
    val collectionDate: ZonedDateTime,
    val equipmentId: String
)
