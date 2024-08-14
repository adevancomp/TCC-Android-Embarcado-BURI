package br.edu.uea.buri.service

import br.edu.uea.buri.domain.EnvironmentEvent

interface IEnvironmentEventService {
    fun save(event: EnvironmentEvent) : EnvironmentEvent
    fun findById(id: Long) : EnvironmentEvent
    fun existsById(id: Long) : Boolean
    fun findTopByEquipmentIdOrderByDateDesc(equipmentId: String) : EnvironmentEvent?
    fun deleteById(id: Long)
}