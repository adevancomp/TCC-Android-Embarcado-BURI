package br.edu.uea.buri.controller

import br.edu.uea.buri.dto.measurement.requests.MeasurementRegisterDTO
import br.edu.uea.buri.dto.measurement.views.MeasurementViewDTO
import br.edu.uea.buri.service.IEquipmentService
import br.edu.uea.buri.service.IMeasurementService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/measurement")
class MeasurementResource (
    private val measurementService: IMeasurementService,
    private val equipmentService: IEquipmentService
){
    @PostMapping
    fun save(@RequestBody dto: MeasurementRegisterDTO) : ResponseEntity<MeasurementViewDTO>{
        val measurement = dto.toEntity()
        measurement.equipment = equipmentService.findById(dto.equipmentId)
        val measurementSaved = measurementService.save(measurement)
        return ResponseEntity.status(HttpStatus.CREATED).body(measurementSaved.toMeasurementViewDTO())
    }
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) : ResponseEntity<MeasurementViewDTO>{
        val measurement = this.measurementService.findById(id)
        return ResponseEntity.status(HttpStatus.OK).body(measurement.toMeasurementViewDTO())
    }
}