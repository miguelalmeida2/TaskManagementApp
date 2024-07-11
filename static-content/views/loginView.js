import {a, button, div, form, h1, input, label, p} from "../DSL.js";

export function loginView(handleLogin) {
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
                href: "#signup",
                class: "link-secondary"
            },
            "Register"
        ),
        p(),
        h1("Login"),
        form(
            {},
            (e) => {
                e.preventDefault();
                const email = document.querySelector("#loginEmail").value;
                const pass = document.querySelector("#loginPassword").value;
                handleLogin(email, pass)
            },
            div(
                label({ for: "email" }, "Email: "),
                input({
                    type: "email", id: "loginEmail",
                    required: true
                }),
                label({ for: "password" }, "Password: "),
                input({
                    type: "password", id: "loginPassword",
                    required: true
                }),
                p(),
                button({ type: "submit" }, "Login")
            )
        )
    )
}