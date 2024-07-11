package pt.isel.ls.database.mem

import pt.isel.ls.DataAlreadyExists
import pt.isel.ls.NotFound
import pt.isel.ls.controllers.SearchBoards
import pt.isel.ls.controllers.UserBoards
import pt.isel.ls.database.BoardsRepository
import pt.isel.ls.model.Board
import pt.isel.ls.model.Card
import pt.isel.ls.model.CardEntry
import pt.isel.ls.model.List
import pt.isel.ls.model.ListEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MemBoardsRepository(private val usersRep: MemUsersRepository) : BoardsRepository {

    val boards = HashMap<String, Board>() // key: boardName
    val boardsUsers = HashMap<String, MutableList<String>>()
    val lists = HashMap<String, List>() // key: listId
    val cards = HashMap<String, Card>() // key: cardId

    override fun createBoard(name: String, description: String, userId: Int): String {
        if (boards[name] != null)
            throw DataAlreadyExists("board with given name ($name) already exists")

        boards[name] = Board(name, description)
        println(boards[name])
        boardsUsers[name] = mutableListOf(userId.toString())
        return name
    }

    override fun addUserToBoard(boardName: String, username: String): Boolean {
        if (boards[boardName] == null)
            throw NotFound("board with given name does not exist")

        val userId = usersRep.getIdByUsername(username) ?: throw NotFound("user not found")

        if (boardsUsers[boardName]?.contains(userId.toString()) == true)
            throw DataAlreadyExists("user already in board")

        boardsUsers[boardName]?.add(userId.toString())
        return true
    }

    override fun getAvailableBoards(userId: Int, limit: Int?, skip: Int?): UserBoards {

        val boards = boardsUsers.filter { it.value.contains(userId.toString()) }.keys.toList()

        return UserBoards(true, true, boards)
    }

    override fun boardDetails(boardName: String): Board =
        boards[boardName] ?: throw NotFound("board with given name does not exist")

    override fun boardUsers(boardName: String): kotlin.collections.List<String> {
        return boardsUsers[boardName] ?: throw NotFound("board with given name ($boardName) does not exist")
    }

    override fun searchBoard(userId: Int, boardName: String, limit: Int?, skip: Int?): SearchBoards {
        val res = mutableListOf<Board>()
        var index = 0
        for (i in 0 until boardName.length) {
            if (boards[boardName.substring(0..(boardName.length - i))] != null)
                res.add(index++, boards[boardName.substring(0..(boardName.length - i))]!!)
        }
        if (res.isEmpty()) {
            throw NotFound("No boards were found with that query")
        }
        return SearchBoards(true, true, res)
    }

    override fun addList(boardName: String, name: String): String {
        if (boards[boardName] == null)
            throw NotFound("board with given name ($boardName) does not exist")

        if (lists.values.find { it.board == boardName && it.name == name } != null)
            throw DataAlreadyExists("list with given name ($name) already exists")

        val listId = UUID.randomUUID().toString()

        lists[listId] = List(listId, boardName, name)

        return listId
    }

    override fun deleteList(listId: String): String {
        lists.remove(listId)
        return "$listId deleted"
    }

    override fun getLists(boardName: String, limit: Int?, skip: Int?): kotlin.collections.List<ListEntry> {
        if (boards[boardName] == null)
            throw NotFound("board with given name ($boardName) does not exist")

        val filteredLists = lists.filter { it.value.board == boardName }
        val startIndex = skip ?: 0
        val endIndex = if (limit != null) startIndex + limit else filteredLists.size

        // TODO return filteredLists.keys.toList().subList(startIndex, endIndex)
        return emptyList()
    }

    override fun listDetails(listId: String): List {
        return lists[listId] ?: throw NotFound("list not found")
    }

    override fun createCard(listId: String, name: String, description: String, dueDate: String?): String {
        if (lists[listId] == null)
            throw NotFound("list $listId not found")

        val cardId = UUID.randomUUID().toString()
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val lastIdx = cards.size + 1
        cards[cardId] = Card(cardId, lastIdx, name, description, currentDate, dueDate, false, listId)
        return cardId
    }

    override fun deleteCard(cardId: String): String {
        cards.remove(cardId)
        return "$cardId deleted"
    }

    override fun getCards(listId: String, limit: Int?, skip: Int?): kotlin.collections.List<CardEntry> {
        val filteredCards = cards.filter { it.value.list == listId }
        val startIndex = skip ?: 0
        val endIndex = if (limit != null) startIndex + limit else filteredCards.size

        // TODO return filteredCards.keys.toList().subList(startIndex, endIndex)
        return emptyList()
    }

    override fun cardDetails(cardId: String): Card {
        return cards[cardId] ?: throw NotFound("card does not exist")
    }

    override fun moveCard(cardId: String, lid: String, cix: Int?): String {
        if (lists[lid] == null)
            throw NotFound("list $lid not found")

        if (cards[cardId] == null)
            throw NotFound("card $cardId not found")

        val cardsInList = cards.values.filter { it.list == lid }

        if (cix != null) {
            var cixInc = cix + 1

            cardsInList.filter { it.cix >= cix }.forEach {
                if (it.id != cardId) {
                    it.cix = cixInc
                    cixInc++
                }
            }
        }

        val maxCix = cardsInList.maxOfOrNull { it.cix } ?: 0
        val usedCix = cix ?: (maxCix + 1)

        cards[cardId]!!.list = lid
        cards[cardId]!!.cix = usedCix

        return "card ($cardId) moved to $lid"
    }
}
