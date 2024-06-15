package br.edu.uea.buri.utils

import br.edu.uea.buri.enums.UserRole

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
    }
}