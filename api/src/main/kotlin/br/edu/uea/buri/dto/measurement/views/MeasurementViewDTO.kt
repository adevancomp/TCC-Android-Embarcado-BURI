package br.edu.uea.buri.dto.measurement.views

import java.math.BigDecimal
import java.time.LocalDateTime

data class MeasurementViewDTO(
    val id: Long,
    val carbonMonoxide: BigDecimal?,
    val airHumidity: BigDecimal?,
    val temperature: BigDecimal?,
    val collectionDate: LocalDateTime,
    val equipmentId: String
)
