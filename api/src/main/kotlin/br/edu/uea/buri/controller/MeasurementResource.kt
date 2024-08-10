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
import kotlin.concurrent.thread

@RestController
@RequestMapping("/measurement")
class MeasurementResource (
    private val measurementService: IMeasurementService,
    private val equipmentService: IEquipmentService,
    private val eventService: IEnvironmentEventService
){
    @PostMapping
    fun save(@RequestBody @Valid dto: MeasurementRegisterDTO) : ResponseEntity<MeasurementViewDTO>{
        val measurement = dto.toEntity()
        if(!measurementService.sensorsAreConnected(dto.equipmentId)){
            throw SensorNotConnectedException("Sensores não conectados!!!")
        }
        measurement.equipment = equipmentService.findById(dto.equipmentId)
        val measurementSaved = measurementService.save(measurement)
        thread(start = true){
            //Salvar um evento de acordo as informações de measurement
            if(measurementSaved.airHumidity!=null && measurementSaved.temperature!=null && measurementSaved.carbonMonoxide!=null){
                var event: EnvironmentEvent? = null
                var lastEvent: EnvironmentEvent = EnvironmentEvent()
                measurementSaved.equipment?.let {
                    lastEvent = eventService.findTopByEquipmentIdOrderByDateDesc(it.id)
                }

                if(measurementSaved.temperature>= BigDecimal(35) || measurementSaved.temperature<= BigDecimal(15)){
                    event = EnvironmentEvent(
                        type = EventType.Temperature,
                        message = temperatureToMessage(measurementSaved.temperature),
                        date = measurementSaved.collectionDate,
                        equipment = measurementSaved.equipment
                    )
                }
                if(measurementSaved.airHumidity<= BigDecimal(0.352) || (measurementSaved.airHumidity <= BigDecimal(0.46))){
                    event = EnvironmentEvent(
                        type = EventType.AirHumidity,
                        message = airHumidityToMessage(measurementSaved.airHumidity),
                        date = measurementSaved.collectionDate,
                        equipment = measurementSaved.equipment
                    )
                }
                if(measurementSaved.carbonMonoxide>= BigDecimal(11)){
                    event = EnvironmentEvent(
                        type = EventType.CarbonMonoxide,
                        message = coToMessage(measurementSaved.carbonMonoxide),
                        date = measurementSaved.collectionDate,
                        equipment = measurementSaved.equipment
                    )
                }

                event?.let {
                    if(lastEvent.id!=null){
                        //Tanto event como o last event existem
                        if(event.type!=lastEvent.type){
                            eventService.save(event)
                        } else {
                            //Os eventos sao do mesmo tipo, verificar se tem a mesma mensagem
                            if(event.message != lastEvent.message){
                                eventService.save(event)
                            } else {

                            }
                        }
                    } else {
                        //Primeiro evento cadastrado
                        eventService.save(event)
                    }
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