import { notify } from "../notifications.js"

export const ICONS = {
    WARNING: 'Papirus-Team-Papirus-Status-Dialog-warning.svg',
    LOCK: 'Papirus-Team-Papirus-Status-Changes-prevent.svg',
    ERROR: 'Papirus-Team-Papirus-Status-Dialog-error.svg',
    INFO: 'Papirus-Team-Papirus-Status-Dialog-information.svg',
    TRASH: 'Papirus-Team-Papirus-Status-User-trash-full.svg'
}

export function createHoverableIcon(iconName, tooltipText, target) {
    const wrapper = document.createElement('div')
    wrapper.className = 'hover-icon'

    const tooltip = document.createElement('div')
    tooltip.className = 'tooltip'
    tooltip.textContent = tooltipText

    fetch(`/timestamped_assets/icons/${iconName}`)
        .then(resp => {
            if (!resp.ok) {
                throw new Error('Icon file could not be fould')
            }
            return resp.text()
        })
        .then(svg => {
            const fixedSvg = normalizeSvgSize(svg)
            wrapper.innerHTML = fixedSvg
            wrapper.appendChild(tooltip)
            target.appendChild(wrapper)
        })
        .catch(ex => notify(ex))
}

export function normalizeSvgSize(svg) {
    const temp = document.createElement('div')
    temp.innerHTML = svg.trim()

    const svgEl = temp.querySelector('svg')

    if (!svgEl.getAttribute('viewBox')) {
        const width = svgEl.getAttribute('width') || '100'
        const height = svgEl.getAttribute('height') || '100'
        svgEl.setAttribute('viewBox', `0 0 ${width} ${height}`)
    }

    svgEl.removeAttribute('width')
    svgEl.removeAttribute('height')

    return temp.innerHTML
}
