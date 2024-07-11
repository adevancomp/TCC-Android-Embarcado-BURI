package br.edu.uea.buri.config.security

import br.edu.uea.buri.repository.UserAppRepository
import jakarta.transaction.Transactional
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

typealias ApplicationUser = br.edu.uea.buri.domain.UserApp

@Service
class CustomUserDetailsService (
    private val userAppRepository: UserAppRepository
) : UserDetailsService {
    @Transactional
    override fun loadUserByUsername(username: String): UserDetails  {
        val user = userAppRepository.findByEmail(username).getOrNull() ?: throw UsernameNotFoundException("Usuário com name $username não encontrado")
        return User(user.email, user.passwordEncrypted, true, true, true, true,mutableListOf(SimpleGrantedAuthority(user.role.toString())))
    }
}