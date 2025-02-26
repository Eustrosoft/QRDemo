export function getParagraph(text, className) {
    let p = document.createElement('p')
    p.innerText = text
    p.className = className
    return p
}

export function getSpan(text, className) {
    let span = document.createElement('span')
    span.innerText = text
    span.className = className
    return span
}

export const MAIN_TEXT = ``