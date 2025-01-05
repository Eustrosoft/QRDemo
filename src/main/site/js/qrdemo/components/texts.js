export function getParagraph(text, className) {
    let p = document.createElement('p')
    p.innerHTML = text
    p.className = className
    return p
}

export function getSpan(text, className) {
    let span = document.createElement('span')
    span.innerHTML = text
    span.className = className
    return span
}