import { notEmptyOrUndefined } from "../commons/common.js"
import { getHr } from "./components/hrs.js"

export function notify(message, title, duration = 5000) {
    let container = document.getElementById('notification-container')
    if (!container) {
        container = document.createElement('div')
        container.id = 'notification-container'
        document.body.appendChild(container)
    }

    const notification = document.createElement('div')
    notification.classList.add('notification')

    const textsDiv = document.createElement('div')
    if (notEmptyOrUndefined(title)) {
        const titleBlock = document.createElement('span')
        titleBlock.innerText = title
        textsDiv.appendChild(titleBlock)
        textsDiv.appendChild(getHr("1px"))
    }

    const spanMessage = document.createElement('span')
    spanMessage.innerText = message
    const closeBtn = document.createElement('button')
    closeBtn.classList.add('close-btn')
    closeBtn.innerHTML = '&times;'
    textsDiv.appendChild(spanMessage)
    notification.append(textsDiv, closeBtn)
    container.appendChild(notification)
    setTimeout(() => notification.classList.add('show'), 50);
    
    notification.querySelector('.close-btn').addEventListener(
        'click',  
        () => removeNotification(notification)
    )

    setTimeout(() => removeNotification(notification), duration)
}

export function removeNotification(notification) {
    notification.classList.add('hide')
    setTimeout(() => notification.remove(), 300)
}