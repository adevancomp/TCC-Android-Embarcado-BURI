package br.edu.uea.buri.domain.user

import br.edu.uea.buri.domain.equipment.Equipment
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class User(
    @JsonProperty("id") val userId: UUID,
    @JsonProperty("name") val name: String,
    @JsonProperty("email") val email: String,
    @JsonProperty("role")  val role: String,
    @JsonProperty("equipments") val equipments: List<Equipment>
)
