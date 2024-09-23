package br.edu.uea.buri.domain.measurement

import java.math.BigDecimal
import java.time.ZonedDateTime

data class RawMeasurement(
    val co: BigDecimal?,
    val air: BigDecimal?,
    val tmp: BigDecimal?,
    val collectionDate: ZonedDateTime,
    val equipmentId: String
)
