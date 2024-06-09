package br.edu.uea.buri.service.impl

import br.edu.uea.buri.domain.UserApp
import br.edu.uea.buri.exception.DomainException
import br.edu.uea.buri.repository.UserAppRepository
import br.edu.uea.buri.service.IUserService
import jakarta.validation.constraints.Email
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val repo: UserAppRepository
) : IUserService {
    override fun save(user: UserApp): UserApp = this.repo.save(user)

    override fun findById(id: UUID): UserApp = this.repo.findById(id).orElseThrow{
        throw DomainException("Usuário com id $id não encontrado!")
    }

    override fun existsById(id: UUID): Boolean = this.repo.existsById(id)
    override fun existsByEmail(email: String): Boolean = this.repo.existsByEmail(email)

    override fun deleteById(id: UUID) {
        this.repo.deleteById(id)
    }
}