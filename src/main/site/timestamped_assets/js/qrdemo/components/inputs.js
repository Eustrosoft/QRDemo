import { notEmptyOrUndefined } from "../../commons/common.js";

export function getSingleInput(type, required = false, id = '', placeholder = '', value = '', editable = true) {
    let htmlInput
    if (type === 'TEXTAREA') {
        htmlInput = getTextArea(null, required, id, placeholder, false, value)
        htmlInput.style.width = '100%'
    } else {
        htmlInput = document.createElement('input')
        htmlInput.type = type
    }
    htmlInput.placeholder = placeholder
    htmlInput.required = required
    htmlInput.id = id
    htmlInput.value = value
    htmlInput.editable = editable
    return htmlInput
}

export function getInput(labelText, type, required = false, id = '', placeholder = '', inline = false, autocomplete = 'on', value = '') {
    let htmlInputDiv = document.createElement('div');
    if (inline) {
        htmlInputDiv.style.display = 'flex'
    }

    if (notEmptyOrUndefined(labelText)) {
        let htmlLabel = document.createElement('label');
        let requiredStar = required ? ' *' : ''
        htmlLabel.innerText = labelText + requiredStar
        htmlInputDiv.appendChild(htmlLabel)
        if (!inline) {
            htmlInputDiv.appendChild(document.createElement('br'))
        }
    }
    let htmlInput = document.createElement('input')
    htmlInput.type = type
    htmlInput.placeholder = placeholder
    htmlInput.required = required
    htmlInput.id = id
    if (notEmptyOrUndefined(value)) {
        htmlInput.value = value
        if ('checkbox' === type) {
            htmlInput.checked = value
        }
    }
    if (notEmptyOrUndefined(autocomplete)) {
        htmlInput.autocomplete = autocomplete
    }

    htmlInputDiv.appendChild(htmlInput)

    return htmlInputDiv
}

export function getTextArea(labelText, required, id, placeholder, inline = false, value = '') {
    let htmlInputDiv = document.createElement('div');
    if (inline) {
        htmlInputDiv.style.display = 'flex'
    }

    let textarea = document.createElement('textarea')
    textarea.placeholder = placeholder
    textarea.required = required
    textarea.id = id
    textarea.rows = 4
    if (notEmptyOrUndefined(value)) {
        textarea.value = value
    }

    if (notEmptyOrUndefined(labelText)) {
        let htmlLabel = document.createElement('label');
        let requiredStar = required ? ' *' : ''
        htmlLabel.innerText = labelText + requiredStar
        htmlInputDiv.appendChild(htmlLabel)
        if (!inline) {
            htmlInputDiv.appendChild(document.createElement('br'))
        }
    } else {
        return textarea
    }
    htmlInputDiv.appendChild(textarea)
    return htmlInputDiv
}

export function getSwitch(checked = false, xSize, ySize) {
    let label = document.createElement('label')
    label.className = 'switch'
    let input = document.createElement('input')
    input.type = 'checkbox'
    input.checked = checked

    let span = document.createElement('span')
    span.className = 'slider round'
    if (xSize && ySize) {
        document.documentElement.style.setProperty('--slider-height', ySize / 2 + 'px')
        document.documentElement.style.setProperty('--slider-width', xSize / 2 + 'px')
        document.documentElement.style.setProperty('--switch-height', ySize + 'px')
        document.documentElement.style.setProperty('--switch-width', xSize + 'px')
    }

    label.append(input, span)
    return label
}

export function getSelect(labelText, id = '', values, value = null, required = false) {
    let htmlInputDiv = document.createElement('div');

    let htmlLabel = document.createElement('label');
    htmlLabel.innerText = labelText
    let htmlInput = document.createElement('select')
    htmlInput.required = required
    htmlInput.id = id

    for (let val in values) {
        let option = document.createElement('option')
        option.innerText = values[val]
        if (value !== null || value !== undefined) {
            if (values[val] === value) {
                option.selected = true
            }
        }
        htmlInput.appendChild(option)
    }

    htmlInputDiv.appendChild(htmlLabel)
    htmlInputDiv.appendChild(document.createElement('br'))
    htmlInputDiv.appendChild(htmlInput)
    htmlInputDiv.appendChild(document.createElement('br'))

    return htmlInputDiv
}
