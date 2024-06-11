package br.edu.uea.buri.dto.measurement.requests

import br.edu.uea.buri.domain.Measurement
import java.math.BigDecimal
import java.time.LocalDateTime

data class MeasurementRegisterDTO (
    val co: BigDecimal? = null,
    val airH: BigDecimal? = null,
    val temp: BigDecimal? = null,
    val collectionDate: LocalDateTime,
    val equipmentId: String
) {
    fun toEntity() : Measurement = Measurement(
        carbonMonoxide = co,
        airHumidity = airH,
        temperature = temp,
        collectionDate = collectionDate
    )
}