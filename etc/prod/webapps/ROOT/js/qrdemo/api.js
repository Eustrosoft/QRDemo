import { emptyOrUndefined, processFetchErrorToLogin } from "./utils.js";

export const QR_DEMO_API_DEV = `${window.location.protocol}//${window.location.hostname}:9983/qr/v1/api/`
export const QR_DEMO_API = `${window.location.protocol}//${window.location.hostname}/qrCodeDemo/v1/api/`
export const QR_PRINTER_URL = `printer.html`

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
        getQrs: () => {
            let url = `${QR_DEMO_API}secured/qrs`;

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
            let url = `${QR_DEMO_API}secured/qrs/code?q=${q}`;

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
            let url = `${QR_DEMO_API}secured/qrs`;

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
        uploadFormFile: (id, fileRequest) => {
            const url = `${QR_DEMO_API}secured/forms/${id}/files/upload`;
            uploadSingleFile(url, fileRequest)
        },
        uploadQRFile: (id, fileRequest) => {
            const url = `${QR_DEMO_API}secured/qrs/${id}/files/upload`;
            uploadSingleFile(url, fileRequest)
        },
        getFileLink: (id, name) => {
            return QR_DEMO_API + `unsecured/qrs/${id}/files/${name}`
        },
        getFileLinkV2: (id, fileId) => {
            return QR_DEMO_API + `unsecured/qrs/${id}/files/v2/${fileId}`
        },
        getRanges: () => {
            let url = `${QR_DEMO_API}secured/ranges`;

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
        createQR: (name, description) => {
            let url = `${QR_DEMO_API}secured/qrs`;

            const req = new Request(
                url,
                {
                    method: 'POST',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify({name: name, description: description})
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
        openFile: (id) => {
            let url = `${QR_DEMO_API}secured/files/${id}/download`;

            const req = new Request(
                url,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include'
                }
            )
            fetch(req)
                .then(resp => resp.status === 200 ? resp.blob() : Promise.reject('Что-то пошло не так'))
                .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.target = '_'
                a.style.display = 'none';
                a.href = url;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
            })
                .catch(() => alert('oh no!'));
        },
        downloadFile: (id) => {
            let url = `${QR_DEMO_API}secured/files/${id}/download`;

            var link = document.createElement('a')
            link.target = '_'
            link.href = url
            document.body.appendChild(link)
            link.click()
            link.remove()
        },
        downloadFileUnsecured: (id) => {
            let url = `${QR_DEMO_API}unsecured/files/${id}/download`;

            var link = document.createElement('a')
            link.target = '_'
            link.href = url
            document.body.appendChild(link)
            link.click()
            link.remove()
        },
        uploadFile: (fileRequest) => {
            let url = `${QR_DEMO_API}secured/files/upload`;
            uploadSingleFile(url, fileRequest)
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
                    body: JSON.stringify({'username': login, 'password': password, 'confirmPassword': password_2})
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
                    body: JSON.stringify({'username': user, 'password': password})
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
        }
    }
}

const MAX_FILE_UPLOAD_SIZE = 10_485_760

function uploadSingleFile(url, fileRequest) {
    let data = new FormData()
    let file = fileRequest.file.files[0]
    let fileSize = file.size

    if (fileSize > MAX_FILE_UPLOAD_SIZE) {
        alert('Файл слишком большой, выберите файл менее 10 МБ!')
        throw new Error('Выберите файл менее 10 МБ!')
    }

    data.append('file', file, file.name)
    data.append('name', fileRequest.name)
    data.append('description', fileRequest.description)
    data.append('public', fileRequest.public)

    const request = new XMLHttpRequest()
    request.open('POST', url, false)
    request.withCredentials = true
    request.send(data)
}

class RequestDecorators {
    static withAuth(fetch) {
        return function(req) {
            const response = fetch(req);
            return response.then(resp => {
                if (resp.status === 401) {
                    throw new Error('Unauthorized')
                }
                return resp
            })
                .catch(processFetchErrorToLogin)
        }
    }
}
