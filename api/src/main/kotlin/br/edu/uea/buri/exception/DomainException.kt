package br.edu.uea.buri.exception

data class DomainException(override val message: String?) : RuntimeException(message)
