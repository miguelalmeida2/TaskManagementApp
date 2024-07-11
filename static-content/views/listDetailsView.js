import {a, button, div, form, h1, input, label, li, p, ul} from "../DSL.js";
import cardServices from "../services/card.services.js";

export function listDetailsView(list, listCards, handleNewCardSubmit) {

    let cardLinks = listCards.cards.map(card => {
        const deleteButton = button({ class: "btn btn-primary" }, "Delete")

        deleteButton.addEventListener("click", async () => {
            if(await cardServices.deleteCard(card.id))
                deleteButton.parentNode.remove()
        })

        return div(
            a({
                href: "#CardDetails/" + card.id,
                class: "link-primary"
            }, card.name),
            deleteButton,
            p()
        )
    })

    if (cardLinks.length === 0)
        cardLinks = [p("No cards associated to this list")]

    const minDate = new Date().toISOString().split("T")[0];

    return div(
        a({
            href: "#BoardDetails/" + list.board,
            class: "link-primary"
        }, list.board),
        p(),
        a({
            href: "#logout",
            class: "link-primary"
        }, 'Logout'),
        h1("List Details"),
        ul(
            li("Name : " + list.name),
            li("Board : " + list.board)
        ),
        h1("Create a new card"),
        form(
            {},
            (e) => {
                e.preventDefault()
                handleNewCardSubmit(list.id)
            },
            div(
                label({ for: "cardName" }, "Card name:"),
                input({
                    type: "cardName", id: "cardName",
                    required: true
                }),
                label({ for: "cardDescription" }, "Description:"),
                input({
                    type: "cardDescription", id: "cardDescription",
                    required: true
                }),
                p(),
                label({ for: "cardConclusionDate" }, "Conclusion date:"),
                input({
                    type: "date", id: "cardConclusionDate", min: minDate, required: true
                }),
                button({ type: "submit" }, "Create card")
            )
        ),
        h1("Cards"),
        ...cardLinks
    )
}