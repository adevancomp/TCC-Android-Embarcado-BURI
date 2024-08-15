package br.edu.uea.buri.controller

import br.edu.uea.buri.config.security.AuthResponse
import br.edu.uea.buri.domain.EnvironmentEvent
import br.edu.uea.buri.domain.Measurement
import br.edu.uea.buri.dto.equipment.response.EquipmentNewId
import br.edu.uea.buri.dto.measurement.requests.MeasurementRegisterDTO
import br.edu.uea.buri.dto.measurement.views.MeasurementViewDTO
import br.edu.uea.buri.dto.user.requests.LoginDTO
import br.edu.uea.buri.dto.user.requests.UserRegisterDTO
import br.edu.uea.buri.dto.user.views.UserViewDTO
import br.edu.uea.buri.enums.EventType
import br.edu.uea.buri.exception.SensorNotConnectedException
import br.edu.uea.buri.service.IEnvironmentEventService
import br.edu.uea.buri.service.IEquipmentService
import br.edu.uea.buri.service.IMeasurementService
import br.edu.uea.buri.service.IUserService
import br.edu.uea.buri.utils.DomainOperations
import br.edu.uea.buri.utils.DomainOperations.Companion.airHumidityToMessage
import br.edu.uea.buri.utils.DomainOperations.Companion.coToMessage
import br.edu.uea.buri.utils.DomainOperations.Companion.temperatureToMessage
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.concurrent.thread

@RestController
@RequestMapping("/auth")
class AuthResource (
    private val authManager: AuthenticationManager,
    private val userService: IUserService,
    private val encoder: PasswordEncoder,
    private val measurementService: IMeasurementService,
    private val equipmentService: IEquipmentService,
    private val eventService: IEnvironmentEventService
){
    @PostMapping
    fun login(@RequestBody @Valid dto: LoginDTO) : ResponseEntity<AuthResponse>{
        val authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                dto.email, dto.password
        )
        val userId = userService.findByEmail(dto.email).id!!
        val authenticationResponse = authManager.authenticate(authenticationRequest)
        val authResponse = AuthResponse(authenticated = authenticationResponse.isAuthenticated, name = dto.email, id = userId)
        SecurityContextHolder.getContext().authentication = authenticationResponse
        return ResponseEntity.status(HttpStatus.OK).body(authResponse)
    }
    @PostMapping("/register")
    fun save(@RequestBody @Valid dto: UserRegisterDTO) : ResponseEntity<UserViewDTO>{
        val user = dto.toEntity()
        user.passwordEncrypted = encoder.encode(user.passwordEncrypted)
        val userSaved = userService.save(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(userSaved.toUserViewDTO())
    }

    @PostMapping("/measurement")
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

    @GetMapping("/generateId")
    fun generateEquipmentId() : ResponseEntity<EquipmentNewId>{
        var newId : String
        var existsId : Boolean
        do {
            newId = DomainOperations.generateEquipmentId()
            existsId = equipmentService.existsById(newId)
        } while (existsId)
        val eqpId = EquipmentNewId(newId)
        return ResponseEntity.status(HttpStatus.OK).body(eqpId)
    }
}