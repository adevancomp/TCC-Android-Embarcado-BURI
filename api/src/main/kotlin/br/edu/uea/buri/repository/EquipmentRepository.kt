package br.edu.uea.buri.repository

import br.edu.uea.buri.domain.Equipment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EquipmentRepository : JpaRepository<Equipment, String> {
    fun findByOwnerId(id:UUID) : List<Equipment>
}