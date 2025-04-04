export function getLink(text, href, className, target = '_blank') {
    let link = document.createElement('a')
    link.innerText = text
    link.href = href
    link.className = className
    link.target = target
    return link
}

export function getLinkWithAction(text, href, className, action, ...actionArgs) {
    let link = document.createElement('a')
    link.innerText = text
    link.href = href
    link.className = className

    link.addEventListener('click', (e) => { 
        e.preventDefault()
        action(...actionArgs) 
    })

    return link
}
