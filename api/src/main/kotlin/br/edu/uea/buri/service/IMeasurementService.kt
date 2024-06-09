package br.edu.uea.buri.service

import br.edu.uea.buri.domain.Measurement

interface IMeasurementService {
    fun save(measurement: Measurement) : Measurement
    fun findById(id: Long) : Measurement
    fun existsById(id: Long) : Boolean
    fun deleteById(id: Long)
}