
export function getBigButton(text) {
    let button = document.createElement('button')
    button.className = 'big_button'
    button.type = 'button'
    button.innerHTML = text
    return button
}
