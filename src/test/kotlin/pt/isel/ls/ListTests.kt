package pt.isel.ls

import org.junit.jupiter.api.Test
import pt.isel.ls.database.mem.MemBoardsRepository
import pt.isel.ls.database.mem.MemUsersRepository
import pt.isel.ls.services.BoardServicesTest
import pt.isel.ls.services.UserServicesTest
import kotlin.test.assertEquals
import kotlin.test.assertFails

class ListTests {
    private val data = MemBoardsRepository(MemUsersRepository())
    private val services = BoardServicesTest(data)
    private val users = UserServicesTest(MemUsersRepository())

    private val globalUser = users.createUser("Sonia", "sonia@gmail.com", "ntASafePass")
    private val testerUser = globalUser.number

    private val boardName = services.createBoard("exampleBoard", "Some boardList example1", testerUser)

    @Test
    fun `create and add list into a board `() {
        val listName = "firstList"
        val list = services.addList(boardName, listName)
        assertEquals(data.lists[list]?.name, listName)
    }

    @Test
    fun `add a duplicate list at same board throws exception`() {
        val listName = "firstList1"
        services.addList(boardName, listName)
        assertFails { services.addList(boardName, listName) }
    }

    // TODO
//    @Test
//    fun `get lists`() {
//        val listsName = mutableListOf("list1", "list2", "list3")
//
//        listsName.forEach { name ->
//            services.addList(boardName, name)
//        }
//        val lists = services.getLists(boardName, null, null)
//        assertTrue(lists.size >= 3)
//    }

    @Test
    fun `get list details`() {
        val listId = services.addList(boardName, "toDo")
        val details = services.listDetails(listId)
        assertEquals(details.id, listId)
    }

    @Test
    fun `get inexistents list details throws exception`() {
        val listId = services.addList(boardName, "done")
        services.deleteList(listId)
        assertFails { services.listDetails(listId) }
    }

    @Test
    fun `delete a list`() {
        val listId = services.addList(boardName, "doing")
        val res = services.deleteList(listId)
        assertEquals("$listId deleted", res)
    }
}
