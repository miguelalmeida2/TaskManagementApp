package pt.isel.ls.database.jdbi

import pt.isel.ls.database.BoardsRepository
import pt.isel.ls.database.UsersRepository

interface Transaction {

    val usersRepository: UsersRepository

    val boardsRepository: BoardsRepository

    fun rollback()
}
