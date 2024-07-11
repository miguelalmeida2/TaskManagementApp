import {isUserLoggedIn} from "./handlers.js";

export const API_BASE_URL = "http://localhost:9000"
//export const API_BASE_URL = "https://service-ls-2223-2-41n-g08-b0x4.onrender.com"

const routes = []
let notFoundRouteHandler = () => { throw Error("Route handler for unknown routes not defined") }

function addRouteHandler(path, handler) {
    routes.push({ path, handler })
}

function addDefaultNotFoundRouteHandler(notFoundRH) {
    notFoundRouteHandler = notFoundRH
}

function getRouteHandler(path) {
    const route = routes.find(r => r.path === path)

    if (route === undefined ||
        ((route.path === "login" || route.path === "signup") && isUserLoggedIn) ||
        (route.path.includes("logout") && !isUserLoggedIn)
    )
        return notFoundRouteHandler
    else
        return route.handler
}

function getRoutes() {
    return routes.map((route) => {
        return route.path
    })
}

const router = {
    addRouteHandler,
    getRouteHandler,
    getRoutes,
    addDefaultNotFoundRouteHandler
}

export default router