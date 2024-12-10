export const QR_DEMO_API_DEV = `${window.location.protocol}//${window.location.hostname}:9983/qr/v1/api/`
export const QR_DEMO_API = `${window.location.protocol}//${window.location.hostname}/qrCodeDemo/v1/api/`

export function qrApi() {
    const headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    }

    return {
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
            return fetch(req)
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
            return fetch(req)
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
            return fetch(req)
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
            return fetch(req)
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
            return fetch(req)
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
            return fetch(req)
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
            return fetch(req)
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
            return fetch(req)
        },
        uploadFile: (id, name, fileInput) => {
            let data = new FormData()
            let file = fileInput.files[0];

            data.append('file', file, file.name)

            const request = new XMLHttpRequest();
            let url = `${QR_DEMO_API}secured/qrs/${id}/files/${name}`;
            request.open('PUT', url, false)
            request.withCredentials = true
            request.send(data)
        },
        getFileLink: (id, name) => {
            return QR_DEMO_API + `unsecured/qrs/${id}/files/${name}`
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
            return fetch(req)
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
            return fetch(req)
        }
    }
}

export function adminApi() {
    const headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    }

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
            return fetch(req)
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
            return fetch(req)
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
            return fetch(req)
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
            return fetch(req)
        }
    }
}

export function userApi() {
    const headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    }

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
            return fetch(req)
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
        getSettings: () => {
            const req = new Request(
                `${QR_DEMO_API}secured/participants/settings`,
                {
                    method: 'GET',
                    headers: headers,
                    credentials: 'include',
                }
            )
            return fetch(req)
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
            return fetch(req)
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
            return fetch(req)
        }
    }
}