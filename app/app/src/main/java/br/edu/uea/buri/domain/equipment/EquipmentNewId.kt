package br.edu.uea.buri.domain.equipment

import com.fasterxml.jackson.annotation.JsonProperty

data class EquipmentNewId(
    @JsonProperty("id") val id: String
)
