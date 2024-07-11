package pt.isel.ls

import org.junit.jupiter.api.Test
import pt.isel.ls.database.mem.MemBoardsRepository
import pt.isel.ls.database.mem.MemUsersRepository
import pt.isel.ls.services.BoardServicesTest
import pt.isel.ls.services.UserServicesTest
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class CardTests {

    private val data = MemBoardsRepository(MemUsersRepository())
    private val services = BoardServicesTest(data)
    private val users = UserServicesTest(MemUsersRepository())

    private val globalUser = users.createUser("Sonia", "sonia@gmail.com", "ntASafePass")
    private val testerUser = globalUser.number
    @Test
    fun `create and add  card into a list`() {
        val boardName = "BoardList"
        services.createBoard(boardName, "Some board example", testerUser)
        val listId = services.addList(boardName, "firstList")
        val cardId = services.createCard(listId, "first", "first card created", null)
        assertEquals(cardId, data.cards[cardId]?.id)
    }

    // TODO
//    @Test
//    fun `get all list's cards`() {
//        val boardName = "BoardListEx"
//        services.createBoard(boardName, "Some board example2", testerUser)
//        val listId = services.addList(boardName, "firstListEx")
//        services.createCard(listId, "second", "second card created", null)
//        val cards = services.getCards(listId, null, null)
//        assertEquals(1, cards.size)
//    }

    @Test
    fun `get empty list's cards`() {
        val boardName = "BoardListE"
        services.createBoard(boardName, "Some board example3", testerUser)
        val listId = services.addList(boardName, "firstListEx1")
        // data.createCard(boardName,listName,"third","third card created",null)
        val cards = services.getCards(listId, null, null)
        assertTrue(cards.isEmpty())
    }

    @Test
    fun `get card details`() {
        val boardName = "anotherBoard1"
        val listName = "firstListEx2"
        val cardName = "third"
        services.createBoard(boardName, "Some board example4", testerUser)
        val listId = services.addList(boardName, listName)
        val cardId = services.createCard(listId, cardName, "third card created", null)
        val details = services.cardDetails(cardId)
        assertEquals(cardName, details.name)
    }

    @Test
    fun `move a card to another list`() {
        val boardName = "anotherBoard2"
        val listName1 = "firstListEx3"
        val listName2 = "firstListExa"
        services.createBoard(boardName, "Some board example4", testerUser)
        val listId1 = services.addList(boardName, listName1)
        val listId2 = services.addList(boardName, listName2)
        val cardId = services.createCard(listId1, "fourth", "anther card created", null)
        services.moveCard(cardId, listId2, null)
        assertEquals(listId2, data.cards[cardId]?.list,)
    }

    @Test
    fun `move a  card to a list that is not at board throws exception`() {
        val boardName = "anotherBoard3"
        services.createBoard(boardName, "Some board example5", testerUser)
        val listId = services.addList(boardName, "firstListEx4")
        val listId2 = services.addList(boardName, "firstListE")
        services.deleteList(listId2)
        val cardId = services.createCard(listId, "abcd", "yes, card created", null)
        assertFails { services.moveCard(cardId, listId2, null) }
    }
}
