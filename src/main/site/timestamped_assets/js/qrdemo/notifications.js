export function notify(message, duration = 5000) {
    let container = document.getElementById('notification-container')
    if (!container) {
        container = document.createElement('div')
        container.id = 'notification-container'
        document.body.appendChild(container)
    }

    const notification = document.createElement('div')
    notification.classList.add('notification')
    notification.innerHTML = `
        <span>${message}</span>
        <button class='close-btn'>&times;</button>
    `
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