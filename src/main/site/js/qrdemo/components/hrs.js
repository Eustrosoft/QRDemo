export function getHr(margin, padding, className) {
    let hr = document.createElement('hr')
    hr.style.margin = margin
    hr.style.padding = padding
    hr.className = className
    return hr
}