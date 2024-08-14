package br.edu.uea.buri.utils

import br.edu.uea.buri.enums.UserRole
import java.math.BigDecimal

class DomainOperations {
    companion object{
        fun generateEquipmentId() : String {
            val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            return (1..6).map { allowedChars.random() }.joinToString ("")
        }
        fun stringToRoleUser(value: String) : UserRole = when(value){
            UserRole.ROLE_ADM.toString() -> UserRole.ROLE_ADM
            UserRole.ROLE_CUSTOMER.toString() -> UserRole.ROLE_CUSTOMER
            else -> UserRole.ROLE_UNDEFINED
        }
        fun temperatureToMessage(temp: BigDecimal) : String = when {
           temp > BigDecimal(35.0) -> "Temperatura elevada!"
           temp < BigDecimal(15.0) -> "Temperatura baixa!"
           else -> ""
        }
        fun airHumidityToMessage(air: BigDecimal) : String = when {
            air > BigDecimal(0.48) && air < BigDecimal(0.62) -> "Umidade do ar adequada"
            air < BigDecimal(0.352) -> "Estado crítico, baixa umidade do ar"
            else -> ""
        }
        fun coToMessage(co: BigDecimal) : String = when {
            co < BigDecimal(10.0) -> "Quantidade não preocupante"
            co >= BigDecimal(10.0) && co < BigDecimal(35.0) -> "nenhum sintoma adverso dentro de 8 horas de exposição"
            co >= BigDecimal(35.0) && co < BigDecimal(200.0) -> "dor de cabeça após 2 a 3 horas de exposição"
            co >= BigDecimal(200.0) && co < BigDecimal(400.0) -> "dor de cabeça e náusea após 1 a 2 horas de exposição"
            co >= BigDecimal(400.0) && co < BigDecimal(800.0) -> "dor de cabeça, náusea e distúrbios após 45 minutos de exposição; morte em até 2 horas de duração"
            co >= BigDecimal(800.0) && co < BigDecimal(1000.0) -> "perda de consciência"
            co >= BigDecimal(1000.0) && co < BigDecimal(1600.0) -> "dor de cabeça, náusea e distúrbios após 5 a 10 minutos de exposição, perda da consciência após 30 minutos de exposição"
            else -> "efeitos fisiológicos imediatos, perda de consciência e risco de vida após 1 a 3 minutos de exposição"
        }

    }
}