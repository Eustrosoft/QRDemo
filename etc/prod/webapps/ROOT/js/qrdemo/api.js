import { notEmptyOrUndefined, processFetchError } from "../commons/common.js";
import { DICTIONARIES, DICTIONARIES_NAMES } from "./domain/dictionaries.js";
import { notify } from "./notifications.js";
import { emptyOrUndefined, processFetchErrorToLogin } from "./utils.js";

export const QR_DEMO_API_DEV = `${window.location.protocol}//${window.location.hostname}:9983/qr/v1/api/`
export const QR_DEMO_API = `${window.location.protocol}//${window.location.hostname}/qrCodeDemo/v1/api/`
export const QR_PRINTER_URL = `/printer/`

export function qrApi() {
    const headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    }
    const authFetch = RequestDecorators.withAuth(fetch)
    return {
        getAppVersion: () => {
            let url = `${QR_DEMO_API}unsecured/alive/version`;

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return fetch(req)
        },
        saveForm: (form) => {
            let url = `${QR_DEMO_API}secured/forms`;

            const req = new Request(
                url,
                {
                    method: 'POST',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify(form)
                }
            )
            return authFetch(req)
        },
        saveDefaultForm: () => {
            let url = `${QR_DEMO_API}secured/forms/default`;

            const req = new Request(
                url,
                {
                    method: 'POST',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        updateForm: (form) => {
            let url = `${QR_DEMO_API}secured/forms/${form.id}`;

            const req = new Request(
                url,
                {
                    method: 'PUT',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify(form)
                }
            )
            return authFetch(req)
        },
        getAllForms: () => {
            let url = `${QR_DEMO_API}secured/forms`;

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        getAllFormFields: () => {
            let url = `${QR_DEMO_API}secured/forms/fields`;

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        getFormById: (id) => {
            let url = `${QR_DEMO_API}secured/forms/${id}`;

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        deleteForm: (id) => {
            let url = `${QR_DEMO_API}secured/forms/${id}`;

            const req = new Request(
                url,
                {
                    method: 'DELETE',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        getQrs: (rangeId) => {
            let url = `${QR_DEMO_API}secured/qrs`;
            if (notEmptyOrUndefined(rangeId)) {
                url += `?rangeId=${rangeId}`
            }

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        getQr: (q) => {
            let url = `${QR_DEMO_API}secured/qrs/code?q=${q}`

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        getQrPublic: (q) => {
            let url = `${QR_DEMO_API}unsecured/qrs?q=${q}`;

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return fetch(req)
        },
        saveQr: (data) => {
            let url = `${QR_DEMO_API}secured/qrs`

            const req = new Request(
                url,
                {
                    method: 'PUT',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify(data)
                }
            )
            return authFetch(req)
        },
        uploadFormFile: (id, fileRequest, userSettings) => {
            const fileUploadUrl = `${QR_DEMO_API}secured/files/upload/blob`;
            uploadFileByBytes(fileUploadUrl, fileRequest, userSettings,
                (fileId) => {
                    qrApi().connectFileToForm(id, fileId)
                        .then(r => alert('Файл был добавлен'))
                        .then(e => window.location.reload())
                })
        },
        uploadQRFile: (id, fileRequest, userSettings) => {
            const fileUploadUrl = `${QR_DEMO_API}secured/files/upload/blob`;
            uploadFileByBytes(fileUploadUrl, fileRequest, userSettings,
                (fileId) => {
                    qrApi().connectFileToQR(id, fileId)
                        .then(r => alert('Файл был добавлен'))
                        .then(e => window.location.reload())
                })
        },
        // getDownloadAllQRPublicFilesLink: (id) => {
        //     return `${QR_DEMO_API}unsecured/qrs/files/all/download?q=${id}`
        // },
        connectFileToQR: (id, fileId) => {
            let url = `${QR_DEMO_API}secured/qrs/${id}/files/choose`;

            const req = new Request(
                url,
                {
                    method: 'PUT',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify({ id: fileId })
                }
            )
            return authFetch(req)
        },
        connectFileToForm: (id, fileId) => {
            let url = `${QR_DEMO_API}secured/forms/${id}/files/choose`

            const req = new Request(
                url,
                {
                    method: 'PUT',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify({ id: fileId })
                }
            )
            return authFetch(req)
        },
        getRanges: () => {
            let url = `${QR_DEMO_API}secured/ranges`

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        createQR: (json) => {
            let url = `${QR_DEMO_API}secured/qrs`

            const req = new Request(
                url,
                {
                    method: 'POST',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify(json)
                }
            )
            return authFetch(req)
        },
        getAllFiles: () => {
            let url = `${QR_DEMO_API}secured/files`;

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        getById: (id) => {
            let url = `${QR_DEMO_API}secured/files/${id}`;

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        deleteFile: (id) => {
            let url = `${QR_DEMO_API}secured/files/${id}`;

            const req = new Request(
                url,
                {
                    method: 'DELETE',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        downloadFile: (id, fileName) => {
            let url = `${QR_DEMO_API}secured/files/${id}/download/${fileName}`;

            var link = document.createElement('a')
            link.target = '_blank'
            link.href = url
            document.body.appendChild(link)
            link.click()
            link.remove()
        },
        downloadFileUnsecured: (id, fileName) => {
            let url = `${QR_DEMO_API}unsecured/files/${id}/download/${fileName}`;

            var link = document.createElement('a')
            link.target = '_'
            link.href = url
            document.body.appendChild(link)
            link.click()
            link.remove()
        },
        uploadFile: (fileRequest, userSettings) => {
            let url = `${QR_DEMO_API}secured/files/upload`;
            uploadFileByBytes(url, fileRequest, userSettings)
        },
        reuploadFile: (id, fileRequest, userSettings) => {
            let url = `${QR_DEMO_API}secured/files/${id}/re-upload`;
            uploadFileByBytes(url, fileRequest, userSettings)
        },
        updateFile: (id, data) => {
            let url = `${QR_DEMO_API}secured/files/${id}`;
            const req = new Request(
                url,
                {
                    method: 'PUT',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify(data)
                }
            )
            return authFetch(req)
        },
        uploadFileByBytes: (fileRequest, userSettings) => {
            try {
                let url = `${QR_DEMO_API}secured/files/upload/blob`;
                uploadFileByBytes(url, fileRequest, userSettings)
                alert('Файл был загружен')
                window.location.reload()
            } catch (ex) {
                alert(ex)
            }
        }
    }
}

export function adminApi() {
    const headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    }
    const authFetch = RequestDecorators.withAuth(fetch)

    return {
        getParticipants: () => {
            let url = `${QR_DEMO_API}admin/panel/participants`;

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        getParticipant: (id) => {
            let url = `${QR_DEMO_API}admin/panel/participants/${id}`;

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        createParticipant: (form) => {
            let url = `${QR_DEMO_API}admin/panel/participants`;

            const req = new Request(
                url,
                {
                    method: 'POST',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify(form)
                }
            )
            return authFetch(req)
        },
        getRoles: () => {
            const req = new Request(
                `${QR_DEMO_API}admin/panel/roles`,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        unblockUser: (id) => {
            if (emptyOrUndefined(id)) {
                throw new Error('ID пользователя не задан')
            }

            const req = new Request(
                `${QR_DEMO_API}admin/panel/participants/unblock`,
                {
                    method: 'POST',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify({ id: id })
                }
            )
            return authFetch(req)
        },
        blockUser: (id, reason) => {
            if (emptyOrUndefined(id)) {
                throw new Error('ID пользователя не задан')
            }

            const req = new Request(
                `${QR_DEMO_API}admin/panel/participants/block`,
                {
                    method: 'POST',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify({ id: id, reason: reason })
                }
            )
            return authFetch(req)
        },
        addRangeToParticipant: (id, json) => {
            const req = new Request(
                `${QR_DEMO_API}admin/panel/participants/${id}/ranges/add`,
                {
                    method: 'PUT',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify(json)
                }
            )
            return authFetch(req)
        },
        changeParticipantPassword: (id, password, confirmPassword) => {
            const req = new Request(
                `${QR_DEMO_API}admin/panel/participants/${id}/change-password`,
                {
                    method: 'PUT',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify({ password: password, confirmPassword: confirmPassword })
                }
            )
            return authFetch(req)
        },
        updateParticipant: (id, participantData) => {
            const req = new Request(
                `${QR_DEMO_API}admin/panel/participants/${id}`,
                {
                    method: 'PUT',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify(participantData)
                }
            )
            return authFetch(req)
        }
    }
}

export function userApi() {
    const headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    }

    const authFetch = RequestDecorators.withAuth(fetch)
    return {
        register: (login, password, password_2) => {
            const req = new Request(
                `${QR_DEMO_API}registration`,
                {
                    method: 'POST',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify({ 'username': login, 'password': password, 'confirmPassword': password_2 })
                }
            )
            return fetch(req)
        },
        login: (user, password) => {
            let url = `${QR_DEMO_API}login`;

            const req = new Request(
                url,
                {
                    method: 'POST',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify({ 'username': user, 'password': password })
                }
            )
            return fetch(req)
        },
        logout: () => {
            const req = new Request(
                `${QR_DEMO_API}secured/logout`,
                {
                    method: 'POST',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        me: () => {
            const req = new Request(
                `${QR_DEMO_API}secured/participants/me`,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include',
                }
            )
            return fetch(req)
        },
        authMe: () => {
            const req = new Request(
                `${QR_DEMO_API}secured/participants/me`,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include',
                }
            )
            return authFetch(req)
        },
        getSettings: () => {
            const req = new Request(
                `${QR_DEMO_API}secured/participants/settings`,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include',
                }
            )
            return authFetch(req)
        },
        updateSettings: (settings) => {
            const req = new Request(
                `${QR_DEMO_API}secured/participants/settings`,
                {
                    method: 'PATCH',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify(settings)
                }
            )
            return authFetch(req)
        },
        changePassword: (changePassw) => {
            const req = new Request(
                `${QR_DEMO_API}secured/participants/settings/change-password`,
                {
                    method: 'POST',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify(changePassw)
                }
            )
            return authFetch(req)
        }
    }
}

export function dictionaryApi() {
    const headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    }
    const authFetch = RequestDecorators.withAuth(fetch)

    return {
        getDictionariesByCode: (code) => {
            if (emptyOrUndefined(code)) {
                throw Error('Illegal code, could not be empty or null')
            }
            let url = `${QR_DEMO_API}secured/dictionaries?code=${code}`;

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        },
        getByCodeAndName: (code, name) => {
            if (emptyOrUndefined(code) || emptyOrUndefined(name)) {
                throw Error('Illegal code or name, could not be empty or null')
            }
            let url = `${QR_DEMO_API}secured/dictionaries/${code}?name=${name}`;

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            return authFetch(req)
        }
    }
}

const MAX_FILE_UPLOAD_SIZE = 16_777_216

function uploadSingleFile(url, fileRequest, userSettings, afterAction) {
    let data = new FormData()
    let file = fileRequest?.file?.files[0]
    let fileSize = file?.size

    if (notEmptyOrUndefined(file) && notEmptyOrUndefined(fileSize)) {
        data.append('file', file, file.name)

        let checkUploadSizeValue = userSettings?.settings?.checkUploadSize
        if ((checkUploadSizeValue == undefined || checkUploadSizeValue == null || checkUploadSizeValue) && fileSize > MAX_FILE_UPLOAD_SIZE) {
            throw new Error('Выберите файл менее 16 МБ!')
        } else {
            sendUploadRequest(url, fileRequest, data)
        }
    } else if (notEmptyOrUndefined(fileRequest.storagePath)) {
        data.append('storagePath', fileRequest.storagePath)
        sendUploadRequest(url, fileRequest, data, afterAction)
    }
}

function uploadFileByBytes(url, fileRequest, userSettings, afterAction) {
    if (fileRequest.fileStorageType == 'URL') {
        uploadSingleFile(url, fileRequest, userSettings, afterAction)
        return
    }

    dictionaryApi()
        .getByCodeAndName(DICTIONARIES.FILE_UPLOAD, DICTIONARIES_NAMES.CHUNK_SIZE)
        .then(resp => resp.json())
        .then(json => {
        console.log(json)
        let chunkSize = Number(json?.value)
        if (emptyOrUndefined(chunkSize)) {
            throw Error('Chunk size is not defined')
        }

        let file = fileRequest?.file?.files[0]
        let fileSize = file?.size

        const total = Math.ceil(fileSize / chunkSize);

        let createdId
        for (let i = 0; i < total; i++) {
            const start = i * chunkSize
            const end = Math.min(start + chunkSize, fileSize)
            const chunk = file.slice(start, end)

            const data = new FormData()
            data.append('chunk', chunk, file.name)
            data.append('no', i + 1)
            data.append('total', total)
            data.append('fileSize', fileSize)
            data.append('chunkSize', chunk?.size)
            data.append('name', fileRequest.name)
            data.append('description', fileRequest.description)
            data.append('public', fileRequest.public)
            data.append('active', fileRequest.active)
            if (notEmptyOrUndefined(createdId)) {
                data.append('fileId', createdId)
            }
            if (fileRequest.fileStorageType) {
                data.append('fileStorageType', fileRequest.fileStorageType)
            }

            const request = new XMLHttpRequest()
            try {
                request.open('POST', url, false)
                request.withCredentials = true
                request.send(data)
                if (request.status != 200 && request.status != 204) {
                    throw new Error('Ошибка сервера')
                }
                let resp = JSON.parse(request.responseText)
                createdId = resp?.fileId

                if (emptyOrUndefined(createdId)) {
                    throw new Error('Returned ID was empty or undefined for created part')
                }
            } catch (err) {
                let resp = JSON.parse(request.responseText)
                notify(resp?.errors[0]?.detail, resp?.errors[0]?.title)
                throw new Error(err)
            }
        }
        notify('Файл был загружен!')
        if (afterAction != null || afterAction != undefined && createdId) {
            afterAction(createdId)
        }
    })
}

function sendUploadRequest(url, fileRequest, data, afterAction) {
    data.append('name', fileRequest.name)
    data.append('description', fileRequest.description)
    data.append('public', fileRequest.public)
    data.append('active', fileRequest.active)

    if (fileRequest.fileStorageType) {
        data.append('fileStorageType', fileRequest.fileStorageType)
    }

    const request = new XMLHttpRequest()
    request.open('POST', url, false)
    request.withCredentials = true
    request.send(data)
    if (request.status != 200 && request.status != 204) {
        throw new Error('Ошибка сервера')
    }
    let respText = JSON.parse(request.responseText)
    if (afterAction != null && afterAction != undefined && respText?.fileId) {
        afterAction(respText?.fileId)
    }
}

class RequestDecorators {
    static withAuth(fetch) {
        return function (req) {
            const response = fetch(req);
            return response.then(resp => {
                if (!resp.ok) {
                    return Promise.reject(resp)
                }
                return resp
            })
                .catch(resp => {
                if (resp.status == 401) {
                    processFetchErrorToLogin()
                } else {
                    processFetchError(resp)
                }
            })
        }
    }
}
