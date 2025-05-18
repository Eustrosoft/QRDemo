import { renderLkPage as renderLkPage } from "./lk.js";
import { renderCardPage } from "./card.js";
import { emptyOrUndefined, notNullOrUndefined, processFetchErrorToLogin } from "./utils.js";
import { renderFormPage as renderFormPage } from "./form.js";
import { userApi } from "./api.js";
import { LOCAL_STORAGE_USER, LOCAL_STORAGE_USER_THEME } from "./localStorage.js";
import { renderFilesPage as renderFilesPage } from "./files.js";
import { getLink, getLinkWithAction } from "./components/link.js";
import { getTextLabel } from "./components/labels.js";
import { getHr } from "./components/hrs.js";
import { showAboutModal, showContactModal } from "./components/modals.js";
import { Loader } from "./components/loader.js";
import { getSwitch } from "./components/inputs.js";
import { getSpan, MAIN_TEXT } from "./components/texts.js";
import { notEmptyOrUndefined, processFetchError } from "../commons/common.js";
import { renderVersionHistory } from "./components/versions.js";

(function (window, document, undefined) {
    window.onload = init

    function init() {
        // Elements
        const mainBlock = document.getElementById('main_block')

        // Url params
        const urlParams = new URLSearchParams(window.location.search)
        const q = urlParams.get('q')
        const id = urlParams.get('id')
        const page = urlParams.get('page')
        const edit = urlParams.get('edit')

        // Basic listeners
        initBasicListeners()
        // Init user settings
        initUserSettings()

        if (page === 'login') {
            if (mainBlock) {
                renderLoginForm(mainBlock);
            }
        } else if (page === 'lk') {
            if (q) {
                let isEdit = edit ? true : false
                renderCardPage(q, false, isEdit)
            } else {
                renderLkPage()
            }
        } else if (q) {
            let isEdit = edit ? true : false
            renderCardPage(q, false, isEdit)
        } else if (page === 'forms') {
            if (id) {
                renderFormPage(id)
            } else {
                renderFormPage()
            }
        } else if (page === 'files') {
            renderFilesPage()
        } else {
            if (mainBlock) {
                renderLkPage()
            }
        }
    }

})(window, document, undefined);

export function initUserSettings() {
    let theme = localStorage.getItem(LOCAL_STORAGE_USER_THEME)
    if (notNullOrUndefined(theme)) {
        if (theme === 'dark') {
            document.body.classList.replace('light', 'dark')
        }
    }
}

function initBasicListeners() {
    const avatarDropdown = document.getElementById('avatar__dropdown')
    const avatarDropdownContent = document.getElementById('avatar__dropdown__content')
    if (avatarDropdown && avatarDropdownContent) {
        avatarDropdown.addEventListener('click', showAvatarDropdownContentListener)
        window.addEventListener('mousedown', (event) => {
            // Check if ther is modal windows on screen to close them first
            let modal = document.getElementsByClassName('modal')
            if (modal.length !== 0) {
                return
            }

            // Close the dropdown by condition
            if (event.target !== avatarDropdownContent
                && !avatarDropdownContent.contains(event.target)) {
                if (avatarDropdownContent.style.display == 'none') {
                    avatarDropdown.addEventListener('click', showAvatarDropdownContentListener)
                }
                avatarDropdownContent.style.display = 'none'
            }
        })
    }
}

function showAvatarDropdownContentListener() {
    const avatarDropdownContent = document.getElementById('avatar__dropdown__content')
    avatarDropdownContent.innerHTML = ''

    let loader = new Loader()
    avatarDropdownContent.style.display = 'block'
    avatarDropdownContent.appendChild(loader.get())
    loader.showLoader()

    userApi().me()
        .then(resp => {
            loader.destroy()
            if (resp.ok) {
                return resp.json()
            }
            throw new Error('Unauthorized')
        })
        .then(json => {
            // Personal data section
            let personDataSection = document.createElement('div')
            personDataSection.className = 'dropdown__content__section'
            let usernameLabel = getTextLabel(json?.username)
            let emailLabel = getTextLabel(json?.email, 'color-grey')
            let cardsLink = getLinkWithAction('Карточки', '?page=lk', '', renderLkPage)
            let templatesLink = getLinkWithAction('Шаблоны', '?page=forms', '', renderFormPage)
            let filesLink = getLinkWithAction('Загруженные файлы', '?page=files', '', renderFilesPage)

            personDataSection.appendChild(usernameLabel)
            personDataSection.appendChild(emailLabel)
            personDataSection.appendChild(cardsLink)
            personDataSection.appendChild(templatesLink)
            personDataSection.appendChild(filesLink)
            avatarDropdownContent.appendChild(personDataSection)
            avatarDropdownContent.appendChild(getHr())

            // Settings section
            let settingsSection = document.createElement('div')
            settingsSection.className = 'dropdown__content__section'

            let settingsLink = getLinkWithAction('Настройки', '', '', () => {
                renderLkPage(null, true)
            })
            settingsSection.appendChild(settingsLink)

            avatarDropdownContent.appendChild(getHelpSection())
            avatarDropdownContent.appendChild(getHr())
            avatarDropdownContent.appendChild(settingsSection)
            avatarDropdownContent.appendChild(getHr())
            avatarDropdownContent.appendChild(getThemeSection())
            avatarDropdownContent.appendChild(getHr())

            // Exit section
            let exitSection = document.createElement('div')
            exitSection.className = 'dropdown__content__section'
            let exitLink = getLink('Выйти', '', '')
            exitLink.addEventListener('click', (e) => {
                e.preventDefault()
                const toExit = confirm("Выйти?")
                if (toExit) {
                    userApi().logout()
                        .finally(resp => location.reload())
                }
            })
            exitSection.appendChild(exitLink)
            avatarDropdownContent.appendChild(exitSection)
        })
        .catch(ex => {
            loader.destroy()
            let enterSection = document.createElement('div')
            enterSection.className = 'dropdown__content__section'
            let enterLink = getLink('Войти', '?login=true', '')
            enterSection.appendChild(enterLink)
            avatarDropdownContent.appendChild(getHelpSection())
            avatarDropdownContent.appendChild(getHr())
            avatarDropdownContent.appendChild(getThemeSection())
            avatarDropdownContent.appendChild(getHr())
            avatarDropdownContent.appendChild(enterSection)
        })
    this.removeEventListener('click', showAvatarDropdownContentListener)
}

function getThemeSection() {
    let lsTheme = localStorage.getItem(LOCAL_STORAGE_USER_THEME)
    let theme = notNullOrUndefined(lsTheme) ? lsTheme : 'light'

    let themeSection = document.createElement('div')
    themeSection.className = 'dropdown__content__section'

    let themeLabel = getSpan('Тема')
    themeLabel.style.lineHeight = '16px'
    themeLabel.style.verticalAlign = 'center'
    let themeSwitch = getSwitch(theme === 'dark', 30, 20)
    themeSwitch.style.marginLeft = '6px'
    themeSwitch.children[0].addEventListener('change', function () {
        if (this.checked) {
            document.body.classList.replace('light', 'dark')
            localStorage.setItem(LOCAL_STORAGE_USER_THEME, 'dark')
        } else {
            document.body.classList.replace('dark', 'light')
            localStorage.setItem(LOCAL_STORAGE_USER_THEME, 'light')
        }
        themeSwitch = getSwitch(theme === 'dark', 30, 20)
    })
    themeLabel.appendChild(themeSwitch)
    themeSection.append(themeLabel)
    return themeSection
}

function getHelpSection() {
    let helpSection = document.createElement('div')
    helpSection.className = 'dropdown__content__section'
    let aboutLink = getLink('О сервисе', '', '')
    aboutLink.addEventListener('click', (e) => {
        e.preventDefault()
        showAboutModal()
    })
    helpSection.appendChild(aboutLink)
    let versionsLink = getLink('История версий', '', '')
    versionsLink.addEventListener('click', (e) => {
        e.preventDefault()
        renderVersionHistory()
    })
    helpSection.appendChild(versionsLink)
    let contactLink = getLink('Связаться с нами', '', '')
    contactLink.addEventListener('click', (e) => {
        e.preventDefault()
        showContactModal()
    })
    helpSection.appendChild(contactLink)
    let helpLink = getLink('Помощь', '/help/', '')
    helpSection.appendChild(helpLink)
    return helpSection
}

function renderLoginForm(parent) {
    let loginPart = document.createElement('div')
    loginPart.id = 'main_page'

    loginPart.innerHTML = `
        <h3> Страница входа </h3>
        <label>Логин: </label>
        <input type="text" id="login" placeholder="Введите логин">
        <label>Пароль: </label>
        <input type="password" id="password" placeholder="Введите пароль">
        <button class="big_button" id="login_submit">Войти</button>
        <!-- <br> Или <br>
        <a class="big_button" id="register" href="/signup/">Регистрация</a>
        -->
    `
    parent.appendChild(loginPart)
    document.getElementById("login_submit")
        .addEventListener('click', (e) => {
            login(e)
            document.activeElement.blur()
        })
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            login(e)
            document.activeElement.blur()
        }
    })
}

async function login(e) {
    e.preventDefault()

    const login = document.getElementById('login').value
    const password = document.getElementById('password').value
    if (emptyOrUndefined(login) || emptyOrUndefined(password)) {
        notify("Заполните все поля")
        return
    }

    try {
        let loginResp = await userApi().login(login, password)
        if (!loginResp.ok) {
            processFetchError(loginResp)
            return
        }
        processToLk()
        return
    } catch (e) {
        notify(e?.message)
    }
}

async function processToLk() {
    await userApi()
        .me()
        .then((response) => response.json())
        .then(data => {
            localStorage.setItem(LOCAL_STORAGE_USER, JSON.stringify(data))
        })
        .then(rsp => {
            const urlParams = new URLSearchParams(window.location.search)
            let q = notEmptyOrUndefined(urlParams.get('q')) ? urlParams.get('q') : ''
            let edit = notEmptyOrUndefined(urlParams.get('edit')) ? urlParams.get('edit') : ''
            let val = `q=${q}&edit=${edit}`
            window.location.href = '?page=lk&' + val
        })
        .catch(processFetchErrorToLogin)
}
