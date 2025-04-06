export function getStatusLine(statuses, currentStatus) {
    const statusLine = document.createElement('div');
    statusLine.className = 'status-line';

    statuses.forEach((status, index) => {
        const statusCircle = document.createElement('div');
        statusCircle.className = 'status-circle ' + (status === currentStatus ? 'completed' : 'waited');

        const tooltip = document.createElement('div')
        tooltip.className = 'tooltip'
        tooltip.textContent = status
        statusCircle.appendChild(tooltip)

        statusLine.appendChild(statusCircle);

        if (index < statuses.length - 1) {
            const connector = document.createElement('div');
            connector.className = 'connector';
            statusLine.appendChild(connector);
        }
    });

    return statusLine
}