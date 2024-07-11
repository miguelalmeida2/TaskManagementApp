package pt.isel.ls.database.jdbi

import org.jdbi.v3.core.Handle
import org.slf4j.LoggerFactory
import pt.isel.ls.DataAlreadyExists
import pt.isel.ls.InvalidFormat
import pt.isel.ls.NotFound
import pt.isel.ls.controllers.SearchBoards
import pt.isel.ls.controllers.UserBoards
import pt.isel.ls.database.BoardsRepository
import pt.isel.ls.model.Board
import pt.isel.ls.model.Card
import pt.isel.ls.model.CardEntry
import pt.isel.ls.model.ListEntry
import pt.isel.ls.utils.format
import java.time.LocalDate
import java.util.UUID

class JdbiBoardsRepository(private val handle: Handle) : BoardsRepository {

    override fun createBoard(name: String, description: String, userId: Int): String {
        logger.info("\t\tAccessing DB for method createBoard")
        if (handle.createQuery("select name from board where name=:name").bind("name", name).mapTo(String::class.java)
            .singleOrNull() != null
        )
            throw DataAlreadyExists("board ($name) already exists")

        handle.createUpdate(
            "insert into board(name, description) values (:name, :description)"
        ).bind("name", name).bind("description", description).execute()
        handle.createUpdate(
            "insert into boards_users(board, users) values (:name, :userId)"
        ).bind("name", name).bind("userId", userId).execute()

        return name
    }

    override fun addUserToBoard(boardName: String, username: String): Boolean {
        logger.info("\t\tAccessing DB for method addUserToBoard")
        val userId = handle.createQuery("select number from users where name=:username")
            .bind("username", username).mapTo(Int::class.java).singleOrNull()
            ?: throw NotFound("user not found")

        if (handle.createQuery("select users from boards_users where board=:boardName and users=:userId")
            .bind("boardName", boardName).bind("userId", userId).mapTo(Int::class.java).singleOrNull() != null
        )
            throw DataAlreadyExists("user already added")

        handle.createUpdate(
            "insert into boards_users(board, users) values (:name, :userId)"
        ).bind("name", boardName).bind("userId", userId).execute()

        return true
    }

    override fun getAvailableBoards(userId: Int, limit: Int?, skip: Int?): UserBoards {

        logger.info("\t\tAccessing DB for method getAvailableBoards")

        val query =
            handle.createQuery("select board from boards_users where users=:userId order by board asc limit :limit offset :offset")
                .bind("userId", userId)
                .bind("limit", limit)
                .bind("offset", skip)

        val boards = query.mapTo(String::class.java).list()

        // Check if there is a previous page
        val hasPreviousPage = skip?.let { it > 0 } ?: false

        // Check if there is a next page
        val hasNextPage = if (limit != null) {
            val nextQuery =
                handle.createQuery("select board from boards_users where users=:userId order by board asc limit :limit offset :offset")
                    .bind("userId", userId)
                    .bind("limit", limit)
                    .bind("offset", skip?.plus(limit))
                    .mapTo(String::class.java)
                    .list()
            nextQuery.isNotEmpty()
        } else {
            false
        }

        return UserBoards(hasPreviousPage, hasNextPage, boards)
    }

    override fun boardDetails(boardName: String): Board {
        logger.info("\t\tAccessing DB for method boardDetails")
        return handle.createQuery("select * from board where name=:boardName").bind("boardName", boardName)
            .mapTo(Board::class.java).singleOrNull() ?: throw NotFound("board ($boardName) was not found")
    }

    override fun boardUsers(boardName: String): List<String> {
        logger.info("\t\tAccessing DB for method boardUsers")
        handle.createQuery("select * from board where name=:boardName").bind("boardName", boardName)
            .mapTo(Board::class.java).singleOrNull() ?: throw NotFound("board ($boardName) was not found")

        return handle.createQuery("select name from boards_users bu join users on bu.users=number where board=:boardName")
            .bind("boardName", boardName)
            .mapTo(String::class.java).list()
    }

    override fun searchBoard(userId: Int, boardName: String, limit: Int?, skip: Int?): SearchBoards {
        logger.info("\t\tAccessing DB for method searchBoard")
        val searchParam = boardName
        val queryVal = "%$searchParam%"

        val query = handle.createQuery(
            "select * from board b join boards_users bu on b.name = bu.board where (name ILIKE :query and bu.users = :userId) order by b.name asc limit :limit offset :offset"
        )
            .bind("userId", userId)
            .bind("query", queryVal)
            .bind("limit", limit)
            .bind("offset", skip)

        val boards = query.mapTo(Board::class.java).list() ?: throw NotFound("No boards were found with that query")

        // Check if there is a previous page
        val hasPreviousPage = skip?.let { it > 0 } ?: false

        // Check if there is a next page
        val hasNextPage = if (limit != null) {
            val nextQuery =
                handle.createQuery("select * from board b where name ILIKE :query order by b.name asc limit :limit offset :offset")
                    .bind("query", queryVal)
                    .bind("limit", limit)
                    .bind("offset", skip?.plus(limit))
                    .mapTo(String::class.java)
                    .list()
            nextQuery.isNotEmpty()
        } else {
            false
        }

        return SearchBoards(hasPreviousPage, hasNextPage, boards)
    }

    override fun addList(boardName: String, name: String): String {
        logger.info("\t\tAccessing DB for method addList")
        handle.createQuery("select name from board where name=:boardName").bind("boardName", boardName)
            .mapTo(String::class.java).singleOrNull() ?: throw NotFound("board $boardName not found")

        if (handle.createQuery("select name from list where name=:name and board=:boardName").bind("name", name)
            .bind("boardName", boardName).mapTo(String::class.java)
            .singleOrNull() != null
        ) throw DataAlreadyExists("list ($name) already exists in ($boardName)")

        val listId = UUID.randomUUID().toString()

        handle.createUpdate(
            "insert into list(id,board,name) values (:listId, :board, :name)"
        ).bind("listId", listId).bind("board", boardName).bind("name", name).execute()

        return listId
    }

    override fun deleteList(listId: String): String {
        logger.info("\t\tAccessing DB for method deleteList")
        handle.createUpdate("delete from card where list=:listId; delete from list where id=:listId")
            .bind("listId", listId)
            .execute()

        return "list ($listId) deleted"
    }

    override fun getLists(boardName: String, limit: Int?, skip: Int?): List<ListEntry> {
        logger.info("\t\tAccessing DB for method getLists")
        handle.createQuery("select name from board where name=:boardName").bind("boardName", boardName)
            .mapTo(String::class.java).singleOrNull() ?: throw NotFound("board $boardName not found")

        return handle.createQuery("select id, name from list where board=:boardName limit :limit offset :offset")
            .bind("boardName", boardName)
            .bind("limit", limit)
            .bind("offset", skip)
            .mapTo(ListEntry::class.java).list()
    }

    override fun listDetails(listId: String): pt.isel.ls.model.List {
        logger.info("\t\tAccessing DB for method listDetails")
        return handle.createQuery("select * from list where id=:listId").bind("listId", listId)
            .mapTo(pt.isel.ls.model.List::class.java).singleOrNull()
            ?: throw NotFound("list ($listId) not found")
    }

    override fun createCard(
        listId: String,
        name: String,
        description: String,
        dueDate: String?
    ): String {
        logger.info("\t\tAccessing DB for method createCard")
        handle.createQuery("select name from list where id=:listId").bind("listId", listId).mapTo(String::class.java)
            .singleOrNull() ?: throw NotFound("list ($listId) not found")

        val lastIdx =
            handle.createQuery("select coalesce(max(cix), 0) + 1 from card where list=:listId").bind("listId", listId)
                .mapTo(Int::class.java)
                .one()

        val cardId = UUID.randomUUID().toString()
        var conclusionDate: LocalDate? = null

        if (dueDate != null) {
            conclusionDate = dueDate.format()
            if (conclusionDate.isBefore(LocalDate.now())) throw InvalidFormat("Conclusion date must be after creation date")
        }

        handle.createUpdate(
            "insert into card(id,cix,name,description,creation_date,conclusion_date,archived,list) values (:id, :cix, :name,:description,current_date,:conclusion_date,:archived,:listId)"
        )
            .bind("id", cardId)
            .bind("cix", lastIdx)
            .bind("name", name)
            .bind("description", description)
            .bind("conclusion_date", conclusionDate)
            .bind("archived", false)
            .bind("listId", listId)
            .execute()

        return cardId
    }

    override fun deleteCard(cardId: String): String {
        logger.info("\t\tAccessing DB for method deleteCard")
        val cardName = handle.createQuery("select name from card where id=:cardId").bind("cardId", cardId)
            .mapTo(String::class.java)
            .singleOrNull() ?: throw NotFound("card ($cardId) not found")

        handle.createUpdate(
            "delete from card where id=:cardId"
        )
            .bind("cardId", cardId)
            .execute()

        return "card ($cardName) deleted"
    }

    override fun getCards(listId: String, limit: Int?, skip: Int?): List<CardEntry> {
        logger.info("\t\tAccessing DB for method getCards")
        handle.createQuery("select name from list where id=:listId").bind("listId", listId).mapTo(String::class.java)
            .singleOrNull() ?: throw NotFound("list ($listId) not found")

        return handle.createQuery(
            "select id, name from card where list=:listId order by name asc limit :limit offset :offset"
        )
            .bind("listId", listId)
            .bind("limit", limit)
            .bind("offset", skip)
            .mapTo(CardEntry::class.java).list()
    }

    override fun cardDetails(cardId: String): Card {
        logger.info("\t\tAccessing DB for method cardDetails")
        return handle.createQuery(
            "select * from card where id=:id"
        ).bind("id", cardId).mapTo(Card::class.java).singleOrNull()
            ?: throw NotFound("card $cardId not found")
    }

    override fun moveCard(cardId: String, lid: String, cix: Int?): String {
        logger.info("\t\tAccessing DB for method moveCard")
        handle.createQuery(
            "select id from card where id=:id"
        ).bind("id", cardId).mapTo(String::class.java).singleOrNull()
            ?: throw NotFound("card $cardId not found")

        val cardsInList = handle.createQuery(
            "select * from card where list=:listId order by cix asc"
        ).bind("listId", lid).mapTo(Card::class.java).list()

        if (cix != null) {
            var cixInc = cix + 1

            cardsInList.filter { it.cix >= cix }.forEach {
                if (it.id != cardId) {
                    handle.createUpdate(
                        "update card set cix=:cix where id=:cardId"
                    )
                        .bind("cix", cixInc)
                        .bind("cardId", it.id)
                        .execute()

                    cixInc++
                }
            }
        }

        val usedCix = cix ?: handle.createQuery("select coalesce(max(cix), 0) + 1 from card where list=:listId")
            .bind("listId", lid)
            .mapTo(Int::class.java)
            .one()

        handle.createUpdate(
            "update card set list=:listId, cix=:cix where id=:cardId"
        )
            .bind("listId", lid)
            .bind("cix", usedCix)
            .bind("cardId", cardId)
            .execute()

        return "card ($cardId) moved to $lid position $cix"
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JdbiBoardsRepository::class.java)
    }
}
