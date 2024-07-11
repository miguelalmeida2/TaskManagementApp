package pt.isel.ls.model

import kotlinx.serialization.Serializable

@Serializable
data class Username(val username: String)

@Serializable
data class UserComplete(val number: Int, val name: String, val bearer: String, val email: String)

@Serializable
data class User(val number: Int, val token: String? = null)

@Serializable
data class UserDTO(val number: Int, val name: String, val bearer: String, val email: String)

@Serializable
data class UserDetailsLoginRegister(val number: Int, val name: String, val email: String, val token: String)

@Serializable
data class UserDetails(val number: Int, val name: String, val email: String)

@Serializable
data class Board(val name: String, val description: String)

@Serializable
data class BoardsUsers(val board: String, val users: Int)

@Serializable
data class ListDTO(val name: String)

@Serializable
data class List(val id: String, val board: String, val name: String)

@Serializable
data class ListEntry(val id: String, val name: String)

@Serializable
data class CardEntry(val id: String, val name: String)

@Serializable
data class NewCard(
    val name: String,
    val description: String,
    val conclusionDate: String?
)

@Serializable
data class Card(
    val id: String,
    var cix: Int,
    val name: String,
    val description: String,
    val creationDate: String,
    val conclusionDate: String?,
    val archived: Boolean = false,
    var list: String
)
