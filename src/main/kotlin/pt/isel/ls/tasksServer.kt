package pt.isel.ls

import org.http4k.core.RequestContexts
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.slf4j.LoggerFactory
import pt.isel.ls.controllers.BoardsWebApi
import pt.isel.ls.controllers.UsersWebApi
import pt.isel.ls.database.jdbi.JdbiTransactionManager
import pt.isel.ls.database.jdbi.configure
import pt.isel.ls.http.pipeline.AuthenticationInterceptor
import pt.isel.ls.http.pipeline.AuthorizationHeaderProcessor
import pt.isel.ls.services.BoardsServices
import pt.isel.ls.services.UsersServices

fun main() {

    val log = LoggerFactory.getLogger("TaskServer")

    log.info("Searching for DB env Variale...")
    val jdbiDatabaseURL = System.getenv("LS_DATABASE_URL")
    log.info("Establishing connection with DB...")
    val jdbi = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL(jdbiDatabaseURL)
        }
    ).configure()

    log.info("Injecting needed Dependencies...")
    val contexts = RequestContexts()
    val transManager = JdbiTransactionManager(jdbi = jdbi)
    val servicesUsers = UsersServices(transManager)
    val servicesBoards = BoardsServices(transManager)
    val usersController = UsersWebApi(servicesUsers)
    val boardsController = BoardsWebApi(contexts, servicesBoards, servicesUsers)
    val authProcessor = AuthorizationHeaderProcessor(servicesUsers)

    log.info("Registering Routes...")
    val app = routes(
        usersController.routes,
        boardsController.routes.withFilter(AuthenticationInterceptor(contexts, authProcessor)),
        singlePageApp(ResourceLoader.Directory("static-content")),
    )

    val port = System.getenv("PORT")?.toIntOrNull() ?: 9000
    log.info("Initializing Server...")
    val server = ServerFilters
        .InitialiseRequestContext(contexts).then(app).asServer(Jetty(port)).start()
    log.info("Server Listening...")
    // readln()
    // log.info("Server Stopping...")
    // server.stop()
}
