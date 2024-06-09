package br.edu.uea.buri.service.impl

import br.edu.uea.buri.domain.Measurement
import br.edu.uea.buri.exception.DomainException
import br.edu.uea.buri.repository.MeasurementRepository
import br.edu.uea.buri.service.IMeasurementService
import org.springframework.stereotype.Service

@Service
class MeasurementService (
    private val repo: MeasurementRepository
) : IMeasurementService {
    override fun save(measurement: Measurement): Measurement = this.repo.save(measurement)

    override fun findById(id: Long): Measurement = this.repo.findById(id).orElseThrow{
        throw DomainException("Medição com id $id não encontrado!")
    }

    override fun existsById(id: Long): Boolean = this.repo.existsById(id)

    override fun deleteById(id: Long) {
        this.repo.deleteById(id)
    }

}