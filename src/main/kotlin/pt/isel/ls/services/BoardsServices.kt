package pt.isel.ls.services

import org.slf4j.LoggerFactory
import pt.isel.ls.InvalidFormat
import pt.isel.ls.controllers.SearchBoards
import pt.isel.ls.controllers.UserBoards
import pt.isel.ls.database.jdbi.TransactionManager
import pt.isel.ls.model.Board
import pt.isel.ls.model.Card
import pt.isel.ls.model.CardEntry
import pt.isel.ls.model.ListEntry

private const val NAMESIZE = 80
private const val IDSIZE = 36

class BoardsServices(private val transactionManager: TransactionManager) : IBoardsServices {
    override fun createBoard(name: String, description: String, userId: Int): String {
        logger.info("\tChecking createUser params format and content")
        if (name.length > NAMESIZE) throw InvalidFormat("Board name needs to be less than $NAMESIZE characters")
        if (description.length > 200) throw InvalidFormat("Board description needs to be less than 200 characters")
        logger.info("\tCalling createUser Repo Method.. ")
        return transactionManager.run {
            it.boardsRepository.createBoard(name, description, userId)
        }
    }

    override fun addUserToBoard(boardName: String, username: String): Boolean {
        logger.info("\tChecking addUserToBoard params format and content")
        if (boardName.length > NAMESIZE) throw InvalidFormat("Board name needs to be less than $NAMESIZE characters")
        if (username.length > NAMESIZE) throw InvalidFormat("Username needs to be less than $NAMESIZE characters")
        logger.info("\tCalling addUserToBoard Repo Method.. ")
        return transactionManager.run {
            it.boardsRepository.addUserToBoard(boardName, username)
        }
    }

    override fun getAvailableBoards(userId: Int, limit: Int?, skip: Int?): UserBoards = transactionManager.run {
        logger.info("\tChecking getAvailableBoards params format and content")
        logger.info("\tCalling getAvailableBoards Repo Method.. ")
        it.boardsRepository.getAvailableBoards(userId, limit, skip)
    }

    override fun boardDetails(boardName: String): Board {
        logger.info("\tChecking boardDetails params format and content")
        if (boardName.length > NAMESIZE) throw InvalidFormat("Board name needs to be less than $NAMESIZE characters")
        logger.info("\tCalling boardDetails Repo Method.. ")
        return transactionManager.run {
            it.boardsRepository.boardDetails(boardName)
        }
    }

    override fun boardUsers(boardName: String): List<String> {
        logger.info("\tChecking boardUsers params format and content")
        if (boardName.length > NAMESIZE) throw InvalidFormat("Board name needs to be less than $NAMESIZE characters")
        logger.info("\tCalling boardUsers Repo Method.. ")
        return transactionManager.run {
            it.boardsRepository.boardUsers(boardName)
        }
    }

    override fun searchBoard(userId: Int, boardName: String, limit: Int?, skip: Int?): SearchBoards {
        logger.info("\tChecking searchBoard params format and content")
        if (boardName.length > NAMESIZE) throw InvalidFormat("Board name needs to be less than $NAMESIZE characters")
        logger.info("\tCalling searchBoard Repo Method.. ")
        return transactionManager.run {
            it.boardsRepository.searchBoard(userId, boardName, limit, skip)
        }
    }

    override fun addList(boardName: String, name: String): String {
        logger.info("\tChecking addList params format and content")
        if (boardName.length > NAMESIZE) throw InvalidFormat("Board name needs to be less than $NAMESIZE characters")
        if (name.length > NAMESIZE) throw InvalidFormat("List name needs to be less than $NAMESIZE characters")
        logger.info("\tCalling addList Repo Method.. ")
        return transactionManager.run {
            it.boardsRepository.addList(boardName, name)
        }
    }

    override fun deleteList(listId: String): String {
        logger.info("\tChecking deleteList params format and content")
        if (listId.length > IDSIZE) throw InvalidFormat("ListId needs to be less than $IDSIZE characters")
        logger.info("\tCalling deleteList Repo Method.. ")
        return transactionManager.run {
            it.boardsRepository.deleteList(listId)
        }
    }

    override fun getLists(boardName: String, limit: Int?, skip: Int?): List<ListEntry> {
        logger.info("\tChecking getLists params format and content")
        if (boardName.length > NAMESIZE) throw InvalidFormat("Board name needs to be less than $NAMESIZE characters")
        logger.info("\tCalling getLists Repo Method.. ")
        return transactionManager.run {
            it.boardsRepository.getLists(boardName, limit, skip)
        }
    }

    override fun listDetails(listId: String): pt.isel.ls.model.List {
        logger.info("\tChecking listDetails params format and content")
        if (listId.length > IDSIZE) throw InvalidFormat("List name needs to be less than $IDSIZE characters")
        logger.info("\tCalling listDetails Repo Method.. ")
        return transactionManager.run {
            it.boardsRepository.listDetails(listId)
        }
    }

    override fun createCard(listId: String, name: String, description: String, dueDate: String?): String {
        logger.info("\tChecking createCard params format and content")
        if (listId.length > IDSIZE) throw InvalidFormat("List name needs to be less than $IDSIZE characters")
        if (name.length > NAMESIZE) throw InvalidFormat("Card name needs to be less than $NAMESIZE characters")
        if (description.length > 200) throw InvalidFormat("Card description needs to be less than 200 characters")
        logger.info("\tCalling createCard Repo Method.. ")
        return transactionManager.run {
            it.boardsRepository.createCard(listId, name, description, dueDate)
        }
    }

    override fun deleteCard(cardId: String): String {
        logger.info("\tChecking deleteCard params format and content")
        if (cardId.length > IDSIZE) throw InvalidFormat("CardId needs to be less than $IDSIZE characters")
        logger.info("\tCalling deleteCard Repo Method.. ")
        return transactionManager.run {
            it.boardsRepository.deleteCard(cardId)
        }
    }

    override fun getCards(listId: String, limit: Int?, skip: Int?): List<CardEntry> {
        logger.info("\tChecking getCards params format and content")
        if (listId.length > IDSIZE) throw InvalidFormat("List name needs to be less than $IDSIZE characters")
        logger.info("\tCalling getCards Repo Method.. ")
        return transactionManager.run {
            it.boardsRepository.getCards(listId, limit, skip)
        }
    }

    override fun cardDetails(cardId: String): Card {
        logger.info("\tChecking cardDetails params format and content")
        if (cardId.length > IDSIZE) throw InvalidFormat("CardId needs to be less than $IDSIZE characters")
        logger.info("\tCalling cardDetails Repo Method.. ")
        return transactionManager.run {
            it.boardsRepository.cardDetails(cardId)
        }
    }

    override fun moveCard(cardId: String, lid: String, cix: Int?): String {
        logger.info("\tChecking moveCard params format and content")
        if (cardId.length > IDSIZE) throw InvalidFormat("CardId needs to be less than $IDSIZE characters")
        if (lid.length > IDSIZE) throw InvalidFormat("ListId needs to be less than $IDSIZE characters")
        logger.info("\tCalling moveCard Repo Method.. ")
        return transactionManager.run {
            it.boardsRepository.moveCard(cardId, lid, cix)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BoardsServices::class.java)
    }
}
