import { API_BASE_URL } from "../router.js";
import { setError } from "../handlers.js";
import { div, h1, ul, li, a, p, input, label, form, button } from "../DSL.js";

class ListServices {

    async listDetails(listId) {
        try {
            const res = await fetch(API_BASE_URL + "/lists/" + listId, {})
            return await res.json()
        } catch (error) {
            console.log(error)
            setError(error.message)
        }
    }

    async listCards(listId) {
        try {
            const res = await fetch(API_BASE_URL + "/lists/" + listId + "/cards", {})
            return await res.json()
        } catch (error) {
            console.log(error)
            setError(error.message)
        }
    }

    async deleteList(listId) {
        try {
            const res = await fetch(API_BASE_URL + "/lists/" + listId, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' }
            })
            return await res.json()
        } catch (error) {
            console.log(error)
            setError(error.message)
        }
    }

    async newList(boardName) {
        try {
            const inputName = document.querySelector("#listName")

            const res = await fetch(
                API_BASE_URL + `/boards/${boardName}/lists`,
                {
                    method: "POST",
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({
                        name: inputName.value
                    })
                }
            )

            if (res.ok) {
                const list = await res.json()
                window.location.hash = `ListDetails/${list.listId}`
            } else
                throw new Error("Error creating list")
        } catch (error) {
            console.log(error)
            setError(error.message)
        }
    }

}

export default new ListServices();