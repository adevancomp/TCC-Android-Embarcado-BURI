package br.edu.uea.buri.controller

import br.edu.uea.buri.domain.Equipment
import br.edu.uea.buri.dto.equipment.requests.EquipmentRegisterDTO
import br.edu.uea.buri.dto.equipment.views.EquipmentViewDTO
import br.edu.uea.buri.service.IEquipmentService
import br.edu.uea.buri.service.IUserService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/equipment")
class EquipmentResource(
    private val equipmentService: IEquipmentService,
    private val userService: IUserService
) {
    @PostMapping
    fun save(@RequestBody @Valid dto: EquipmentRegisterDTO) : ResponseEntity<EquipmentViewDTO>{
        val equipment = dto.toEntity()
        val user = userService.findById(dto.ownerId)
        equipment.owner = user
        val equipmentSaved = equipmentService.save(equipment)
        return ResponseEntity.status(HttpStatus.CREATED).body(equipmentSaved.toEquipmentViewDTO())
    }
    @GetMapping("/{id}")
    fun findById(@PathVariable id: String) : ResponseEntity<EquipmentViewDTO>{
        val equipmentView = equipmentService.findById(id)
        return ResponseEntity.status(HttpStatus.OK).body(equipmentView.toEquipmentViewDTO())
    }
    @GetMapping
    fun findAllByOwnerId(@RequestParam("ownerId") ownerId: UUID) : ResponseEntity<List<EquipmentViewDTO>>{
        val list = equipmentService.findByOwnerId(id = ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(list.map { item -> item.toEquipmentViewDTO() })
    }
    @GetMapping("/all")
    fun findAllEquipments(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "10") size: Int
    ) : Page<EquipmentViewDTO>{
        val equipmentPage = this.equipmentService.findAllEquipments(page,size)
        return equipmentPage.map { equipment: Equipment? -> equipment?.toEquipmentViewDTO() }
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: String){
        equipmentService.deleteById(id)
    }
}