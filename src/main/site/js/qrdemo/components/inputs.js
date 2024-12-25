import { notEmptyOrUndefined } from "../../commons/common";

export function getInput(labelText, type, required = false, id = '', placeholder = '', readonly = false) {
    let htmlInputDiv = document.createElement('div');

    if (notEmptyOrUndefined(labelText)) {
        let htmlLabel = document.createElement('label');
        htmlLabel.innerHTML = labelText
        htmlInputDiv.appendChild(htmlLabel)
        htmlInputDiv.appendChild(document.createElement('br'))
    }
    let htmlInput = document.createElement('input')
    htmlInput.type = type
    htmlInput.placeholder = placeholder
    htmlInput.required = required
    htmlInput.id = id

    htmlInputDiv.appendChild(htmlInput)

    return htmlInputDiv
}

export function getSelect(labelText, id = '', values, value = null, required = false) {
    let htmlInputDiv = document.createElement('div');

    let htmlLabel = document.createElement('label');
    htmlLabel.innerHTML = labelText
    let htmlInput = document.createElement('select')
    htmlInput.required = required
    htmlInput.id = id

    for (let val in values) {
        let option = document.createElement('option')
        option.innerHTML = values[val]
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
