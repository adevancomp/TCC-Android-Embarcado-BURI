package br.edu.uea.buri.domain

import jakarta.persistence.*
import java.util.UUID

@Entity
data class UserApp(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    @Column(nullable = false, length = 210)
    var name: String,
    @Column(nullable = false, length = 170, unique = true)
    var email: String,
    @Column(nullable = false)
    var passwordEncrypted: String,
    @OneToMany(mappedBy = "owner",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.PERSIST]
    ) val equipments: List<Equipment> = mutableListOf()
)