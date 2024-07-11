package pt.isel.ls.services

import pt.isel.ls.InvalidFormat
import pt.isel.ls.MissingParameters
import pt.isel.ls.database.mem.MemUsersRepository
import pt.isel.ls.model.UserDetails
import pt.isel.ls.model.UserDetailsLoginRegister

class UserServicesTest(private val users: MemUsersRepository) : IUsersServices {

    override fun createUser(name: String, email: String, password: String): UserDetailsLoginRegister {
        if (name.isBlank() && email.isBlank()) throw MissingParameters("name & email")
        if (name.isBlank()) throw MissingParameters("name")
        if (email.isBlank()) throw MissingParameters("email")
        if (name.length > 32) throw InvalidFormat("name has size > 32chars")
        if (email.length > 32) throw InvalidFormat("email has size > 32chars")
        return users.createUser(name, email, password)
    }

    override fun getUserById(id: Int): UserDetails {
        if (id < 0) throw InvalidFormat("id cannot be lower than 0")
        return users.getUserById(id)
    }

    override fun checkBearerToken(bearerToken: String): Int? {
        return users.checkBearerToken(bearerToken)
    }

    override fun authUser(email: String, hashPassword: String): UserDetailsLoginRegister {
        TODO("Not yet implemented")
    }
}
