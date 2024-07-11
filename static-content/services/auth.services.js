import {API_BASE_URL} from "../router.js";
import {changeLoginState, setError} from "../handlers.js";

class AuthServices {

    async login(email, pass) {
        try {
            const res = await fetch(API_BASE_URL + "/login", {
                method: "POST",
                body: JSON.stringify({
                    email: email,
                    password: pass
                })
            })

            if (res.ok){
                changeLoginState(true)
                window.location.hash = `Boards`
            } else
                throw new Error(`Login failed with status ${res.status}`);
        } catch (error) {
            console.log(error)
            changeLoginState(false)
            setError(error.message)
        }
    }

    async logout() {
        try {
            const res = await fetch(API_BASE_URL + "/logout", {
                method: "POST"
            })
            if (res.ok) {
                changeLoginState(false)
                window.location.hash = `UserHome`
            }
        } catch (error) {
            console.log(error)
            setError(error.message)
        }
    }

    async register(name, email, pass) {
        try {
            const res = await fetch(API_BASE_URL + "/signup", {
                method: "POST",
                body: JSON.stringify({
                    name: name,
                    email: email,
                    password: pass
                })
            })

            await res.json()
            if (res.status === 201)
                window.location.hash = `login`
        } catch (error) {
            console.log(error)
            setError(error.message)
        }
    }
}

export default new AuthServices();