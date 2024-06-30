package br.edu.uea.buri.service

import br.edu.uea.buri.domain.Equipment
import br.edu.uea.buri.domain.Measurement
import org.springframework.data.domain.Page
import java.util.UUID

interface IEquipmentService {
    fun save(equipment: Equipment) : Equipment
    fun findById(id: String) : Equipment
    fun findAllEquipments(page:Int,size:Int): Page<Equipment>
    fun findByOwnerId(id: UUID) : List<Equipment>
    fun existsById(id: String) : Boolean
    fun deleteById(id: String)
}