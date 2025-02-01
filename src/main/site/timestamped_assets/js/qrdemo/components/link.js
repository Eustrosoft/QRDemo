export function getLink(text, href, className) {
    let link = document.createElement('a')
    link.innerHTML = text
    link.href = href
    link.className = className
    return link
}