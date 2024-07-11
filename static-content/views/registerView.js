import {a, button, div, form, h1, input, label, p} from "../DSL.js";

export function registerView(handleRegister) {
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
                href: "#login",
                class: "link-secondary"
            },
            "Login"
        ),
        p(),
        h1("Register"),
        form(
            {},
            (e) => {
                e.preventDefault();
                const name = document.querySelector("#regName").value;
                const email = document.querySelector("#regEmail").value;
                const pass = document.querySelector("#regPassword").value;
                const repPass = document.querySelector("#repRegPassword").value;
                if (pass === repPass)
                    handleRegister(name, email, pass)
                else
                    alert("Passwords do not match")
            },
            div(
                label({ for: "regName" }, "Name:"),
                input({
                    type: "text", id: "regName",
                    required: true
                }),
                p(),
                label({ for: "email" }, "Email:"),
                input({
                    type: "email", id: "regEmail",
                    required: true
                }),
                p(),
                label({ for: "password" }, "Password:"),
                input({
                    type: "password", id: "regPassword",
                    required: true
                }),
                p(),
                label({ for: "repRegPassword" }, "Repeat password:"),
                input({
                    type: "password", id: "repRegPassword",
                    required: true
                }),
                p(),
                button({ type: "submit" }, "Register")
            )
        )
    )
}