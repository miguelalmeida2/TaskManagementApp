package pt.isel.ls.services

import pt.isel.ls.model.UserDetails
import pt.isel.ls.model.UserDetailsLoginRegister

interface IUsersServices {

    /**-------------------------------Users--------------------------------------**/
    /**
     * Gets a User by his [id].
     * @return correspondent User Details
     */
    fun getUserById(id: Int): UserDetails

    /**
     * Creates a new User with [name] and [email].
     * @return Pair of UUID and Number of newly created player
     */
    fun createUser(name: String, email: String, password: String): UserDetailsLoginRegister

    /**
     * Gets user number of given [bearerToken] or NULL if there's none.
     * @return number
     */
    fun checkBearerToken(bearerToken: String): Int?

    /**
     * Authenticates [username] credentials with given [hashPassword].
     * @return UUID of [username] if credentials match
     */
    fun authUser(email: String, hashPassword: String): UserDetailsLoginRegister
}
