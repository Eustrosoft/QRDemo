import { notEmptyOrUndefined, notNullOrUndefined } from "../../commons/common.js"

export const DOWNRAISING_INDEX = 'downraisingIndex'
export const UPRAISING_INDEX = 'upraisingIndex'

export function getTable(
    headItems, items, itemRowClass = null, id = '', 
    tableClass = null, rowClickCallback, isItemsDom = false, domItemsIndexes = []
) {
    let table = document.createElement('table')
    if (id != '') {
        table.id = id
    }
    if (notNullOrUndefined(tableClass)) {
        table.className = tableClass
    }

    if (notEmptyOrUndefined(headItems) && headItems.length !== 0) {
        let tr = document.createElement('tr')
        for (let hi in headItems) {
            let th = document.createElement('th')
            th.innerText = headItems[hi].name
            th.style = `width: ${headItems[hi].width}`
            tr.appendChild(th)
        }
        table.appendChild(tr)
    }

    for (let item in items) {
        let tr = document.createElement('tr')
        if (notNullOrUndefined(itemRowClass)) {
            tr.className = itemRowClass + ' black_border'
        } else {
            tr.className = 'black_border'
        }
        for (let rowItem in items[item]) {
            let td = document.createElement('td')
            if (isItemsDom) {
                td.innerHTML = items[item][rowItem]
            } else {
                if (domItemsIndexes != null && domItemsIndexes.includes(rowItem)) {
                    td.append(items[item][rowItem])
                } else {
                    td.innerText = items[item][rowItem]
                }
            }
            tr.appendChild(td)
        }
        if (rowClickCallback != null) {
            tr.addEventListener('click', rowClickCallback)
        }
        table.appendChild(tr)
    }

    return table
}

export function getComplexTable(
    headItems, items, 
    itemRowClass = null, tableId = '', tableClass = null, trKey = null, 
    rowClickCallback = null, hasActionsItem = false) {
    let table = document.createElement('table')
    if (tableId != '') {
        table.id = tableId
    }
    if (notNullOrUndefined(tableClass)) {
        table.className = tableClass
    }

    if (notEmptyOrUndefined(headItems) && headItems.length !== 0) {
        let tr = document.createElement('tr')
        for (let hi in headItems) {
            let th = document.createElement('th')
            th.innerText = headItems[hi].name
            th.style = `width: ${headItems[hi].width}`
            tr.appendChild(th)
        }
        if (hasActionsItem) {
            let th = document.createElement('th')
            th.innerText = 'Действия'
            th.style = `width: 8%`
            tr.appendChild(th)
        }
        table.appendChild(tr)
    }

    for (let i = 0, j = items?.length; i < items?.length; i++, j--) {
        let tr = document.createElement('tr')
        let item = items[i]
        if (trKey != null) {
            tr.setAttribute('key', item?.[trKey])
        }

        if (notNullOrUndefined(itemRowClass)) {
            tr.className = itemRowClass + ' black_border'
        } else {
            tr.className = 'black_border'
        }

        for (let k = 0; k < headItems?.length; k++) {
            let headItemKey = headItems[k]?.key
            let td = document.createElement('td')

            if (headItemKey === DOWNRAISING_INDEX) {
                td.innerText = j
            } else if (headItemKey === UPRAISING_INDEX) {
                td.innerText = k
            } else {
                if (notNullOrUndefined(headItems[k]?.itemCallback)) {
                    td.innerText = headItems[k]?.itemCallback(item?.[headItemKey])
                } else {
                    td.innerText = item?.[headItemKey]
                }
            }
            tr.appendChild(td)
        }
        if (hasActionsItem && notEmptyOrUndefined(item['actions'])) {
            tr.appendChild(item['actions'])
        }
        if (rowClickCallback != null) {
            tr.addEventListener('click', rowClickCallback)
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
    constructor(name, width, key, itemCallback) {
        this.name = name
        this.width = width
        this.key = key
        this.itemCallback = itemCallback
    }
}

export class ActionColumn {
    constructor(buttons) {
        this.buttons = buttons
    }

    getButtons() {
        return this.buttons
    }
}
