export const REGISTRATION_STATUSES = {
    PENDING: 'Ожидание',
    IN_WORK: 'В работе',
    ACCEPTED: 'Заявка принята',
    REJECTED: 'Заявка отклонена'

}

// Format statuses: {STATUS: 'Translated Status'}
export function getStatusLine(statuses, currentStatus) {
    const statusLine = document.createElement('div')
    statusLine.className = 'status-line'

    const entries = Object.entries(statuses)
    let index = 0
    for (const [status, text] of entries) {
        const statusCircle = document.createElement('div')
        statusCircle.className = 'status-circle ' + (status === currentStatus ? 'completed' : 'waited')

        const tooltip = document.createElement('div')
        tooltip.className = 'tooltip'
        tooltip.textContent = text
        statusCircle.appendChild(tooltip)

        statusLine.appendChild(statusCircle)

        if (index < entries.length - 1) {
            const connector = document.createElement('div')
            connector.className = 'connector'
            statusLine.appendChild(connector)
        }
        index++
    }

    return statusLine
}