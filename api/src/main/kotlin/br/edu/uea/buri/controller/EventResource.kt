package br.edu.uea.buri.controller

import br.edu.uea.buri.domain.EnvironmentEvent
import br.edu.uea.buri.dto.event.EventView
import br.edu.uea.buri.service.IEnvironmentEventService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/event")
class EventResource (
    private val service : IEnvironmentEventService
) {
    @GetMapping
    fun realTime(@RequestParam("eqpId") equipmentId: String) : ResponseEntity<EventView> {
        val event = service.findTopByEquipmentIdOrderByDateDesc(equipmentId)
        return ResponseEntity.status(HttpStatus.OK).body(event?.toEventView())
    }
}