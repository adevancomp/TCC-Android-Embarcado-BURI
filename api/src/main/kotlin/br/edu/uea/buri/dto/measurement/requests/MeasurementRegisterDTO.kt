package br.edu.uea.buri.dto.measurement.requests

import br.edu.uea.buri.domain.Measurement
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDateTime

data class MeasurementRegisterDTO (
    @Digits(integer = 3, fraction = 4)
    val co: BigDecimal? = null,
    @Digits(integer = 3, fraction = 4)
    val airH: BigDecimal? = null,
    @Digits(integer = 3, fraction = 4)
    val temp: BigDecimal? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    val collectionDate: LocalDateTime,
    @NotBlank(message = "equipmentId não pode ser vazio") @Size(min = 6, max = 6, message = "ID deve ter 6 caracteres")
    val equipmentId: String = ""
) {
    fun toEntity() : Measurement = Measurement(
        carbonMonoxide = co,
        airHumidity = airH,
        temperature = temp,
        collectionDate = collectionDate
    )
}