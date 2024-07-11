package pt.isel.ls.services

import pt.isel.ls.InvalidFormat
import pt.isel.ls.controllers.SearchBoards
import pt.isel.ls.controllers.UserBoards
import pt.isel.ls.database.mem.MemBoardsRepository
import pt.isel.ls.model.Board
import pt.isel.ls.model.Card
import pt.isel.ls.model.CardEntry
import pt.isel.ls.model.ListEntry
import java.text.ParseException
import java.text.SimpleDateFormat

private const val NAMESIZE = 80
private const val IDSIZE = 36

class BoardServicesTest(private val data: MemBoardsRepository) : IBoardsServices {

    override fun createBoard(name: String, description: String, userId: Int): String {
        if (name.length > NAMESIZE) throw InvalidFormat("Board name needs to be less than $NAMESIZE characters")
        if (description.length > 200) throw InvalidFormat("Board description needs to be less than 200 characters")
        return data.createBoard(name, description, userId)
    }

    override fun addUserToBoard(boardName: String, username: String): Boolean {
        if (boardName.length > NAMESIZE) throw InvalidFormat("Board name needs to be less than $NAMESIZE characters")
        if (username.length > NAMESIZE) throw InvalidFormat("Username needs to be less than $NAMESIZE characters")
        return data.addUserToBoard(boardName, username)
    }

    override fun getAvailableBoards(userId: Int, limit: Int?, skip: Int?): UserBoards =
        data.getAvailableBoards(userId, limit, skip)

    override fun boardDetails(boardName: String): Board {
        if (boardName.length > NAMESIZE) throw InvalidFormat("Board name needs to be less than $NAMESIZE characters")
        return data.boardDetails(boardName)
    }

    override fun boardUsers(boardName: String): List<String> {
        if (boardName.length > NAMESIZE) throw InvalidFormat("Board name needs to be less than $NAMESIZE characters")
        return data.boardUsers(boardName)
    }

    override fun searchBoard(userId: Int, boardName: String, limit: Int?, skip: Int?): SearchBoards {
        if (boardName.length > NAMESIZE) throw InvalidFormat("Board name needs to be less than $NAMESIZE characters")
        return data.searchBoard(userId, boardName, limit, skip)
    }

    override fun addList(boardName: String, name: String): String {
        if (boardName.length > NAMESIZE) throw InvalidFormat("Board name needs to be less than $NAMESIZE characters")
        if (name.length > NAMESIZE) throw InvalidFormat("List name needs to be less than $NAMESIZE characters")
        return data.addList(boardName, name)
    }

    override fun deleteList(listId: String): String {
        if (listId.length > IDSIZE) throw InvalidFormat("List name needs to be less than $IDSIZE characters")
        return data.deleteList(listId)
    }

    override fun getLists(boardName: String, limit: Int?, skip: Int?): List<ListEntry> {
        if (boardName.length > NAMESIZE) throw InvalidFormat("Board name needs to be less than $NAMESIZE characters")
        return data.getLists(boardName, limit, skip)
    }

    override fun listDetails(listId: String): pt.isel.ls.model.List {
        if (listId.length > IDSIZE) throw InvalidFormat("List name needs to be less than $IDSIZE characters")
        return data.listDetails(listId)
    }

    override fun createCard(listId: String, name: String, description: String, dueDate: String?): String {
        if (listId.length > IDSIZE) throw InvalidFormat("List name needs to be less than $IDSIZE characters")
        if (name.length > NAMESIZE) throw InvalidFormat("Card name needs to be less than $NAMESIZE characters")
        if (description.length > 200) throw InvalidFormat("Card description needs to be less than 200 characters")

        return data.createCard(listId, name, description, dueDate)
    }

    override fun deleteCard(cardId: String): String {
        if (cardId.length > IDSIZE) throw InvalidFormat("CardId needs to be less than $IDSIZE characters")

        return data.deleteCard(cardId)
    }

    override fun getCards(listId: String, limit: Int?, skip: Int?): List<CardEntry> {
        if (listId.length > IDSIZE) throw InvalidFormat("List name needs to be less than $IDSIZE characters")
        return data.getCards(listId, limit, skip)
    }

    override fun cardDetails(cardId: String): Card {
        if (cardId.length > IDSIZE) throw InvalidFormat("CardId needs to be less than $IDSIZE characters")
        return data.cardDetails(cardId)
    }

    override fun moveCard(cardId: String, lid: String, cix: Int?): String {
        if (cardId.length > IDSIZE) throw InvalidFormat("CardId needs to be less than $IDSIZE characters")
        if (lid.length > IDSIZE) throw InvalidFormat("ListId needs to be less than $IDSIZE characters")

        return data.moveCard(cardId, lid, cix)
    }
}

/*
* função auxiliar
* */
fun isAValidDate(date: String?, dateFormat: String): Boolean {
    val format = SimpleDateFormat(dateFormat)
    format.isLenient = false
    return try {
        format.parse(date)
        true
    } catch (e: ParseException) {
        false
    }
}
