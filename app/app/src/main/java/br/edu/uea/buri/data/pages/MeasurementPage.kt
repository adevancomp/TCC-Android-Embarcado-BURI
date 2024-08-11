package br.edu.uea.buri.data.pages

import br.edu.uea.buri.domain.measurement.Measurement
import com.fasterxml.jackson.annotation.JsonProperty

data class MeasurementPage(
    @JsonProperty("totalPages") val totalPages: Int,
    @JsonProperty("totalElements") val totalElements: Int,
    @JsonProperty("size") val size: Int,
    @JsonProperty("content") val measurements: List<Measurement>,
    @JsonProperty("empty") val isEmpty: Boolean
)
