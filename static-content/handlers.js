import { div, h1, ul, li, a, p, input, label, form, button, select, option } from "./DSL.js";
import { cardDetailsView } from "./views/cardDetailsView.js";
import cardServices from "./services/card.services.js"
import listServices from "./services/list.services.js";
import { listDetailsView } from "./views/listDetailsView.js";
import boardServices from "./services/board.services.js";
import { boardsView } from "./views/boardsView.js";
import { boardDetailsView } from "./views/boardDetailsView.js";
import { loginView } from "./views/loginView.js";
import { registerView } from "./views/registerView.js";
import { searchView } from "./views/searchView.js";
import authServices from "./services/auth.services.js";
import { API_BASE_URL } from "./router.js";

const LIMIT_PAGING = 5
const INITIAL_SKIP_VALUE = 0

export let isUserLoggedIn = false
export function changeLoginState(value) { isUserLoggedIn = value; }

export function setError(message) {
    const error = document.getElementById("error")
    error.replaceChildren(
        div(
            p(message)
        )
    )
}

function authRefs() {
    if (isUserLoggedIn) {
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
                    href: "#UserDetails",
                    class: "link-primary"
                },
                "User Details"
            ),
            p(),
            a({
                href: "#logout",
                class: "link-primary"
            }, 'Logout')
        )
    } else {
        return div(
            a(
                {
                    href: "#login",
                    class: "link-secondary"
                },
                "Login"),
            p(),
            a(
                {
                    href: "#signup",
                    class: "link-secondary"
                },
                "Register"
            ),
        )
    }
}

function getUserHome(mainContent) {
    mainContent.replaceChildren(
        div(
            authRefs(),
            h1("Home")
        )
    )
}

async function getLogin(mainContent) {
    mainContent.replaceChildren(
        loginView(await authServices.login)
    )
}

async function getRegister(mainContent) {
    mainContent.replaceChildren(
        registerView(await authServices.register)
    )
}

async function handleLogout() {
    await authServices.logout()
}

function getUserDetails(mainContent) {
    return fetch(API_BASE_URL + "/userDetails", {})
        .then(res => res.json())
        .then(user => {
            mainContent.replaceChildren(
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
                        href: "#Boards",
                        class: "link-primary"
                    },
                    "Boards"
                ),
                p(),
                h1("User Details"),
                ul(
                    li("Number : " + user.number),
                    li("Name : " + user.name),
                    li("Email : " + user.email)
                )
            )
        })
}


function getBoardUsers(mainContent, pathVariables) {
    let identifier = pathVariables.id
    return fetch(API_BASE_URL + "/boards/" + identifier + "/users", {})
        .then(res => res.json())
        .then(obj => {
            const userLinks = obj.users.map(username => {
                return p(username)
            });

            mainContent.replaceChildren(
                div(
                    a({
                        href: "#BoardDetails/" + identifier,
                        class: "link-primary"
                    }, identifier),
                    p(),
                    h1("Board Users"),
                    ...userLinks
                )
            )
        })
}

async function getBoards(mainContent, pathVariables, queryParams) {

    if (queryParams === null) {
        queryParams = {
            skip: 0,
            limit: 5
        };
    } else {
        queryParams.limit = 5
    }

    const boards = await boardServices.boards(queryParams.skip, queryParams.limit)

    mainContent.replaceChildren(
        boardsView(boards, queryParams, await boardServices.newBoard)
    )
}

async function getBoardDetails(mainContent, pathVariables) {
    let identifier = pathVariables.id

    const board = await boardServices.getBoard(identifier)

    const boardLists = await boardServices.boardLists(identifier)

    mainContent.replaceChildren(
        boardDetailsView(board, boardLists, await listServices.newList, await listServices.deleteList)
    )
}

async function getListDetails(mainContent, pathVariables) {
    let identifier = pathVariables.id
    const list = await listServices.listDetails(identifier)

    const listCards = await listServices.listCards(identifier)

    mainContent.replaceChildren(
        listDetailsView(list, listCards, await cardServices.newCard)
    )
}

async function getCardDetails(mainContent, pathVariables) {
    let identifier = pathVariables.id
    const card = await cardServices.cardDetails(identifier)

    const list = await listServices.listDetails(card.list)

    const availableLists = await boardServices.boardLists(list.board)

    mainContent.replaceChildren(
        cardDetailsView(card, list, availableLists, await cardServices.moveCard)
    )
}

async function searchBoard(mainContent, pathVariables, queryParams) {

    if (queryParams === null) {
        queryParams = {
            q: 'board',
            skip: 0,
            limit: 5
        };
    } else {
        queryParams.limit = 5
    }

    const boards = await boardServices.searchBoard(queryParams)

    mainContent.replaceChildren(
        searchView(boards, queryParams)
    )
}



export const handlers = {
    getUserHome,
    getLogin,
    getRegister,
    handleLogout,
    getUserDetails,
    getBoards,
    getBoardDetails,
    getBoardUsers,
    getListDetails,
    getCardDetails,
    searchBoard
}

export default handlers