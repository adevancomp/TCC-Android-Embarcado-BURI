package br.edu.uea.buri.domain

import br.edu.uea.buri.dto.event.EventView
import br.edu.uea.buri.enums.EventType
import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
data class EnvironmentEvent(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Enumerated(value = EnumType.STRING)
    val type: EventType = EventType.General,
    @Column(nullable = false, columnDefinition = "TEXT")
    val message: String = "",
    @Column(nullable = false)
    val date: ZonedDateTime = ZonedDateTime.now(),
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id")
    val equipment: Equipment? = null
) {
    fun toEventView() : EventView = EventView(
        id = this.id!!,
        type = this.type.toString(),
        message = this.message,
        date = this.date,
        equipmentId = this.equipment?.id
    )
}