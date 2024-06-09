package br.edu.uea.buri.service

import br.edu.uea.buri.domain.UserApp
import java.util.UUID

interface IUserService {
    fun save(user: UserApp) : UserApp
    fun findById(id: UUID) : UserApp
    fun existsById(id: UUID) : Boolean
    fun existsByEmail(email: String) : Boolean
    fun deleteById(id: UUID)
}