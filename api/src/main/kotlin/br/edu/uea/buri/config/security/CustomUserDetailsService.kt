package br.edu.uea.buri.config.security

import br.edu.uea.buri.repository.UserAppRepository
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
    override fun loadUserByUsername(username: String): UserDetails  =
        userAppRepository.findByEmail(username).getOrNull()?.toUserDetails() ?: throw UsernameNotFoundException("Usuário com name $username não encontrado")

    private fun ApplicationUser.toUserDetails(): UserDetails =
        User
            .builder()
            .username(this.email)
            .password(this.passwordEncrypted)
            .roles(this.role.toString()).build()
}