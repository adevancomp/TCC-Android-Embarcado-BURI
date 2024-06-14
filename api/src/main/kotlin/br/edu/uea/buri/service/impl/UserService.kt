package br.edu.uea.buri.service.impl

import br.edu.uea.buri.domain.UserApp
import br.edu.uea.buri.exception.DomainException
import br.edu.uea.buri.repository.EquipmentRepository
import br.edu.uea.buri.repository.UserAppRepository
import br.edu.uea.buri.service.IUserService
import jakarta.transaction.Transactional
import jakarta.validation.constraints.Email
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val repo: UserAppRepository,
    private val eqpRepo: EquipmentRepository,
    private val encoder: PasswordEncoder
) : IUserService {
    @Transactional
    override fun save(user: UserApp): UserApp = this.repo.save(user)

    override fun findById(id: UUID): UserApp = this.repo.findById(id).orElseThrow{
        throw DomainException("Usuário com id $id não encontrado!")
    }

    override fun existsById(id: UUID): Boolean = this.repo.existsById(id)
    override fun existsByEmail(email: String): Boolean = this.repo.existsByEmail(email)

    @Transactional
    override fun deleteById(id: UUID) {
        var user = this.findById(id)
        user.equipments.forEach{
            equipment ->
                equipment.owner=null
                eqpRepo.save(equipment)
        }
        this.repo.delete(user)
    }
}