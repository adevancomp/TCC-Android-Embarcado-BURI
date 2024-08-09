package br.edu.uea.buri.controller

import br.edu.uea.buri.config.security.AuthResponse
import br.edu.uea.buri.dto.measurement.requests.MeasurementRegisterDTO
import br.edu.uea.buri.dto.measurement.views.MeasurementViewDTO
import br.edu.uea.buri.dto.user.requests.LoginDTO
import br.edu.uea.buri.dto.user.requests.UserRegisterDTO
import br.edu.uea.buri.dto.user.views.UserViewDTO
import br.edu.uea.buri.exception.SensorNotConnectedException
import br.edu.uea.buri.service.IEnvironmentEventService
import br.edu.uea.buri.service.IEquipmentService
import br.edu.uea.buri.service.IMeasurementService
import br.edu.uea.buri.service.IUserService
import br.edu.uea.buri.utils.DomainOperations
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

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
        if(!measurementService.sensorsAreConnected(dto.equipmentId)){
            throw SensorNotConnectedException("Sensores não conectados!!!")
        }
        measurement.equipment = equipmentService.findById(dto.equipmentId)
        val measurementSaved = measurementService.save(measurement)
        return ResponseEntity.status(HttpStatus.CREATED).body(measurementSaved.toMeasurementViewDTO())
    }
    @GetMapping("/generateId")
    fun generateEquipmentId() : ResponseEntity<String>{
        var newId : String
        var existsId : Boolean
        do {
            newId = DomainOperations.generateEquipmentId()
            existsId = equipmentService.existsById(newId)
        } while (existsId)
        return ResponseEntity.status(HttpStatus.OK).body(newId)
    }
}