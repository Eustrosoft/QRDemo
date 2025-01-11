
export function getBigButton(text, id = null) {
    let button = document.createElement('button')
    button.className = 'big_button'
    button.type = 'button'
    button.innerHTML = text
    if (id !== null) {
        button.id = id
    }
    return button
}

export function getCustomButton(text) {
    let button = document.createElement('button')
    button.className = 'custom_button'
    button.type = 'button'
    button.innerHTML = text
    return button
}
