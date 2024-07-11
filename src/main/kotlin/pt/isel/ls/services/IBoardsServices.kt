package pt.isel.ls.services

import pt.isel.ls.controllers.SearchBoards
import pt.isel.ls.controllers.UserBoards
import pt.isel.ls.model.Board
import pt.isel.ls.model.Card
import pt.isel.ls.model.CardEntry
import pt.isel.ls.model.ListEntry

interface IBoardsServices {
    /**-------------------------------Boards--------------------------------------**/
    /**
     * Creates a new Board with [name] and [description] and adds the user given by [userId] to the board right after.
     * @return [String] with info about the creation of the board.
     */
    fun createBoard(name: String, description: String, userId: Int): String // parameters: name, description

    /**
     * Adds a User with given [username] to given Board with [boardName].
     */
    fun addUserToBoard(boardName: String, username: String): Boolean

    /**
     * Gets all available boards to user given by [userId].
     * @return [List] with board's name.
     */
    fun getAvailableBoards(userId: Int, limit: Int?, skip: Int?): UserBoards

    /**
     * Retrieves all board details given by [boardName].
     * @return Object [Board] with board's fields.
     */
    fun boardDetails(boardName: String): Board

    /**
     * Retrieves all usernames associated with a board
     */
    fun boardUsers(boardName: String): List<String>

    /**
     * Adds [List] with [name] to given board with [boardName].
     * @return [String] with the name of the List added.
     */
    fun addList(boardName: String, name: String): String // parameters: name

    /**
     * Delete a list and all associated cards
     */
    fun deleteList(listId: String): String

    /**
     * Gets all the lists available in given [boardName].
     * @return [List] with list's name.
     */
    fun getLists(boardName: String, limit: Int?, skip: Int?): List<ListEntry>

    /**
     * Retrieves all List details given by [boardName] and [listName].
     * @return Object [List] with list's fields.
     */
    fun listDetails(listId: String): pt.isel.ls.model.List

    /**
     * Creates a new Card with [name], [description] and  [dueDate].
     * @return [String] with info giving the card Name and Id.
     */
    fun createCard(
        listId: String,
        name: String,
        description: String,
        dueDate: String?
    ): String // parameters: name, description, due_date (optional)

    /**
     * Delete a card
     */
    fun deleteCard(cardId: String): String

    /**
     * Gets all the Cards available in given [boardName] and [listName].
     * @return [List] with card's Id.
     */
    fun getCards(listId: String, limit: Int?, skip: Int?): List<CardEntry>

    /**
     * Retrieves all Card details given by [boardName] and [listName].
     * @return Object [Card] with card's fields.
     */
    fun cardDetails(cardId: String): Card

    /**
     * Moves a Card with [cardId] in a list with [listName] belonging to [boardName] to another list given by [lid].
     */
    fun moveCard(cardId: String, lid: String, cix: Int?): String

    fun searchBoard(userId: Int, boardName: String, limit: Int?, skip: Int?): SearchBoards
}
