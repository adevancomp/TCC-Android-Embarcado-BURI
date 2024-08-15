package br.edu.uea.buri.controller

import br.edu.uea.buri.domain.EnvironmentEvent
import br.edu.uea.buri.domain.Measurement
import br.edu.uea.buri.dto.measurement.requests.MeasurementRegisterDTO
import br.edu.uea.buri.dto.measurement.views.MeasurementViewDTO
import br.edu.uea.buri.enums.EventType
import br.edu.uea.buri.exception.SensorNotConnectedException
import br.edu.uea.buri.service.IEnvironmentEventService
import br.edu.uea.buri.service.IEquipmentService
import br.edu.uea.buri.service.IMeasurementService
import br.edu.uea.buri.utils.DomainOperations.Companion.airHumidityToMessage
import br.edu.uea.buri.utils.DomainOperations.Companion.coToMessage
import br.edu.uea.buri.utils.DomainOperations.Companion.temperatureToMessage
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.concurrent.thread

@RestController
@RequestMapping("/measurement")
class MeasurementResource (
    private val measurementService: IMeasurementService,
    private val equipmentService: IEquipmentService,
    private val eventService: IEnvironmentEventService
){
    @PostMapping
    fun saveMeasurement(@RequestBody @Valid dto: MeasurementRegisterDTO) : ResponseEntity<MeasurementViewDTO>{
        val measurement = dto.toEntity()
        measurement.equipment = equipmentService.findById(dto.equipmentId)

        if(!measurementService.sensorsAreConnected(dto.equipmentId)){
            if(measurement.airHumidity==null || measurement.carbonMonoxide==null || measurement.temperature==null){
                eventService.save(
                    EnvironmentEvent(
                        id = null,
                        type = EventType.SensorsNotConnected,
                        message = "Sensores do ${dto.equipmentId} não conectados",
                        date = ZonedDateTime.now(ZoneOffset.UTC),
                        equipment = measurement.equipment
                    )
                )
                throw SensorNotConnectedException("Sensores não conectados!!!")
            }
        }

        val measurementSaved = measurementService.save(measurement)

        thread(start = true){
            var event : EnvironmentEvent? = null
            val lastEvent: EnvironmentEvent? = eventService.findTopByEquipmentIdOrderByDateDesc(dto.equipmentId)

            measurementSaved.temperature?.let { temp ->
                if(temp<BigDecimal(15.0) || temp<BigDecimal(35.0)){
                    event = EnvironmentEvent(id = null, type = EventType.Temperature, message = temperatureToMessage(temp), date = measurementSaved.collectionDate, equipment = measurementSaved.equipment)
                }
            }

            measurementSaved.airHumidity?.let { airH ->
                if(airH<=BigDecimal(0.62)){
                    event = EnvironmentEvent(id = null, type = EventType.AirHumidity, message = airHumidityToMessage(airH), date = measurementSaved.collectionDate, equipment = measurementSaved.equipment)
                }
            }

            measurementSaved.carbonMonoxide?.let { co ->
                if(co>BigDecimal(10.0)){
                    event = EnvironmentEvent(id = null, type = EventType.CarbonMonoxide, message = coToMessage(co), date = measurementSaved.collectionDate, equipment = measurementSaved.equipment)
                }
            }

            if(lastEvent==null && event!=null){
                eventService.save(event!!)
            } else if (lastEvent!=null && event!=null){
                if(lastEvent.type==event!!.type){
                    if(lastEvent.message==event!!.message){
                        eventService.deleteById(lastEvent.id!!)
                        eventService.save(event!!)
                    }
                } else {
                    eventService.save(event!!)
                }
            }
        }

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