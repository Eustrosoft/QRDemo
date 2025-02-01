import { qrApi } from "../api.js"
import { APP_VERSION } from "../version.js"

export function getAppVersionSpan() {
    let appVersionSpan = document.createElement('span')
    qrApi().getAppVersion()
        .then(resp => {
            if (resp.ok) {
                return resp.text()
            }
            throw new Error('Ошибка при получении версии приложения')
        })
        .then(text => {
            appVersionSpan.innerHTML = `${text}_b ${APP_VERSION}_f`
        })
        .catch(ex => console.log(ex))
    return appVersionSpan
}