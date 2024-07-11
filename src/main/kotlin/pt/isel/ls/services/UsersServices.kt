package pt.isel.ls.services

import org.slf4j.LoggerFactory
import pt.isel.ls.InvalidFormat
import pt.isel.ls.database.jdbi.TransactionManager
import pt.isel.ls.model.UserDetails
import pt.isel.ls.model.UserDetailsLoginRegister
import java.math.BigInteger
import java.security.MessageDigest

class UsersServices(private val transactionManager: TransactionManager) : IUsersServices {

    override fun createUser(name: String, email: String, password: String): UserDetailsLoginRegister {
        logger.info("\tChecking createUser params format and content")
        if (name.length > 32) throw InvalidFormat("name has size > 32chars")
        if (email.length > 32) throw InvalidFormat("email has size > 32chars")
        if (password.length > 32) throw InvalidFormat("password has size > 32chars")
        logger.info("\tCalling createUser Repo Method.. ")
        return transactionManager.run { it.usersRepository.createUser(name, email, password.md5()) }
    }

    override fun getUserById(id: Int): UserDetails {
        logger.info("\tChecking getUserById params format and content")
        if (id < 0) throw InvalidFormat("id cannot be lower than 0")
        logger.info("\tCalling getUserById Repo Method.. ")
        return transactionManager.run { it.usersRepository.getUserById(id) }
    }

    fun getUserDetails(bearerToken: String): UserDetails {
        logger.info("\tCalling getUserDetails Repo Method.. ")
        return transactionManager.run { it.usersRepository.getUserDetails(bearerToken) }
    }

    override fun checkBearerToken(bearerToken: String): Int? {
        logger.info("\tCalling checkBearerToken Repo Method.. ")
        return transactionManager.run { it.usersRepository.checkBearerToken(bearerToken) }
    }

    override fun authUser(email: String, hashPassword: String): UserDetailsLoginRegister {
        logger.info("\tChecking authUser params format and content")
        if (email.length > 32)
            throw InvalidFormat("Username length can't be bigger than 32chars.")
        if (hashPassword.length > 32)
            throw InvalidFormat("Password length can't be bigger than 32chars.")
        logger.info("\tCalling authUser Repo Method.. ")
        return transactionManager.run {
            val usersRepository = it.usersRepository
            usersRepository.authUser(email, hashPassword.md5())
        }
    }
    companion object {
        private val logger = LoggerFactory.getLogger(UsersServices::class.java)
    }
}

/**
 * Generates an MD5 Hash for a certain password
 */
fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}
