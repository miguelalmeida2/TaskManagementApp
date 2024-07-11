package pt.isel.ls.database.mem

import pt.isel.ls.DataAlreadyExists
import pt.isel.ls.NotFound
import pt.isel.ls.database.UsersRepository
import pt.isel.ls.model.UserComplete
import pt.isel.ls.model.UserDetails
import pt.isel.ls.model.UserDetailsLoginRegister
import java.util.UUID

class MemUsersRepository : UsersRepository {

    val users = HashMap<Int, UserComplete>()
    private var nextUserId = 1

    override fun getUserById(id: Int): UserDetails {
        val user = users[id] ?: throw NotFound("user by given id")
        return UserDetails(user.number, user.name, user.email)
    }

    override fun getUserDetails(bearerToken: String): UserDetails {
        TODO("Not yet implemented")
    }

    fun getIdByUsername(username: String): Int? = users.entries.find { it.value.name == username }?.key

    override fun checkBearerToken(bearerToken: String): Int? {
        TODO("Not yet implemented")
    }

    override fun authUser(email: String, hashPassword: String): UserDetailsLoginRegister {
        TODO("Not yet implemented")
    }

    override fun createUser(username: String, email: String, password: String): UserDetailsLoginRegister {
        if (users.values.find { it.name == username } != null)
            throw DataAlreadyExists("username already exists")

        nextUserId++
        val newToken = UUID.randomUUID().toString()

        users[nextUserId] = UserComplete(nextUserId, username, newToken, email)
        return UserDetailsLoginRegister(nextUserId, username, email, newToken)
    }
}
