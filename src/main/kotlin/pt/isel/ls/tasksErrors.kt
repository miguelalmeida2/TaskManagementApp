package pt.isel.ls

import kotlinx.serialization.Serializable
import org.http4k.core.Status

@Serializable
abstract class Errors : Exception() {
    abstract fun getStatus(): Status
}

class Unauthorized(override val message: String) : Errors() {
    override fun getStatus() = Status.UNAUTHORIZED
}

class FailedAuthenticationException(override val message: String) : Errors() {
    override fun getStatus() = Status.BAD_REQUEST
}

class MissingParameters(override val message: String) : Errors() {
    override fun getStatus() = Status.BAD_REQUEST
}

class NotFound(override val message: String) : Errors() {
    override fun getStatus() = Status.NOT_FOUND
}

class DataBaseNotAvailable(override val message: String) : Errors() {
    override fun getStatus() = Status.INTERNAL_SERVER_ERROR
}

class NativeRequestDoesntExistException(override val message: String) : Errors() {
    override fun getStatus() = Status.BAD_REQUEST
}

class Conflict(override val message: String) : Errors() {
    override fun getStatus() = Status.CONFLICT
}

class DataAlreadyExists(override val message: String) : Errors() {
    override fun getStatus() = Status.BAD_REQUEST
}

class InvalidFormat(override val message: String) : Errors() {
    override fun getStatus() = Status.BAD_REQUEST
}

class DataSourceError(override val message: String) : Errors() {
    override fun getStatus() = Status.INTERNAL_SERVER_ERROR
}
