import { API_BASE_URL } from "../router.js";
import { setError } from "../handlers.js";

class BoardServices {

    async getBoard(boardName) {
        try {
            const res = await fetch(API_BASE_URL + "/boards/" + boardName, {})
            return await res.json()
        } catch (error) {
            console.log(error)
            setError(error.message)
        }
    }

    async boards(skip, limit) {
        try {
            const res = await fetch(API_BASE_URL + "/boards?limit=" + limit + "&skip=" + skip, {})
            return await res.json()
        } catch (error) {
            console.log(error)
            setError(error.message)
        }
    }

    async boardLists(boardName) {
        try {
            const res = await fetch(API_BASE_URL + "/boards/" + boardName + "/lists", {})
            return await res.json()
        } catch (error) {
            console.log(error)
            setError(error.message)
        }
    }

    async searchBoard(queryParams) {
        try {
            const res = await fetch(API_BASE_URL + "/search/board?q=" + queryParams.q + "&limit=" + queryParams.limit + "&skip=" + queryParams.skip, {})
            return res.json()
        } catch (error) {
            console.log(error)
            setError(error.message)
        }
    }

    async newBoard() {
        try {
            const inputName = document.querySelector("#boardName")
            const inputDescription = document.querySelector("#boardDescription")

            const res = await fetch(
                API_BASE_URL + "/boards",
                {
                    method: "POST",
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        name: inputName.value,
                        description: inputDescription.value
                    })
                }
            )

            if(res.ok){
                const board = await res.json()
                window.location.hash = `BoardDetails/${board.boardName}`
            } else
                throw new Error("Error creating board")
        } catch (error) {
            console.log(error)
            setError(error.message)
        }

    }
}

export default new BoardServices();