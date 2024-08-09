package br.edu.uea.buri.exception

data class SensorNotConnectedException(override val message: String?) : RuntimeException(message)
