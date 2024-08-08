package br.edu.uea.buri.repository

import br.edu.uea.buri.domain.EnvironmentEvent
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EnvironmentEventRepository : JpaRepository<EnvironmentEvent, Long>{
    fun findTopByEquipmentIdOrderByDateDesc(equipmentId: String): EnvironmentEvent?
}