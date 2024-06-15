package br.edu.uea.buri.repository

import br.edu.uea.buri.domain.Equipment
import br.edu.uea.buri.domain.Measurement
import org.springframework.data.jpa.repository.JpaRepository

interface MeasurementRepository : JpaRepository<Measurement, Long> {
    fun findByEquipment(equipment: Equipment) : List<Measurement>
}