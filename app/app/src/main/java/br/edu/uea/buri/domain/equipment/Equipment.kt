package br.edu.uea.buri.domain.equipment

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Equipment(
    @JsonProperty("id") val equipmentId: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("ownerId") val ownerId: UUID? = null
)