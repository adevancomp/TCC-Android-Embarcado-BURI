package br.edu.uea.buri.service

import br.edu.uea.buri.domain.Equipment
import br.edu.uea.buri.domain.Measurement

interface IEquipmentService {
    fun save(equipment: Equipment) : Equipment
    fun findById(id: String) : Equipment
    fun existsById(id: String) : Boolean
    fun deleteById(id: String)
}