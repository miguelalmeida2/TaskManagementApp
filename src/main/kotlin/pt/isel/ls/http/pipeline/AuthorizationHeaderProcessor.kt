package pt.isel.ls.http.pipeline

import org.slf4j.LoggerFactory
import pt.isel.ls.model.User
import pt.isel.ls.services.UsersServices

class AuthorizationHeaderProcessor(
    private val usersService: UsersServices
) {
    fun process(authorizationValue: String?): User? {
        if (authorizationValue == null) {
            return null
        }
        val parts = authorizationValue.trim().split(" ")
        if (parts.size != 2) {
            return null
        }
        if (parts[0].lowercase() != SCHEME_BEARER) {
            return null
        }
        val id = usersService.checkBearerToken(parts[1]) ?: return null
        return User(id, parts[1])
    }

    fun processCookie(authorizationValue: String?): User? {
        if (authorizationValue == null) {
            return null
        }
        val cookies = HashMap<String, String>()
        authorizationValue.split("; ")
            .forEach { cookie ->
                val index = cookie.indexOf('=')
                val name = cookie.substring(0, index)
                val value = cookie.substring(index + 1).trim('"')
                cookies[name] = value
            }
        val tokenCookie = cookies["token"] ?: return null
        val id = usersService.checkBearerToken(tokenCookie) ?: return null
        return User(id, tokenCookie)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthorizationHeaderProcessor::class.java)
        const val SCHEME_BEARER = "bearer"
    }
}
