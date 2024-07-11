package pt.isel.ls.database.jdbi
import org.jdbi.v3.core.Handle
import pt.isel.ls.database.BoardsRepository
import pt.isel.ls.database.UsersRepository

class JdbiTransaction(
    private val handle: Handle,
) : Transaction {

    override val usersRepository: UsersRepository by lazy { JdbiUsersRepository(handle) }

    override val boardsRepository: BoardsRepository by lazy { JdbiBoardsRepository(handle) }

    override fun rollback() {
        handle.rollback()
    }
}
