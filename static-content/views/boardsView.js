import { div, h1, ul, li, a, p, input, label, form, button } from "../DSL.js";


export function boardsView(boardsObj, queryParams, handleNewBoardSubmit) {

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

  const boardLinks = boardsObj.boards.map(boardName => {
    return div(
      a({
        href: "#BoardDetails/" + boardName,
        class: "link-primary"
      }, boardName),
      p()
    )
  });

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
  previousButton.addEventListener("click", () => { window.location.hash = "Boards?skip=" + previousPage })
  nextButton.addEventListener("click", () => { window.location.hash = "Boards?skip=" + nextPage })

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
    h1("Create a new board"),
    form(
      {},
      (e) => {
          e.preventDefault()
          handleNewBoardSubmit()
      },
      div(
        label({ for: "boardName" }, "Board name:"),
        input({
          type: "boardName", id: "boardName",
          required: true
        }),
        label({ for: "boardDescription" }, "Description:"),
        input({
          type: "boardDescription", id: "boardDescription",
          required: true
        }),
        button({ type: "submit", value: "Create Board" }, "Create Board")
      )
    ),
    h1("Available Boards"),
    ...boardLinks,
    previousButton,
    nextButton
  )

}