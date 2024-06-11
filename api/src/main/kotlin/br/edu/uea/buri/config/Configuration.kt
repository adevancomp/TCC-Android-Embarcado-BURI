package br.edu.uea.buri.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain

@Configuration
@EnableWebSecurity
class Configuration : OpenApiCustomizer {
    @Bean
    fun encoder() : PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity) : DefaultSecurityFilterChain {
        http.csrf{it->it.disable()}.authorizeRequests {
            it -> it.anyRequest().permitAll()
        }
        return http.build()
    }

    override fun customise(openApi: OpenAPI?) {
        val info: Info = Info()
        info.apply {
            contact = Contact()
                .name("Adevan Neves Santos")
                .email("ans.eng20@uea.edu.br")
                .url("https://www.linkedin.com/in/adevancomp/")
            title = "Projeto Buri - Spring Boot API"
            description = """
                BURI API é uma API REST FULL que facilita o monitoramento residencial.
            
                Através dessa aplicação, nosso sistema embarcado poderá emitir alertas em
                nosso aplicativo Android e armazenar os dados históricos de monóxido de carbono
                prevenindo acidentes aos moradores.
            """.trimIndent()
            version = "1.0.0"
        }
        openApi?.info = info
    }
}
