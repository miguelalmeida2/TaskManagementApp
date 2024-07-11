package pt.isel.ls.controllers

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Response
import pt.isel.ls.Errors
import pt.isel.ls.model.Board
import pt.isel.ls.model.CardEntry
import pt.isel.ls.model.ListEntry

@Serializable
data class UserResponseDataId(val token: String, val id: Int)

@Serializable
data class UserResponseDataName(val name: String, val token: String)

@Serializable
data class IdentifierData(val id: Int)

@Serializable
data class Response(val response: String)

@Serializable
data class IdentifierCard(val cardId: String)

@Serializable
data class IdentifierList(val listId: String)

@Serializable
data class IdentifierBoard(val boardName: String)

@Serializable
data class UserLogin(val email: String, val password: String)

@Serializable
data class UserRegister(val name: String, val email: String, val password: String)

@Serializable
data class UserAdded(val addedOrNot: Boolean)

@Serializable
data class CardsList(val cards: List<CardEntry>)

@Serializable
data class UserBoards(val previous: Boolean, val next: Boolean, val boards: List<String>)
@Serializable
data class SearchBoards(val previous: Boolean, val next: Boolean, val boards: List<Board>)

@Serializable
data class BoardsLists(val lists: List<ListEntry>)

@Serializable
data class UsersList(val users: List<String>)

@Serializable
data class NewUser(val name: String, val email: String)

@Serializable
data class UserDetails(val number: Int, val name: String, val email: String)

@Serializable
data class ErrorMessageModel(val status: Int, val message: String)

data class SharedState(val userId: Int, val token: String? = null)

fun exceptionHandler(block: () -> Response): Response {
    return try {
        return block()
    } catch (e: Errors) {
        Response(e.getStatus())
            .header("content-type", "application/problem+json")
            .body(Json.encodeToString(ErrorMessageModel(e.getStatus().code, e.message.orEmpty())))
    }
}
