package pt.isel.ls.database

import pt.isel.ls.model.UserDetails
import pt.isel.ls.model.UserDetailsLoginRegister

interface UsersRepository {
    fun getUserById(id: Int): UserDetails
    fun getUserDetails(bearerToken: String): UserDetails
    fun checkBearerToken(bearerToken: String): Int?
    fun authUser(email: String, hashPassword: String): UserDetailsLoginRegister
    fun createUser(username: String, email: String, password: String): UserDetailsLoginRegister
}
