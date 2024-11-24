package br.edu.uea.buri.domain

import br.edu.uea.buri.dto.measurement.views.MeasurementViewDTO
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Entity
data class Measurement(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(precision = 15, scale = 4)
    val carbonMonoxide: BigDecimal? = null,
    @Column(precision = 10, scale = 4)
    val airHumidity: BigDecimal? = null,
    @Column(precision = 8, scale = 4)
    val temperature: BigDecimal? = null,
    @Column(nullable = false)
    val collectionDate: ZonedDateTime,
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    var equipment: Equipment? = null
) {
   fun toMeasurementViewDTO() : MeasurementViewDTO = MeasurementViewDTO(
       id = this.id!!,
       carbonMonoxide = this.carbonMonoxide,
       airHumidity = this.airHumidity,
       temperature = this.temperature,
       collectionDate = this.collectionDate,
       equipmentId = this.equipment!!.id
   )
}