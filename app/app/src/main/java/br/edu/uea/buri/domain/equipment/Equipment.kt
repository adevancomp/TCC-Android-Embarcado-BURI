package br.edu.uea.buri.domain.equipment

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Equipment(
    @JsonProperty("id") val id: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("ownerId") val ownerId: UUID? = null
) : Parcelable