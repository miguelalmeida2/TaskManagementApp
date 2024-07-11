/*
This example creates the students views using directly the DOM Api
But you can create the views in a different way, for example, for the student details you can:
    createElement("ul",
        createElement("li", "Name : " + student.name),
        createElement("li", "Number : " + student.number)
    )
or
    ul(
        li("Name : " + student.name),
        li("Number : " + student.name)
    )
Note: You have to use the DOM Api, but not directly
*/

function createElement(tagName, attributes, ...children) {
    const element = document.createElement(tagName);

    if (attributes) {
        for (const [key, value] of Object.entries(attributes)) {
            element.setAttribute(key, value);
        }
    }

    children.forEach(child => {
        if (typeof child === "string") {
            element.appendChild(document.createTextNode(child));
        } else if (child instanceof HTMLElement) {
            element.appendChild(child);
        } else {
            throw new Error("Child element must be either a string or an HTMLElement.");
        }
    });
    return element;
}

export function div(...children){
    return createElement("div", null, ...children);
}

export function ul(...children){
    return createElement("ul", null, ...children);
}

export function li(...children){
    return createElement("li", null, ...children);
}

export function h1(...children){
    return createElement("h1", null, ...children);
}

export function p(...children){
    return createElement("p", null, ...children);
}

export function a(attributes, ...children) {
    return createElement("a", attributes, ...children);
}

export function form(attributes, eventListener, ...children) {
    const element = createElement("form", attributes, ...children);
    if (eventListener) {
        element.addEventListener("submit", eventListener);
    }
    return element;
}

export function label(attributes, ...children) {
    return createElement("label", attributes, ...children);
}

export function button(attributes, ...children) {
    return createElement("button", attributes, ...children);
}

export function input(attributes, ...children) {
    return createElement("input", attributes, ...children);
}

export function select(attributes, ...children) {
    return createElement("select", attributes, ...children);
}

export function option(attributes, ...children) {
    return createElement("option", attributes, ...children);
}
