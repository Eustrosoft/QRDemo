import { notNullOrUndefined } from "../../commons/common.js"

export function getTable(headItems, items, itemRowClass = null, id = '') {
    let table = document.createElement('table')
    if (id != '') {
        table.id = id
    }

    let tr = document.createElement('tr')
    for (let hi in headItems) {
        let th = document.createElement('th')
        th.innerHTML = headItems[hi].name
        th.style = `width: ${headItems[hi].width}`
        tr.appendChild(th)
    }
    table.appendChild(tr)

    for (let item in items) {
        let tr = document.createElement('tr')
        if (notNullOrUndefined(itemRowClass)) {
            tr.className = itemRowClass + ' black_border'
        } else {
            tr.className = 'black_border'
        }
        for (let rowItem in items[item]) {
            let td = document.createElement('td')
            td.innerHTML = items[item][rowItem]
            tr.appendChild(td)
        }
        table.appendChild(tr)
    }

    return table
}

export function getTr(element, colSpan) {
    let tr = document.createElement('tr')
    let td = document.createElement('td')
    td.colSpan = colSpan
    tr.appendChild(td)
    td.appendChild(element)
    return tr
}

export class TableHead {
    constructor(name, width) {
        this.name = name
        this.width = width
    }
}