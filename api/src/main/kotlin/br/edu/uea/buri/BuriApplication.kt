package br.edu.uea.buri

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(servers = [Server(url="/", description = "Default Server Url")])
class BuriApplication

fun main(args: Array<String>) {
	runApplication<BuriApplication>(*args)
}
