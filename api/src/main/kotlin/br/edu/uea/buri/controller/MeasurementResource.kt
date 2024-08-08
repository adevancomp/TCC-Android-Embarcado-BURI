package br.edu.uea.buri.controller

import br.edu.uea.buri.domain.Measurement
import br.edu.uea.buri.dto.measurement.requests.MeasurementRegisterDTO
import br.edu.uea.buri.dto.measurement.views.MeasurementViewDTO
import br.edu.uea.buri.service.IEquipmentService
import br.edu.uea.buri.service.IMeasurementService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/measurement")
class MeasurementResource (
    private val measurementService: IMeasurementService,
    private val equipmentService: IEquipmentService
){
    @PostMapping
    fun save(@RequestBody @Valid dto: MeasurementRegisterDTO) : ResponseEntity<MeasurementViewDTO>{
        val measurement = dto.toEntity()
        measurement.equipment = equipmentService.findById(dto.equipmentId)
        val measurementSaved = measurementService.save(measurement)
        return ResponseEntity.status(HttpStatus.CREATED).body(measurementSaved.toMeasurementViewDTO())
    }
    @PostMapping("/items")
    fun saveItems(@RequestBody listDto: List<MeasurementRegisterDTO>) : ResponseEntity<String>{
        val measurements = listDto.map { item -> item.toEntity() }
        measurements.stream().forEach(measurementService::save)
        return ResponseEntity.status(HttpStatus.CREATED).body("Os ${measurements.size} foram salvos")
    }
    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) : ResponseEntity<MeasurementViewDTO>{
        val measurement = this.measurementService.findById(id)
        return ResponseEntity.status(HttpStatus.OK).body(measurement.toMeasurementViewDTO())
    }
    @GetMapping("/hour")
    fun findByHourDuration(@RequestParam("equipmentId") equipmentId: String, @RequestParam("hourDuration") hourDuration: Int) : ResponseEntity<List<MeasurementViewDTO>>{
        val measurements = this.measurementService.findAllByLastHourInterval(equipmentId,hourDuration)
        return ResponseEntity.status(HttpStatus.OK).body(measurements.map { measurement -> measurement.toMeasurementViewDTO() })
    }
    @GetMapping("/minute")
    fun findByMinuteDuration(@RequestParam("equipmentId") equipmentId: String, @RequestParam("minuteDuration") minuteDuration: Int) : ResponseEntity<List<MeasurementViewDTO>>{
        val measurements = this.measurementService.findAllByLastMinuteInterval(equipmentId,minuteDuration)
        return ResponseEntity.status(HttpStatus.OK).body(measurements.map { measurement -> measurement.toMeasurementViewDTO() })
    }
    @GetMapping("/equipment/{id}")
    fun getMeasurementsByEquipmentIdSorted(
        @PathVariable("id") equipmentId: String,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "10") size: Int) : Page<MeasurementViewDTO>{
        val pg = this.measurementService.findAllByEquipmentsSortedByDataCollection(equipmentId,page,size)
        val pgView = pg.map { measurement: Measurement -> measurement.toMeasurementViewDTO() }
        return pgView
    }
}