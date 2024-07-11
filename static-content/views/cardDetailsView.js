import {a, button, div, form, h1, label, li, option, p, select, ul} from "../DSL.js";


export function cardDetailsView(card, list, availableLists, moveCard) {

    const availableL = availableLists.lists.filter(l => l.name !== list.name)

    let listOptions = availableL.map(l => {
        return option({ value: l.id }, l.name)
    })

    const moveOptions = () => {
        if (listOptions.length === 0)
            return div(
                p("No lists available.")
            )
        else {
            return div(
                label({ for: "cardName" }, "Select list:"),
                select(
                    {id: "moveLists"},
                    ...listOptions
                ),
                p(),
                button({ type: "submit" }, "Move card")
            )
        }
    }

    return div(
        a(
            {
                href: "#ListDetails/" + card.list,
                class: "link-primary"
            },
            list.name
        ),
        p(),
        a({
            href: "#logout",
            class: "link-primary"
        }, 'Logout'),
        h1("Card details"),
        ul(
            li("Name : " + card.name),
            li("Id : " + card.id),
            li("Description : " + card.description),
            li("Creation Date : " + card.creationDate),
            li("Conclusion Date : " + card.conclusionDate),
            li("List : " + list.name)
        ),
        p(),
        h1("Move a card"),
        form(
            {},
            (e) => {
                e.preventDefault()
                const listId = document.querySelector("#moveLists").value;
                moveCard(card.id, listId, 1) //TODO change index
            },
            moveOptions()
        )
    )
}