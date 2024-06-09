package br.edu.uea.buri.repository

import br.edu.uea.buri.domain.Equipment
import org.springframework.data.jpa.repository.JpaRepository

interface EquipmentRepository : JpaRepository<Equipment, String> {

}