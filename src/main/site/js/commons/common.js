
// Utils functions
export function notNullOrUndefined(value) {
    return value !== null && value !== undefined
}

export const processFetchError = (error) => {
    alert(error)
}

export function rolesToList(roles) {
    const rolesList = []
    for (let i = 0; i < roles.length; i++) {
        rolesList.push(roles[i].name)
    }
    return rolesList
}

export function describeRole(roleRaw) {
    switch (roleRaw) {
        case USER_ROLES.ADMIN:
            return 'Admin'
        case USER_ROLES.USER:
            return 'User'
        default:
            return 'Unknown'
    }
}

export function getCurrentHeaders() {
    let headers = null
    const req = new XMLHttpRequest();
    req.open('GET', document.location, true);
    req.send(null);
    req.onload = function () {
        headers = req.getAllResponseHeaders().toLowerCase()
    };
    return headers
}

export function str2html(str) {
    return str.replace(/(?:\r\n|\r|\n)/g, '<br>')
}

export function checkElementsNotNull(elements) {
    return !!elements;
}

export function notEmptyOrUndefined(value) {
    return !emptyOrUndefined(value);
}

export function emptyOrUndefined(value) {
    return value === null || value === undefined || value === ''
}

export function clearHeaderParams(searchParams) {
    searchParams.entries().forEach(param => {
        searchParams.delete(param[0])
    })
}

export function setQueryParamAndRefresh(key, value) {
    if ('URLSearchParams' in window) {
        let searchParams = new URLSearchParams(window.location.search)
        clearHeaderParams(searchParams)
        searchParams.set(key, value)
        window.location.search = searchParams.toString()
    }
}

export function getMeFromLS() {
    let me = localStorage.getItem(LOCAL_STORAGE_USER)
    if (notNullOrUndefined(me)) {
        return JSON.parse(me)
    }
    throw new Error('Has no user in LS')
}

// Structures
export const USER_ROLES = {
    ADMIN: "ROLE_ADMIN",
    USER: "ROLE_USER",
    SALESMAN: "ROLE_SALESMAN"
}
