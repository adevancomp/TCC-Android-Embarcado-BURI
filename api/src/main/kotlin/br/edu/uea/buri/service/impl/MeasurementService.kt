package br.edu.uea.buri.service.impl

import br.edu.uea.buri.domain.Equipment
import br.edu.uea.buri.domain.Measurement
import br.edu.uea.buri.exception.DomainException
import br.edu.uea.buri.repository.EquipmentRepository
import br.edu.uea.buri.repository.MeasurementRepository
import br.edu.uea.buri.service.IMeasurementService
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

import org.springframework.stereotype.Service

@Service
class MeasurementService (
    private val repo: MeasurementRepository,
    private val eqpService: EquipmentService
) : IMeasurementService {
    @Transactional
    override fun save(measurement: Measurement): Measurement = this.repo.save(measurement)

    override fun findById(id: Long): Measurement = this.repo.findById(id).orElseThrow{
        throw DomainException("Medição com id $id não encontrado!")
    }

    override fun findByEquipment(equipment: Equipment): List<Measurement> {
        return this.repo.findByEquipment(equipment)
    }

    override fun findAllByEquipmentsSortedByDataCollection(equipmentId: String, page:Int,size:Int): Page<Measurement> {
        val equipment = eqpService.findById(equipmentId)
        val list = this.findByEquipment(equipment)
        val sortedList = list.sortedBy { it.collectionDate }
        val pageable: Pageable = PageRequest.of(page, size)
        val pageResult = PageImpl(sortedList, pageable, sortedList.size.toLong())
        return pageResult
    }

    override fun findAllByLastHourInterval(ownerId:String,hourDuration: Int): List<Measurement> = repo.findAllByLastHourInterval(ownerId,hourDuration)

    override fun findAllByLastMinuteInterval(ownerId:String,minuteDuration: Int): List<Measurement> = repo.findAllByLastMinuteInterval(ownerId,minuteDuration)

    override fun existsById(id: Long): Boolean = this.repo.existsById(id)

    @Transactional
    override fun deleteById(id: Long) {
        this.repo.deleteById(id)
    }

}