package pt.isel.ls

import org.junit.jupiter.api.Test
import pt.isel.ls.database.mem.MemBoardsRepository
import pt.isel.ls.database.mem.MemUsersRepository
import pt.isel.ls.services.BoardServicesTest
import pt.isel.ls.services.UserServicesTest
import kotlin.test.assertEquals
import kotlin.test.assertFails

class UserTests {

    private val data = MemUsersRepository()
    private val users = UserServicesTest(data)
    private val services = BoardServicesTest(MemBoardsRepository(MemUsersRepository()))

    @Test
    fun `create a new user`() {
        val userName = "Sofia"
        val email = "sofia.example@gmail.com"
        val password = "ntASafePass"
        val (number, name, newEmail, token) = users.createUser(userName, email, password)
        assertEquals(userName, data.users[number]?.name)
    }

    @Test
    fun `create duplicate users throws exception`() {
        val userName = "Filipe"
        val email = "filipe.example@gmail.com"
        val password = "ntASafePass"
        val (number, name, newEmail, token) = users.createUser(userName, email, password)
        assertFails { users.createUser(userName, email, password) }
    }

    /*
    @Test
    fun `get userId by userName `() {
        val userName = "Sofia"
        val email = "sofia.example@gmail.com"
        val (token, id) = users.createUser(userName, email)
        val userExample = users.getIdByUsername(userName)
        assertEquals(id, userExample)
    }
*/
    @Test
    fun `get user by ID`() {
        val userName = "Maria"
        val email = "maria.example@gmail.com"
        val password = "ntASafePass"
        val (number, name, newEmail, token) = users.createUser(userName, email, password)
        val userExample = users.getUserById(number)
        assertEquals(userName, userExample.name)
    }

    /*
    @Test
    fun `add a user to the board`() {
        val boardName = "Board"
        val user = users.createUser("Darlene", "Darlene@gmail.com")
        val testerUser = users.createUser("Sonia", "sonia@gmail.com")
        services.createBoard(boardName, "Some board example", testerUser.second)
        val res = services.addUserToBoard(boardName,"Darlene")
        assertTrue(res)
    }
*/
}
