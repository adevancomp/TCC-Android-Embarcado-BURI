package br.edu.uea.buri.config

import br.edu.uea.buri.config.security.CustomAuthFilter
import br.edu.uea.buri.config.security.CustomUserDetailsService
import br.edu.uea.buri.config.security.JwtProperties
import br.edu.uea.buri.repository.UserAppRepository
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.ServerHttpSecurity.http
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.util.AntPathMatcher

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties::class)
@SecuritySchemes(
    SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
    )
)
class Configuration: OpenApiCustomizer {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authFilter: CustomAuthFilter
    ) : DefaultSecurityFilterChain {
        http.csrf{it.disable()}.authorizeHttpRequests {
            auth ->
                auth.requestMatchers(
                    "/swagger-ui/**", "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/v3/api-docs/**"
                ).permitAll()
                auth.requestMatchers(HttpMethod.POST,"/auth","/auth/register").permitAll()
                auth.anyRequest().authenticated()
        }.sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }.addFilterBefore(authFilter,UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }


    @Bean
    fun encoder() : PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(userAppRepository: UserAppRepository) : UserDetailsService =
        CustomUserDetailsService(userAppRepository)

    @Bean
    fun authenticationManager(
        userDetailsService: CustomUserDetailsService,
        passwordEncoder: PasswordEncoder) : AuthenticationManager {
        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService)
        authenticationProvider.setPasswordEncoder(passwordEncoder)
        val providerManager = ProviderManager(authenticationProvider)
        providerManager.isEraseCredentialsAfterAuthentication = false
        return providerManager
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
        openApi?.addSecurityItem(SecurityRequirement().addList("basicAuth"))
    }
}

/*
*
auth.requestMatchers(
                    HttpMethod.POST, "/auth/register","/auth","/measurement"
                ).permitAll()
                auth.requestMatchers(
                    "/measurement/**"
                ).permitAll()
*
* */