package br.edu.uea.buri.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain

@Configuration
@EnableWebSecurity
class Configuration{
    @Bean
    fun encoder() : PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity) : DefaultSecurityFilterChain {
        http.csrf{it->it.disable()}.authorizeRequests {
            it -> it.anyRequest().permitAll()
        }
        return http.build()
    }
}
