package pt.isel.ls

import org.junit.jupiter.api.Test
import pt.isel.ls.database.mem.MemBoardsRepository
import pt.isel.ls.database.mem.MemUsersRepository
import pt.isel.ls.services.BoardServicesTest
import pt.isel.ls.services.UserServicesTest
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class BoardTests {

    private val data = MemBoardsRepository(MemUsersRepository())
    private val services = BoardServicesTest(data)
    private val users = UserServicesTest(MemUsersRepository())

    private val globalUser = users.createUser("Sonia", "sonia@gmail.com", "ntASafePass")
    private val testerUser = globalUser.number

    @Test
    fun `create a new board`() {
        val boardName = "First"
        val description = "The first board at this API"
        val user = 1
        services.createBoard(boardName, description, user)
        assertEquals(boardName, data.boards[boardName]?.name)
    }

    @Test
    fun `get available boards`() {
        services.createBoard("boardName", "Some board example1", testerUser)
        services.createBoard("boardExample", "Some board example2", testerUser)
        val boards = services.getAvailableBoards(testerUser, null, null)
        assertTrue(boards.boards.containsAll(mutableListOf("boardName", "boardExample")))
    }

    @Test
    fun `get board details`() {
        val boardName = "anotherBoard"
        services.createBoard(boardName, "Some board example3", testerUser)
        val details = services.boardDetails(boardName)
        assertEquals(details.name, boardName)
    }

    @Test
    fun `user without permission add a user to the board throws exception`() {
        val boardName = "BoardsTest"
        services.createBoard(boardName, "Some board example4", testerUser)
        assertFails { services.addUserToBoard(boardName, "4") }
    }
}
