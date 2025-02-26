export function getLink(text, href, className) {
    let link = document.createElement('a')
    link.innerText = text
    link.href = href
    link.className = className
    return link
}