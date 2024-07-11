package pt.isel.ls.database.jdbi

import org.jdbi.v3.core.Handle
import org.slf4j.LoggerFactory
import pt.isel.ls.DataAlreadyExists
import pt.isel.ls.DataSourceError
import pt.isel.ls.FailedAuthenticationException
import pt.isel.ls.NotFound
import pt.isel.ls.database.UsersRepository
import pt.isel.ls.model.UserDetails
import pt.isel.ls.model.UserDetailsLoginRegister
import java.util.UUID

class JdbiUsersRepository(private val handle: Handle) : UsersRepository {
    override fun getUserById(id: Int): UserDetails {
        logger.info("\t\tAccessing DB for method getUserById")
        return handle.createQuery("select number,name,email from users where number=:id")
            .bind("id", id)
            .mapTo(UserDetails::class.java)
            .singleOrNull() ?: throw NotFound("user not found")
    }

    override fun getUserDetails(bearerToken: String): UserDetails {
        logger.info("\t\tAccessing DB for method getUserDetails")
        return handle.createQuery("select number,name,email from users where token = :token")
            .bind("token", bearerToken)
            .mapTo(UserDetails::class.java)
            .singleOrNull() ?: throw NotFound("User not found")
    }

    override fun checkBearerToken(bearerToken: String): Int? {
        logger.info("\t\tAccessing DB for method checkBearerToken")
        return handle.createQuery("select number from users where token=:bearerToken")
            .bind("bearerToken", bearerToken)
            .mapTo(Int::class.java)
            .singleOrNull()
    }

    override fun authUser(email: String, hashPassword: String): UserDetailsLoginRegister {
        logger.info("\t\tAccessing DB for method authUser")
        return handle.createQuery("select number,name,email,token from users where email=:email and password=:hashpassword")
            .bind("email", email)
            .bind("hashpassword", hashPassword)
            .mapTo(UserDetailsLoginRegister::class.java)
            .singleOrNull() ?: throw FailedAuthenticationException("Incorrect Username Or Password.")
    }

    override fun createUser(username: String, email: String, password: String): UserDetailsLoginRegister {
        logger.info("\t\tAccessing DB for method createUser")
        if (handle.createQuery("select name from users where name=:name").bind("name", username)
            .mapTo(String::class.java)
            .singleOrNull() != null
        )
            throw DataAlreadyExists("username unavailable")

        if (handle.createQuery("select email from users where email=:email").bind("email", email)
            .mapTo(String::class.java)
            .singleOrNull() != null
        )
            throw DataAlreadyExists("email already used")

        val newUUID = UUID.randomUUID().toString()
        val number = handle.createUpdate(
            "INSERT INTO users(name, password, token, email) VALUES (:name,:password, :token, :email) RETURNING number"
        )
            .bind("name", username)
            .bind("password", password)
            .bind("token", newUUID)
            .bind("email", email)
            .executeAndReturnGeneratedKeys("number")
            .mapTo(Int::class.java)
            .firstOrNull() ?: throw DataSourceError("Error creating user")
        return UserDetailsLoginRegister(number, username, email, newUUID)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JdbiUsersRepository::class.java)
    }
}
