package br.edu.uea.buri.domain

import br.edu.uea.buri.dto.equipment.views.EquipmentViewDTO
import jakarta.persistence.*

@Entity
data class Equipment(
    @Id
    val id: String,
    @Column(length = 100)
    var name: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    var owner: UserApp? = null,
    @OneToMany(mappedBy = "equipment",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST, CascadeType.REMOVE],
        orphanRemoval = true
    ) val measurements: List<Measurement> = mutableListOf()
){
    fun toEquipmentViewDTO() : EquipmentViewDTO = EquipmentViewDTO(
        id = this.id,
        name = this.name,
        ownerId = this.owner?.id
    )
}