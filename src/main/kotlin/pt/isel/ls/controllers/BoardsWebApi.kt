package pt.isel.ls.controllers

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.RequestContexts
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.slf4j.LoggerFactory
import pt.isel.ls.InvalidFormat
import pt.isel.ls.MissingParameters
import pt.isel.ls.Unauthorized
import pt.isel.ls.model.Board
import pt.isel.ls.model.ListDTO
import pt.isel.ls.model.NewCard
import pt.isel.ls.model.Username
import pt.isel.ls.services.BoardsServices
import pt.isel.ls.services.UsersServices

class BoardsWebApi(
    contexts: RequestContexts,
    private val servicesBoards: BoardsServices,
    private val servicesUsers: UsersServices
) {

    val routes = routes(
        "userDetails" bind Method.GET to { req: Request -> exceptionHandler { getUser(contexts, req) } },
        "boards" bind Method.POST to { req: Request -> exceptionHandler { createBoard(contexts, req) } },
        "boards/{boardName}" bind Method.PUT to { req: Request ->
            exceptionHandler {
                addUserToBoard(
                    contexts,
                    req
                )
            }
        }, // body parameters: userId
        "boards/{boardName}" bind Method.GET to { req: Request -> exceptionHandler { boardDetails(contexts, req) } },
        "boards" bind Method.GET to { req: Request -> exceptionHandler { getAvailableBoards(contexts, req) } },
        "boards/{boardName}/users" bind Method.GET to { req: Request ->
            exceptionHandler {
                boardUsers(
                    contexts,
                    req
                )
            }
        },
        "search/board" bind Method.GET to { req: Request ->
            exceptionHandler {
                searchBoard(
                    contexts,
                    req
                )
            }
        }, // board parameter : boardName

        "boards/{boardName}/lists" bind Method.POST to { req: Request -> exceptionHandler { addList(contexts, req) } },
        "lists/{listId}" bind Method.DELETE to { req: Request -> exceptionHandler { deleteList(contexts, req) } },
        "lists/{listId}" bind Method.GET to { req: Request -> exceptionHandler { listDetails(contexts, req) } },
        "boards/{boardName}/lists" bind Method.GET to { req: Request -> exceptionHandler { getLists(contexts, req) } },

        "lists/{listId}/cards" bind Method.POST to { req: Request ->
            exceptionHandler {
                createCard(
                    contexts, req
                )
            }
        }, // body parameters: name, description, due_date (optional)
        "cards/{cardId}" bind Method.DELETE to { req: Request -> exceptionHandler { deleteCard(contexts, req) } },
        "lists/{listId}/cards" bind Method.GET to { req: Request -> exceptionHandler { getCards(contexts, req) } },
        "cards/{cardId}" bind Method.GET to { req: Request ->
            exceptionHandler {
                cardDetails(
                    contexts, req
                )
            }
        },
        "cards/{cardId}/{lid}/{cix}" bind Method.PUT to { req: Request ->
            exceptionHandler {
                moveCard(
                    contexts, req
                )
            }
        },
    )

    private fun getUser(contexts: RequestContexts, request: Request): Response = exceptionHandler {
        logger.info("Parsing getUser Request..")
        val requestContext: SharedState = contexts[request]["userId"] ?: throw IllegalStateException()
        logger.info("Calling getUser Service..")
        val user = servicesUsers.getUserDetails(requestContext.token!!)
        logger.info("Responding getUser Request..")
        Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(UserDetails(user.number, user.name, user.email)))
    }

    private fun createBoard(contexts: RequestContexts, request: Request): Response {
        logger.info("Parsing createBoard Request..")
        val requestContext: SharedState = contexts[request]["userId"] ?: throw IllegalStateException()
        val req = Json.decodeFromString<Board>(request.bodyString())
        if (req.name.isBlank() || req.description.isBlank()) throw MissingParameters("name or description")
        // As this method requires bearer auth to be called then we'll perform a test User with fake id
        logger.info("Calling createBoard Service..")
        val apiResponse = servicesBoards.createBoard(req.name, req.description, requestContext.userId)
        logger.info("Responding createBoard Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(IdentifierBoard(apiResponse)))
    }

    private fun addUserToBoard(contexts: RequestContexts, request: Request): Response {
        logger.info("Parsing addUserToBoard Request..")
        val req = Json.decodeFromString<Username>(request.bodyString())
        if (req.username.isBlank()) throw MissingParameters("username missing")
        val boardName = request.path("boardName") ?: throw MissingParameters("Board name must be provided!")
        logger.info("Calling addUserToBoard Service..")
        val apiResponse = servicesBoards.addUserToBoard(boardName, req.username)
        logger.info("Responding addUserToBoard Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(UserAdded(apiResponse)))
    }

    private fun getAvailableBoards(contexts: RequestContexts, request: Request): Response {
        logger.info("Parsing getAvailableBoards Request..")
        val requestContext: SharedState = contexts[request]["userId"] ?: throw Unauthorized("Unauthorized user")
        val limit = request.query("limit")?.toInt()
        val skip = request.query("skip")?.toInt()
        logger.info("Calling getAvailableBoards Service..")
        val apiResponse = servicesBoards.getAvailableBoards(requestContext.userId, limit, skip)
        logger.info("Responding getAvailableBoards Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(apiResponse))
    }

    private fun boardDetails(contexts: RequestContexts, request: Request): Response {
        logger.info("Parsing boardDetails Request..")
        val boardName = request.path("boardName") ?: throw MissingParameters("Board name must be provided!")
        logger.info("Calling boardDetails Service..")
        val apiResponse = servicesBoards.boardDetails(boardName)
        logger.info("Responding boardDetails Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(apiResponse))
    }

    private fun boardUsers(contexts: RequestContexts, request: Request): Response {
        logger.info("Parsing boardUsers Request..")
        val boardName = request.path("boardName") ?: throw MissingParameters("Board name must be provided!")
        logger.info("Calling boardUsers Service..")
        val apiResponse = servicesBoards.boardUsers(boardName)
        logger.info("Responding boardUsers Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(UsersList(apiResponse)))
    }

    private fun searchBoard(contexts: RequestContexts, request: Request): Response {
        val requestContext: SharedState = contexts[request]["userId"] ?: throw Unauthorized("Unauthorized user")
        logger.info("Parsing searchBoard Request..")
        val searchParam = request.query("q") ?: throw MissingParameters("Query String was not provided")
        val limit = request.query("limit")?.toInt()
        val skip = request.query("skip")?.toInt()
        if (searchParam.isBlank()) throw MissingParameters("Search String cannot be empty")
        logger.info("Responding searchBoard Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(servicesBoards.searchBoard(requestContext.userId, searchParam, limit, skip)))
    }

    private fun addList(contexts: RequestContexts, request: Request): Response {
        logger.info("Parsing addList Request..")
        val req = Json.decodeFromString<ListDTO>(request.bodyString())
        if (req.name.isBlank()) throw MissingParameters("list name")
        val boardName = request.path("boardName") ?: throw MissingParameters("Board name must be provided!")
        logger.info("Calling addList Service..")
        val apiResponse = servicesBoards.addList(boardName, req.name)
        logger.info("Responding addList Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(IdentifierList(apiResponse)))
    }

    private fun deleteList(contexts: RequestContexts, request: Request): Response {
        logger.info("Parsing deleteList Request..")
        val listId = request.path("listId") ?: throw MissingParameters("ListId must be provided!")
        logger.info("Responding deleteList Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(Response(servicesBoards.deleteList(listId))))
    }

    private fun getLists(contexts: RequestContexts, request: Request): Response {
        logger.info("Parsing getLists Request..")
        val boardName = request.path("boardName") ?: throw MissingParameters("Board name must be provided!")
        val limit = request.query("limit")?.toInt()
        val skip = request.query("skip")?.toInt()
        logger.info("Responding getLists Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(BoardsLists(servicesBoards.getLists(boardName, limit, skip))))
    }

    private fun listDetails(contexts: RequestContexts, request: Request): Response {
        logger.info("Parsing listDetails Request..")
        val listId = request.path("listId") ?: throw MissingParameters("List id must be provided!")
        logger.info("Responding listDetails Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(servicesBoards.listDetails(listId)))
    }

    private fun createCard(contexts: RequestContexts, request: Request): Response {
        logger.info("Parsing createCard Request..")
        val req = Json.decodeFromString<NewCard>(request.bodyString())
        if (req.name.isBlank() || req.description.isBlank()) throw MissingParameters("list name or description")
        val listId = request.path("listId") ?: throw MissingParameters("List id must be provided!")

        try {
            req.conclusionDate?.format()
        } catch (e: Exception) {
            throw InvalidFormat("conclusion date is invalid")
        }
        logger.info("Responding createCard Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(
                Json.encodeToString(
                    IdentifierCard(
                        servicesBoards.createCard(
                            listId,
                            req.name,
                            req.description,
                            req.conclusionDate
                        )
                    )
                )
            )
    }

    private fun deleteCard(contexts: RequestContexts, request: Request): Response {
        logger.info("Parsing deleteCard Request..")
        val cardId = request.path("cardId") ?: throw MissingParameters("CardId must be provided!")
        logger.info("Responding deleteCard Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(Response(servicesBoards.deleteCard(cardId))))
    }

    private fun getCards(contexts: RequestContexts, request: Request): Response {
        logger.info("Parsing getCards Request..")
        val listId = request.path("listId") ?: throw MissingParameters("List id must be provided!")
        val limit = request.query("limit")?.toInt()
        val skip = request.query("skip")?.toInt()
        logger.info("Responding getCards Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(CardsList(servicesBoards.getCards(listId, limit, skip))))
    }

    private fun cardDetails(contexts: RequestContexts, request: Request): Response {
        logger.info("Parsing cardDetails Request..")
        val cardId = request.path("cardId") ?: throw MissingParameters("CardId must be provided!")
        logger.info("Responding cardDetails Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(servicesBoards.cardDetails(cardId)))
    }

    private fun moveCard(contexts: RequestContexts, request: Request): Response {
        logger.info("Parsing moveCard Request..")
        val listId = request.path("lid") ?: throw MissingParameters("ListId must be provided!")
        val cardId = request.path("cardId") ?: throw MissingParameters("CardId must be provided!")
        var cix = request.path("cix")

        if (cix == "null") cix = null
        logger.info("Responding moveCard Request..")
        return Response(Status.OK)
            .header("content-type", "application-json")
            .body(Json.encodeToString(Response(servicesBoards.moveCard(cardId, listId, cix?.toInt()))))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BoardsWebApi::class.java)
    }
}
