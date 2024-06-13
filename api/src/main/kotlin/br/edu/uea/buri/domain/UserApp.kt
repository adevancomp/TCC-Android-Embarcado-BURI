package br.edu.uea.buri.domain

import br.edu.uea.buri.dto.user.views.UserViewDTO
import br.edu.uea.buri.enums.UserRole
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
    ) val equipments: List<Equipment> = mutableListOf(),
    @Enumerated(EnumType.STRING)
    val role: UserRole
){
    fun toUserViewDTO() : UserViewDTO = UserViewDTO(
        id = this.id!!,
        name = this.name,
        email = this.email,
        role = this.role.toString()
    )
}