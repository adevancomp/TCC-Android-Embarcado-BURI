package br.edu.uea.buri.controller

import br.edu.uea.buri.dto.measurement.requests.MeasurementRegisterDTO
import br.edu.uea.buri.dto.measurement.views.MeasurementViewDTO
import br.edu.uea.buri.service.IEquipmentService
import br.edu.uea.buri.service.IMeasurementService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/m")
class MResource (
    private val measurementService: IMeasurementService,
    private val equipmentService: IEquipmentService
){
    @PostMapping
    fun save(@RequestBody @Valid dto: MeasurementRegisterDTO) : ResponseEntity<MeasurementViewDTO> {
        val measurement = dto.toEntity()
        measurement.equipment = equipmentService.findById(dto.equipmentId)
        val measurementSaved = measurementService.save(measurement)
        return ResponseEntity.status(HttpStatus.CREATED).body(measurementSaved.toMeasurementViewDTO())
    }
}