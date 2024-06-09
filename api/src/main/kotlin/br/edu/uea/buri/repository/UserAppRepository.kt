package br.edu.uea.buri.repository

import br.edu.uea.buri.domain.UserApp
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface UserAppRepository : JpaRepository<UserApp, UUID> {
    fun existsByEmail(email: String) : Boolean
    fun findByEmail(email: String) : Optional<UserApp>
}