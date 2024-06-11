package br.edu.uea.buri.utils

class DomainOperations {
    companion object{
        fun generateEquipmentId() : String {
            val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            return (1..6).map { allowedChars.random() }.joinToString ("")
        }
    }
}