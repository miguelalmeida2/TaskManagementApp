package pt.isel.ls.http.pipeline

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.RequestContexts
import org.http4k.core.Response
import org.http4k.core.Status
import org.slf4j.LoggerFactory
import pt.isel.ls.controllers.ErrorMessageModel
import pt.isel.ls.controllers.SharedState

class AuthenticationInterceptor(
    private val contexts: RequestContexts,
    private val authorizationHeaderProcessor: AuthorizationHeaderProcessor
) :
    Filter {
    override fun invoke(next: HttpHandler): HttpHandler = { request ->

        val user = if (request.header(NAME_AUTHORIZATION_HEADER) != null) authorizationHeaderProcessor.process(
            request.header(NAME_AUTHORIZATION_HEADER)
        ) else authorizationHeaderProcessor.processCookie(request.header(NAME_COOKIE_HEADER))

        if (user == null) {
            Response(Status.UNAUTHORIZED)
                .header("content-type", "application/problem+json")
                .body(Json.encodeToString(ErrorMessageModel(Status.UNAUTHORIZED.code, "Invalid or Missing Auth Token")))
                .header(NAME_WWW_AUTHENTICATE_HEADER, AuthorizationHeaderProcessor.SCHEME_BEARER)
        } else {
            contexts[request]["userId"] = SharedState(user.number, user.token)
            next(request)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthenticationInterceptor::class.java)
        private const val NAME_AUTHORIZATION_HEADER = "Authorization"
        private const val NAME_COOKIE_HEADER = "Cookie"
        private const val NAME_WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
    }
}
