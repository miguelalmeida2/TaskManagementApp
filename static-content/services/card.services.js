import {API_BASE_URL} from "../router.js";
import {setError} from "../handlers.js";

class CardServices {

    async cardDetails(cardId) {
        try {
            const res = await fetch(API_BASE_URL + "/cards/" + cardId, {})
            return await res.json()
        } catch (error) {
            console.log(error)
            setError(error.message)
        }
    }

    async newCard(listId) {
        try {
            const inputName = document.querySelector("#cardName")
            const inputDescription = document.querySelector("#cardDescription")
            const inputConclusionDate = document.querySelector("#cardConclusionDate")

            const res = await fetch(`${API_BASE_URL}/lists/${listId}/cards`, {
                method: "POST",
                body: JSON.stringify({
                    name: inputName.value,
                    description: inputDescription.value,
                    conclusionDate: inputConclusionDate.value
                })
            })
            if (res.ok) {
                const card = await res.json()
                window.location.hash = `CardDetails/${card.cardId}`
            }
        } catch (error) {
            console.log(error)
            setError(error.message)
        }
    }

    async deleteCard(cardId) {
        try {
            const res = await fetch(`${API_BASE_URL}/cards/${cardId}`, {
                method: "DELETE"
            })
            return res.ok;
        } catch (error) {
            console.log(error)
            setError(error.message)
        }
    }

    async moveCard(cardId, listId, index) {
        try {
            const res = await fetch(`${API_BASE_URL}/cards/${cardId}/${listId}/${index}`, {
                method: "PUT"
            })
            if (res.ok)
                window.location.hash = `ListDetails/${listId}`;
        } catch (error) {
            console.log(error)
            setError(error.message)
        }
    }
}

export default new CardServices();