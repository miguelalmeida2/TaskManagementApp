import { div, h1, ul, li, a, p, input, label, form, button } from "../DSL.js";


export function searchView(boardsObj, queryParams) {

  let canGetPrevious
  let currentPage = 0
  let previousPage = 0
  let nextPage = 5

  // paginação
  if (queryParams !== null) {
    nextPage = parseInt(queryParams.skip, 10) + 5
    canGetPrevious = parseInt(queryParams.skip, 10) - 5
    currentPage = parseInt(queryParams.skip, 10)
    previousPage = canGetPrevious < 0 ? 0 : canGetPrevious;
  }

  const searchButton = button({ class: "btn btn-primary" }, "Search")
  const previousButton = button({ class: "btn btn-primary" }, "Previous")
  const nextButton = button({ class: "btn btn-primary" }, "Next")

  previousButton.disabled = !boardsObj.previous
  nextButton.disabled = !boardsObj.next

  searchButton.addEventListener("click", () => {
    const searchInput = document.getElementById("searchBoardInput");
    const query = searchInput.value;
    window.location.hash = "Search/Board?q=" + encodeURIComponent(query) + "&skip=0"
  })
  previousButton.addEventListener("click", () => { window.location.hash = "Search/Board?q=" + queryParams.q + "&skip=" + previousPage })
  nextButton.addEventListener("click", () => { window.location.hash = "Search/Board?q=" + queryParams.q + "&skip=" + nextPage })


  const boardLinks = boardsObj.boards.map(board => {
    return div(
      a({
        href: "#BoardDetails/" + board.name,
        class: "link-primary"
      }, board.name),
      p(board.description)
    )
  });

  if (boardLinks.length === 0) {
    boardLinks = [p("We didn't find boards with this name!")]
  }

  return div(
    a(
      {
        href: "#UserHome",
        class: "link-secondary"
      },
      "Home"
    ),
    p(),
    a(
      {
        href: "#UserDetails",
        class: "link-primary"
      },
      "User Details"
    ),
    p(),
    a(
      {
        href: "#Boards",
        class: "link-primary"
      },
      "Boards"
    ),
    p(),
    // search board button and input
    form(
      {},
      (e) => {
        e.preventDefault()
      },
      div(
        label({ for: "searchBoard" }, "Board name:"),
        input({
          type: "boardName", id: "searchBoardInput",
          required: true
        })
      )
    ),
    searchButton,
    p(),
    h1("Best matchs for given query by <" + queryParams.q + ">:"),
    ...boardLinks,
    previousButton,
    nextButton
  )

}