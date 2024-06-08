package br.edu.uea.buri

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BuriApplication

fun main(args: Array<String>) {
	runApplication<BuriApplication>(*args)
}
