export function getTextLabel(text, className) {
    let label = document.createElement('label')
    label.className = className
    label.innerHTML = text
    return label
}

export function get2TextLabels(text, secondText, className) {
    let labelsDiv = document.createElement('div')

    let label = document.createElement('label')
    label.className = className
    label.innerHTML = text

    let label2 = document.createElement('label')
    label2.className = className
    label2.innerHTML = secondText

    labelsDiv.appendChild(label)
    labelsDiv.appendChild(label2)
    return labelsDiv
}
