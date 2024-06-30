package br.edu.uea.buri.service.impl

import br.edu.uea.buri.domain.Equipment
import br.edu.uea.buri.domain.Measurement
import br.edu.uea.buri.exception.DomainException
import br.edu.uea.buri.repository.EquipmentRepository
import br.edu.uea.buri.service.IEquipmentService
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class EquipmentService(
    private val repo: EquipmentRepository
) : IEquipmentService {
    @Transactional
    override fun save(equipment: Equipment): Equipment = this.repo.save(equipment)

    override fun findById(id: String): Equipment = this.repo.findById(id).orElseThrow{
        throw DomainException("Equipamento com id $id não encontrado!")
    }

    override fun findAllEquipments(page: Int, size: Int): Page<Equipment> {
        return this.repo.findAll(PageRequest.of(page,size))
    }

    override fun findByOwnerId(id: UUID): List<Equipment> =
        this.repo.findByOwnerId(id)


    override fun existsById(id: String): Boolean = this.repo.existsById(id)

    @Transactional
    override fun deleteById(id: String) {
        this.repo.deleteById(id)
    }
}