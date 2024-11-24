package br.edu.uea.buri.service

import br.edu.uea.buri.domain.Equipment
import br.edu.uea.buri.domain.Measurement
import org.springframework.data.domain.Page

interface IMeasurementService {
    fun save(measurement: Measurement) : Measurement
    fun findById(id: Long) : Measurement
    fun findByEquipment(equipment: Equipment) : List<Measurement>
    fun findAllByEquipmentsSortedByDataCollection(equipmentId: String, page:Int,size:Int) : Page<Measurement>
    fun findAllByLastHourInterval(equipmentId:String,hourDuration: Int) : List<Measurement>
    fun findAllByLastMinuteInterval(equipmentId:String,minuteDuration: Int): List<Measurement>
    fun findLast3Measurements(equipmentId: String) : List<Measurement>
    fun sensorsAreConnected(equipmentId: String) : Boolean
    fun existsById(id: Long) : Boolean
    fun deleteById(id: Long)
}