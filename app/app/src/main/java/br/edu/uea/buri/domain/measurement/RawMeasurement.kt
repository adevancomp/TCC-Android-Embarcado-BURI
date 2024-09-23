package br.edu.uea.buri.domain.measurement

import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime

data class RawMeasurement(
    val co: BigDecimal?,
    val air: BigDecimal?,
    val tmp: BigDecimal?,
    val collectionDate: ZonedDateTime,
    val equipmentId: String
) {
    fun toMeasurement() = Measurement(
        id = -1, // Invalid id
        co = this.co,
        air = this.air,
        tmp = this.tmp,
        collectionDate = ZonedDateTime.now(ZoneId.of("Amazonas/Manaus")), // Offline mode
        equipmentId = this.equipmentId
    )
}
