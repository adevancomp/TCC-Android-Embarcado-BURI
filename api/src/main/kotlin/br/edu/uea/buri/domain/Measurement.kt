package br.edu.uea.buri.domain

import br.edu.uea.buri.dto.measurement.views.MeasurementViewDTO
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
data class Measurement(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(precision = 7, scale = 4)
    val carbonMonoxide: BigDecimal? = null,
    @Column(precision = 7, scale = 4)
    val airHumidity: BigDecimal? = null,
    @Column(precision = 7, scale = 4)
    val temperature: BigDecimal? = null,
    @Column(nullable = false)
    val collectionDate: LocalDateTime,
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