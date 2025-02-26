import { notEmptyOrUndefined } from "../../commons/common.js"

export function getBigButton(text, id = null) {
    let button = document.createElement('button')
    button.className = 'big_button'
    button.type = 'button'
    button.innerText = text
    if (id !== null) {
        button.id = id
    }
    return button
}

export function getCustomButton(text) {
    let button = document.createElement('button')
    button.className = 'custom_button'
    button.type = 'button'
    button.innerText = text
    return button
}

export function getIconBtn(text, src) {
    let button = document.createElement('button')
    button.className = 'icon-btn'
    button.type = 'button'

    let icon = document.createElement('img')
    icon.className = 'i-icon'
    if (notEmptyOrUndefined(src)) {
        icon.src = src
    }
    if (notEmptyOrUndefined(text)) {
        button.innerText = text
    }
    button.appendChild(icon)
    return button
}
