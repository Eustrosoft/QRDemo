export function getTextLabel(text, className) {
    let label = document.createElement('label')
    label.className = className
    label.innerText = text
    return label
}

export function get2TextLabels(text, secondText, className) {
    let labelsDiv = document.createElement('div')

    let label = document.createElement('label')
    label.className = className
    label.innerText = text

    let label2 = document.createElement('label')
    label2.className = className
    label2.innerText = secondText

    labelsDiv.appendChild(label)
    labelsDiv.appendChild(label2)
    return labelsDiv
}
