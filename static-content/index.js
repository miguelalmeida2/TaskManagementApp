import router from "./router.js";
import handlers, { setError } from "./handlers.js";
import authServices from "./services/auth.services.js";

window.addEventListener('load', loadHandler)
window.addEventListener('hashchange', hashChangeHandler)
window.addEventListener('beforeunload', logout)

function loadHandler() {

    router.addRouteHandler("UserHome", handlers.getUserHome)
    router.addRouteHandler("UserDetails", handlers.getUserDetails)
    router.addRouteHandler("Search/Board", handlers.searchBoard)
    router.addRouteHandler("Boards", handlers.getBoards)
    router.addRouteHandler("BoardDetails/:id", handlers.getBoardDetails)
    router.addRouteHandler("BoardUsers/:id", handlers.getBoardUsers)
    router.addRouteHandler("ListDetails/:id", handlers.getListDetails)
    router.addRouteHandler("CardDetails/:id", handlers.getCardDetails)
    router.addRouteHandler("login", handlers.getLogin)
    router.addRouteHandler("signup", handlers.getRegister)
    router.addRouteHandler("logout", handlers.handleLogout)

    router.addDefaultNotFoundRouteHandler(() => window.location.hash = "UserHome")

    hashChangeHandler()
}

function hashChangeHandler() {
    setError("") // clear any previous errors
    const mainContent = document.getElementById("mainContent")
    let path = window.location.hash.replace("#", "")
    const urlPath = path.split('?')
    const splitPath = urlPath[0].split('/')
    let filteredAvailableRoutes = null

    let handler
    let queryParams = {}
    let pathVariables = {}

    // Path com size 1
    if (splitPath.length < 2) {
        if (urlPath[1] !== undefined && urlPath[1] != null) {
            queryParams = parseQuery(urlPath[1])
        }
        handler = router.getRouteHandler(splitPath[0])
    } else {
        // Path com size >1

        // Colocar cada parte das rotas registadas no router num array de arrays de strings
        // :boardid/:listid/:cardid -> [":boardid", ":listid", ":cardid"]
        filteredAvailableRoutes = router.getRoutes().map((route) => {
            return route.split("/")
        })

        let foundHandlerPath = []
        for (let i = 0; i < splitPath.length; i++) {

            if (filteredAvailableRoutes.length === 0) {
                handler = router.getRouteHandler()
                break;
            }

            // /:boardid
            let pathPart = splitPath[i]

            // filtrar o array de array por aqueles que têm no indice i um elemento igual à parte da rota
            filteredAvailableRoutes = filteredAvailableRoutes.filter((route) => {

                // se nao tem pathVal
                if (route[i] === pathPart) {
                    return true
                }

                // se tem pathVal
                if (route[i].includes(":")) {
                    let pathValName = route[i].replace(":", "")
                    if (pathVariables[pathValName] === undefined) pathVariables[pathValName] = pathPart;
                    else if (pathVariables[pathValName] instanceof Array) pathVariables[pathValName].push(pathPart);
                    else pathVariables[pathValName] = [object[pathValName], pathPart];
                    return true
                }

                // se nao tem pathVal nem é igual ao path
                return false
            })
            if (filteredAvailableRoutes.length >= 1) {
                //se sim, meto essa parte no que vai ser a rota
                foundHandlerPath.push(pathPart)
            }

        }
        foundHandlerPath = filteredAvailableRoutes[0].join("/")
        if (urlPath[1] !== undefined && urlPath[1] != null) {
            queryParams = parseQuery(urlPath[1])
        }
        handler = router.getRouteHandler(foundHandlerPath)
    }

    if (Object.getOwnPropertyNames(queryParams).length === 0 || Object.keys(queryParams).length === 0) {
        queryParams = null
    }

    handler(mainContent, pathVariables, queryParams)
}

function parseQuery(query) {
    const object = {};
    if (query.indexOf('?') !== -1) {
        query = query.split('?');
        query = query[1];
    }
    let parseQuery = query.split("&");
    for (const element of parseQuery) {
        let pair = element.split('=');
        let key = decodeURIComponent(pair[0]);
        if (key.length === 0) continue;
        let value = decodeURIComponent(pair[1].replace("+", " "));
        if (object[key] === undefined) object[key] = value;
        else if (object[key] instanceof Array) object[key].push(value);
        else object[key] = [object[key], value];
    }
    return object;

}

async function logout() {
    await authServices.logout()
}