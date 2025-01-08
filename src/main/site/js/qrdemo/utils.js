import {qrApi, userApi} from "./api.js";
import {LOCAL_STORAGE_USER} from "./localStorage.js";

export function setLkHeader() {
    setQueryParamAndRefresh("lk", "true")
}

export function setQrHeader(q) {
    setQueryParamAndRefresh("q", q)
}

export function addParamToHeader(name, value) {
    if ('URLSearchParams' in window) {
        let searchParams = new URLSearchParams(window.location.search)
        searchParams.set(name, value)
        window.location.search = searchParams.toString()
    }
}

export function toLoginIfNotAuthorized() {
    return userApi().authMe()
        .then(resp => {
            return resp.json()
        })
        .then(json => {
            localStorage.setItem(LOCAL_STORAGE_USER, JSON.stringify(json))
            return json
        })
}

export const processFetchErrorToLogin = (error) => {
    if (error !== undefined) {
        alert('Login to start using system!')
    }
    window.location.href = 'index.html?login=true'
}

export function getQRImage(code, x = 300) {
    return `
        <a href="?q=${code}">
            <img class="qr_line_image"
                 src="https://qrgen.qxyz.ru/generate?q=${code}&color=%23000000&background=%23ffffff&x=${x}&fileType=SVG&correctionLevel=L"
                 alt="qrImage"
            />
        </a>
    `
}

export function getQRImageDiv(code) {
    const link = document.createElement('a');
    link.href = `?q=${code}`
    link.innerHTML = `
            <img class="qr_line_image"
                 src="https://qrgen.qxyz.ru/generate?q=${code}&color=%23000000&background=%23ffffff&x=300&fileType=SVG&correctionLevel=L"
                 alt="qrImage"
            />
    `
    return link
}

export function setQueryParamsAndRefresh(map) {
    if (!map instanceof Array) {
        return
    }

    if ('URLSearchParams' in window) {
        let searchParams = new URLSearchParams(window.location.search)
        clearHeaderParams(searchParams)
        for (let i in map) {
            searchParams.set(map[i].name, map[i].value)
        }
        window.location.search = searchParams.toString()
    }
}

export function setQueryParamAndRefresh(key, value) {
    if ('URLSearchParams' in window) {
        let searchParams = new URLSearchParams(window.location.search)
        clearHeaderParams(searchParams)
        searchParams.set(key, value)
        window.location.search = searchParams.toString()
    }
}

export function clearHeaderParams(searchParams) {
    searchParams.entries().forEach(param => {
        searchParams.delete(param[0])
    })
}

export function fieldToHtml(field, fieldValue, edit = false, id = null, qrId = null) {
    const type = field?.fieldType
    const placeholder = field?.placeholder
    const name = field?.name

    const editable = edit ? '' : 'readonly'
    const idField = notNullOrUndefined(id) ? `id="${id}"` : '';

    return `
        <label>${name}</label><br/>
        <input ${idField} name="${name}" value="${fieldValue}" ${editable} placeholder="${placeholder}" type="${type}"/>
    `
}

export function fieldToEditFormRow(field, index, fieldTypes = []) {
    const name = field.name
    const placeholder = field.placeholder
    const isStatic = field.isStatic
    const isPublic = field.isPublic
    const type = field.fieldType
    const id = field.id

    let formField = document.createElement('div')
    let fieldTypesStr = fieldTypesToOptions(fieldTypes, type)

    formField.className = 'form_field'
    formField.innerHTML = `
            <label>Тип данных</label> 
            <select name="type">
                  ${fieldTypesStr}
            </select>
            
            <label>Имя поля</label> 
            <input name="name" type="text" value="${name}">
            
            <label>Плейсхолдер</label> 
            <input name="placeholder" type="text" value="${placeholder}">
            
            <label>Статическое</label> 
            <input name="isStatic" type="checkbox" ${isStatic ? 'checked' : ''}>
            
            <label>Публичное</label> 
            <input name="isPublic" type="checkbox" ${isPublic ? 'checked' : ''}>
            
            <input name="id" type="hidden" value="${id}">
            
            <button class="big_button" id="delete_btn_${index}"> X </button>
    `
    return formField
}

export function fieldToHtmlItems(field, index, fieldTypes = []) {
    const name = field.name
    const placeholder = field.placeholder
    const isStatic = field.isStatic
    const isPublic = field.isPublic
    const order = field.fieldOrder
    const type = field.fieldType
    const id = field.id

    let fieldTypesStr = fieldTypesToOptions(fieldTypes, type)

    let items = [
        `<input name="id" type="hidden" value="${id}" id="field_id_${id}">
         <select name="type">${fieldTypesStr}</select>`,
        `<input name="name" class="field_name" type="text" value="${name}">`,
        `<input name="placeholder" type="text" value="${placeholder}">`,
        `<input name="isStatic" type="checkbox" ${isStatic ? 'checked' : ''}></input>`,
        `<input name="isPublic" type="checkbox" ${isPublic ? 'checked' : ''}></input>`,
        `<input name="fieldOrder" type="number" value="${order}">`,
        `<button class="big_button" id="delete_btn_${index}">X</button>`
    ]
    return items
}

function fieldTypesToOptions(fieldTypes, type) {
    if (fieldTypes == null || fieldTypes == undefined) {
        return ''
    }

    return fieldTypes.map(ft => {
        return `<option value="${ft.value}" ${getSelectedOrNot(type, ft.value)}>${ft.description}</option>`
    }).join('')
}

function getSelectedOrNot(type, needToBe) {
    if (type === needToBe) {
        return 'selected'
    }
    return ''
}

export function longToHex(val) {
    if (val === null || val === undefined || val === '')
        return ''
    return Number(val).toString(16).toUpperCase();
}

export function emptyOrUndefined(value) {
    return value === null || value === undefined || value === ''
}

export const processFetchError = (error) => {
    alert(error)
}

export function notNullOrUndefined(value) {
    return value !== null && value !== undefined
}

export function hasAdminRole(roles) {
    if (roles === null) {
        return false
    }
    return roles.map(role => role.name).includes(USER_ROLES.ADMIN)
}

export const USER_ROLES = {
    ADMIN: "ROLE_ADMIN",
    USER: "ROLE_USER"
}

export function formatBytes(bytes, decimals = 2) {
    if (!+bytes) return '0 Bytes'

    const k = 1024
    const dm = decimals < 0 ? 0 : decimals
    const sizes = ['Bytes', 'KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB']

    const i = Math.floor(Math.log(bytes) / Math.log(k))

    return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`
}
