package br.edu.uea.buri.repository

import br.edu.uea.buri.domain.Measurement
import org.springframework.data.jpa.repository.JpaRepository

interface MeasurementRepository : JpaRepository<Measurement, Long> {
}