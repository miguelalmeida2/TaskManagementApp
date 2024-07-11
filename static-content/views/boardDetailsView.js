import { div, h1, ul, li, a, p, input, label, form, button } from "../DSL.js";


export function boardDetailsView(board, boardLists, handleNewListSubmit, deleteList) {


   // obter o link de todas as listas do board
   let listLinks = boardLists.lists.map(list => {
      // guarda no map o link de cada lista associado a um botão de delete

      const deleteButton = button({ class: "btn btn-primary" }, "Delete")

      deleteButton.addEventListener("click", () => {
         deleteList(list.id).then(response => {
            if (response) {
               deleteButton.parentNode.remove()
            }
         })
      })

      return div(
         a({
            href: "#ListDetails/" + list.id,
            class: "link-primary"
         }, list.name),
         deleteButton,
         p()
      )
   })

   // se não tiver nenhuma list na board escreve esta informação
   if (listLinks.length === 0) {
      listLinks = [p("No lists associated to this board")]
   }

   return div(
      a(
         {
            href: "#Boards",
            class: "link-primary"
         },
         "Boards"
      ),
      p(),
      a(
         {
            href: "#BoardUsers/" + board.name,
            class: "link-primary"
         },
         "Board's Users"
      ),
      p(),
      h1("Name : " + board.name),
      ul(
         li("Description : " + board.description)
      ),
      h1("Create a new list"),
      form(
         {},
         (e) => {
             e.preventDefault()
             handleNewListSubmit(board.name)
         },
         div(
            label({ for: "listName" }, "List name:"),
            input({
               type: "listName", id: "listName",
               required: true
            }),
            button({ type: "submit" }, "Create list")
         )
      ),
      h1("Lists"),
      ...listLinks
   )
}