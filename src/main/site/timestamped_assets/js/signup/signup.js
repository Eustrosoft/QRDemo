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
        }).then(text => alert(`Your registration ID is: ${text}, save it and check your registration status`))
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
        }
    }
}
