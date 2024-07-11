package pt.isel.ls.controllers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.slf4j.LoggerFactory
import pt.isel.ls.MissingParameters
import pt.isel.ls.services.UsersServices

class UsersWebApi(private val servicesUsers: UsersServices) {

    val routes = routes(
        "login" bind Method.POST to { req: Request -> exceptionHandler { login(req) } },
        "logout" bind Method.POST to { req: Request -> exceptionHandler { logout() } },
        "signup" bind Method.POST to { req: Request -> exceptionHandler { createUser(req) } },
    )

    private fun login(request: Request): Response {
        logger.info("Parsing Login Request..")
        val req = Json.decodeFromString<UserLogin>(request.bodyString())
        if (req.email.isBlank() || req.password.isBlank()) throw MissingParameters("Parameter missing")
        logger.info("Calling Login Service..")
        val userDetails = servicesUsers.authUser(req.email, req.password)
        val cookie = Cookie("token", userDetails.token).path("/").httpOnly()
        logger.info("Responding Login Request..")
        return Response(Status.CREATED)
            .header("content-type", "application-json")
            .cookie(cookie)
            .body(Json.encodeToString(UserDetails(userDetails.number, userDetails.name, userDetails.email)))
    }

    private fun logout(): Response {
        val cookie = Cookie("token", value = "", maxAge = -1, httpOnly = true, path = "/")
        return Response(Status.OK).cookie(cookie)
    }

    private fun createUser(request: Request): Response = exceptionHandler {
        logger.info("Parsing Register Request..")
        val req = Json.decodeFromString<UserRegister>(request.bodyString())
        if (req.name.isBlank() || req.email.isBlank() || req.password.isBlank()) throw MissingParameters("Parameter missing")
        logger.info("Calling Register Service..")
        val userDetails = servicesUsers.createUser(req.name, req.email, req.password)
        val cookie = Cookie("token", userDetails.token).path("/").httpOnly()
        logger.info("Responding Register Request..")
        Response(Status.CREATED)
            .header("content-type", "application-json")
            .cookie(cookie)
            .body(Json.encodeToString(UserDetails(userDetails.number, userDetails.name, userDetails.email)))
    }
    companion object {
        private val logger = LoggerFactory.getLogger(UsersWebApi::class.java)
    }
}
