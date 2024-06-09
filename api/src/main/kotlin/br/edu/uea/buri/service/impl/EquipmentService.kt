package br.edu.uea.buri.service.impl

import br.edu.uea.buri.domain.Equipment
import br.edu.uea.buri.domain.Measurement
import br.edu.uea.buri.exception.DomainException
import br.edu.uea.buri.repository.EquipmentRepository
import br.edu.uea.buri.service.IEquipmentService
import org.springframework.stereotype.Service

@Service
class EquipmentService(
    private val repo: EquipmentRepository
) : IEquipmentService {
    override fun save(equipment: Equipment): Equipment = this.repo.save(equipment)

    override fun findById(id: String): Equipment = this.repo.findById(id).orElseThrow{
        throw DomainException("Equipamento com id $id não encontrado!")
    }

    override fun existsById(id: String): Boolean = this.repo.existsById(id)

    override fun deleteById(id: String) {
        this.repo.deleteById(id)
    }
}