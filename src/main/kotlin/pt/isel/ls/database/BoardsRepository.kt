package pt.isel.ls.database

import pt.isel.ls.controllers.SearchBoards
import pt.isel.ls.controllers.UserBoards
import pt.isel.ls.model.Board
import pt.isel.ls.model.Card
import pt.isel.ls.model.CardEntry
import pt.isel.ls.model.ListEntry

interface BoardsRepository {

    /**-------------------------------Boards--------------------------------------**/
    fun createBoard(name: String, description: String, userId: Int): String // parameters: name, description
    fun addUserToBoard(boardName: String, username: String): Boolean
    fun getAvailableBoards(userId: Int, limit: Int?, skip: Int?): UserBoards
    fun boardDetails(boardName: String): Board
    fun boardUsers(boardName: String): List<String>
    fun addList(boardName: String, name: String): String // parameters: name e na path :boardName
    fun deleteList(listId: String): String
    fun getLists(boardName: String, limit: Int?, skip: Int?): List<ListEntry>
    fun listDetails(listId: String): pt.isel.ls.model.List
    fun createCard(
        listId: String,
        name: String,
        description: String,
        dueDate: String?
    ): String // parameters: name, description, due_date (optional)
    fun deleteCard(cardId: String): String
    fun getCards(listId: String, limit: Int?, skip: Int?): List<CardEntry>
    fun cardDetails(cardId: String): Card
    fun moveCard(cardId: String, lid: String, cix: Int?): String
    fun searchBoard(userId: Int, boardName: String, limit: Int?, skip: Int?): SearchBoards
}
