const QR_DEMO_API_DEV = `${window.location.protocol}//${window.location.hostname}:9983/qr/v1/api/`
const QR_DEMO_API = `${window.location.protocol}//${window.location.hostname}/qrCodeDemo/v1/api/`

const form = document.getElementById("signupForm");
form.addEventListener("submit", function (event) {
    event.preventDefault();
    const formData = new FormData(form);
    const data = Object.fromEntries(formData)
    console.log(data);
    registrationApi()
        .register(data)
        .then(resp => {
            if (!resp.ok) {
                return Promise.reject(resp)
            }
            return resp.text()
        }).then(text => alert(`Ваш уникальный регистрационный ID: ${text}, сохраните его, чтобы проверить статус заявки`))
        .catch(resp => {
            return resp.json()
        }).then(json => {
            let errors = json?.errors
            for (let err in errors) {
                let prop = errors[err]?.source?.property != 'Unknown' ? errors[err]?.source?.property : ''
                alert(`${errors[err]?.title}: ${prop} ${errors[err]?.detail}`)
            }
        })
});

const checkForm = document.getElementById("checkupForm")
const statusDiv = document.getElementById("registrationStatuses")
checkForm.addEventListener("submit", function (event) {
    event.preventDefault();
    const formData = new FormData(checkForm);
    const data = Object.fromEntries(formData)
    console.log(data);
    registrationApi()
        .checkStatus(data['registration_code'])
        .then(resp => {
            if (!resp.ok) {
                return Promise.reject(resp)
            }
            return resp.json()
        }).then(json => {
            statusDiv.innerHTML = ''
            const status = json?.status
            const statusLine = getStatusLine(REGISTRATION_STATUSES, status)
            statusDiv.append(statusLine)
        })
        .catch(resp => {
            return resp.json()
        }).then(json => {
            let errors = json?.errors
            for (let err in errors) {
                let prop = errors[err]?.source?.property != 'Unknown' ? errors[err]?.source?.property : ''
                alert(`${errors[err]?.title}: ${prop} ${errors[err]?.detail}`)
            }
        })
});

function registrationApi() {
    const headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    }
    return {
        register: (formData) => {
            let url = `${QR_DEMO_API}unsecured/registrations`;

            const req = new Request(
                url,
                {
                    method: 'POST',
                    headers: headers,
                    credentials: 'include',
                    body: JSON.stringify(formData)
                }
            )
            return fetch(req)
        },
        checkStatus: (uid) => {
            let url = `${QR_DEMO_API}unsecured/registrations/${uid}`;

            const req = new Request(
                url,
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

const REGISTRATION_STATUSES = {
    PENDING: 'Ожидание',
    IN_WORK: 'В работе',
    ACCEPTED: 'Заявка принята',
    REJECTED: 'Заявка отклонена'

}

function getStatusLine(statuses, currentStatus) {
    const statusLine = document.createElement('div')
    statusLine.className = 'status-line'

    const entries = Object.entries(statuses)
    let index = 0
    for (const [status, text] of entries) {
        const statusCircle = document.createElement('div')
        statusCircle.className = 'status-circle ' + (status === currentStatus ? 'completed' : 'waited')

        const tooltip = document.createElement('div')
        tooltip.className = 'tooltip'
        tooltip.textContent = text
        statusCircle.appendChild(tooltip)

        statusLine.appendChild(statusCircle)

        if (index < entries.length - 1) {
            const connector = document.createElement('div')
            connector.className = 'connector'
            statusLine.appendChild(connector)
        }
        index++
    }

    return statusLine
}