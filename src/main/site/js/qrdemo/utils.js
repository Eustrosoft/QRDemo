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
    return userApi().me()
        .then(resp => {
            if (resp.status === 401)
                throw new Error('Login to system')
            return resp.json()
        })
        .then(json => {
            localStorage.setItem(LOCAL_STORAGE_USER, JSON.stringify(json))
            return json
        })
        .catch(processFetchErrorToLogin);
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
                 src="https://qrgen.qxyz.ru/generate?q=${code}&color=%23000000&background=%23ffffff&x=${x}&fileType=SVG&correctionLevel=M&site=${window.location.origin}/qrdemo/index.html"
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
                 src="https://qrgen.qxyz.ru/generate?q=${code}&color=%23000000&background=%23ffffff&x=300&fileType=SVG&correctionLevel=M&site=${window.location.origin}/qrdemo/index.html"
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
    const type = field?.type
    const placeholder = field?.placeholder
    const name = field?.name

    const htmlType = 
            type === 'TEXT' ? 'text' 
            : type === 'NUMBER' ? 'number' 
            : 'file'
    const editable = edit ? '' : 'readonly'
    const idField = notNullOrUndefined(id) ? `id="${id}"` : '';

    if ('file' === htmlType) {
        let fileName = fieldValue === null ? '' : '\t(File uploaded: ' + fieldValue + ')'
        let variabilityTab = edit
            ? `<input ${idField} name="${name}" value="${fieldValue}" ${editable} placeholder="${placeholder}" type="${htmlType}"/>`
            : `<div class="custom_button"><a target="_" href="${qrApi().getFileLink(qrId, name)}">Download File</a></div>`
        return `
            <label>${name}${fileName}</label><br/>
            ${variabilityTab}
        `
    }

    return `
        <label>${name}</label><br/>
        <input ${idField} name="${name}" value="${fieldValue}" ${editable} placeholder="${placeholder}" type="${htmlType}"/>
    `
}

export function fieldToEditFormRow(field, index) {
    const name = field.name
    const placeholder = field.placeholder
    const isStatic = field.isStatic
    const isPublic = field.isPublic
    const type = field.type
    const id = field.id

    let formField = document.createElement('div')
    formField.className = 'form_field'
    formField.innerHTML = `
            <label>Input Type</label> 
            <select name="type">
                 <option value="TEXT" ${getSelectedOrNot(type, 'TEXT')}>Text</option>
                  <option value="NUMBER" ${getSelectedOrNot(type, 'NUMBER')}>Number</option>
                  <option value="FILE" ${getSelectedOrNot(type, 'FILE')}>File</option>
            </select>
            
            <label>Name of Field</label> 
            <input name="name" type="text" value="${name}">
            
            <label>Placeholder</label> 
            <input name="placeholder" type="text" value="${placeholder}">
            
            <label>Is Static</label> 
            <input name="isStatic" type="checkbox" ${isStatic ? 'checked' : ''}>
            
            <label>Is Public</label> 
            <input name="isPublic" type="checkbox" ${isPublic ? 'checked' : ''}>
            
            <input name="id" type="hidden" value="${id}">
            
            <button class="big_button" id="delete_btn_${index}">Удалить</button>
    `
    return formField
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
