package br.edu.uea.buri.domain.user

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class UserAuth (
    @JsonProperty("authenticated") val authenticated: Boolean = false,
    @JsonProperty("name") val name: String = "",
    @JsonProperty("id") val id: UUID = UUID.randomUUID()
)