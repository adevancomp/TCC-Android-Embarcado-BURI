package br.edu.uea.buri.service.impl

import br.edu.uea.buri.domain.EnvironmentEvent
import br.edu.uea.buri.exception.DomainException
import br.edu.uea.buri.repository.EnvironmentEventRepository
import br.edu.uea.buri.service.IEnvironmentEventService
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class EnvironmentEventService (
    private val repo: EnvironmentEventRepository
): IEnvironmentEventService {
    @Transactional
    override fun save(event: EnvironmentEvent): EnvironmentEvent = repo.save(event)

    override fun findById(id: Long): EnvironmentEvent = repo.findById(id).orElseThrow {
        throw DomainException("Evento com id $id não encontrado!")
    }

    override fun existsById(id: Long): Boolean = repo.existsById(id)

    override  fun findTopByEquipmentIdOrderByDateDesc(equipmentId: String): EnvironmentEvent {
        val event = repo.findTopByEquipmentIdOrderByDateDesc(equipmentId) ?: throw DomainException("Event for equipment id $equipmentId is Null")
        return event
    }

    @Transactional
    override fun deleteById(id: Long) {
        repo.deleteById(id)
    }

}