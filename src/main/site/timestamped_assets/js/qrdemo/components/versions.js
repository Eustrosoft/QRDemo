import { qrApi } from "../api.js"
import { sanitize } from "../utils.js"
import { APP_VERSION } from "../version.js"
import { getModalWindow } from "./modals.js"

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
            appVersionSpan.innerText = `${text}_b ${APP_VERSION}_f`
        })
        .catch(ex => console.log(ex))
    return appVersionSpan
}


let versionListEl, versionMetaEl, versionTextEl;
let currentVersion = null;

export function renderVersionHistory() {
    const container = document.createElement('div')
    container.className = 'version-container'

    const sidebar = document.createElement('div')
    sidebar.className = 'version-sidebar'
    sidebar.id = 'versionList'

    const content = document.createElement('div')
    content.className = 'version-content'
    content.id = 'versionContent'

    const header = document.createElement('div')
    header.className = 'version-header'
    header.id = 'versionMeta'

    const body = document.createElement('div')
    body.className = 'version-body'
    body.id = 'versionText'

    content.appendChild(header)
    content.appendChild(body)
    container.appendChild(sidebar)
    container.appendChild(content)

    let modal = getModalWindow('История версий', container)
    modal.style.display = 'block'

    // Привязываем DOM-элементы после генерации
    versionListEl = document.getElementById('versionList')
    versionMetaEl = document.getElementById('versionMeta')
    versionTextEl = document.getElementById('versionText')

    qrApi().getAppVersions()
        .then(resp => resp.json())
        .then(data => {
            renderVersionList(data)
            if (data.length > 0) {
                loadVersion(data[0].version)
            }
        })
        .catch(err => {
            console.error('Failed to fetch versions')
        })
}

function renderVersionList(versions) {
    versionListEl.innerHTML = ''
    versions.forEach(v => {
        const btn = document.createElement('button')
        btn.className = 'version-button'
        btn.textContent = v.version
        btn.addEventListener('click', () => loadVersion(v.version))
        versionListEl.appendChild(btn)
    })
}

// Загрузка и отображение версии
function loadVersion(version) {
    if (isSameBtn(version)) {
        return
    }

    qrApi().getAppVersionsContent(version)
        .then(res => res.json())
        .then(data => {
            updateActiveButton(version)
            renderVersionContent(version, data)
        })
        .catch(err => {
            console.error(`Ошибка загрузки ${version}:`, err)
        });
}

// Обновление активной кнопки
function updateActiveButton(version) {
    const buttons = versionListEl.querySelectorAll('.version-button')
    buttons.forEach(btn => {
        btn.classList.toggle('active', btn.textContent === version)
    });
}

function isSameBtn(version) {
    const buttons = versionListEl.querySelectorAll('.version-button')
    let same = false
    buttons.forEach(btn => {
        if (btn.textContent == version && btn.classList.contains('active')) {
            same = true
        }
    });
    return same
}

function renderVersionContent(version, data) {
    versionMetaEl.innerHTML = `
      <div><strong>Версия:</strong> ${sanitize(version)}</div>
      <div><strong>Автор:</strong> ${sanitize(data.author)} | 
      <strong>Дата:</strong> ${sanitize(data.releaseDate)}</div>
    `

    versionTextEl.textContent = data.content
}
