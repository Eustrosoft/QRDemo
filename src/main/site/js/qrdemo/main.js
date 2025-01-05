import { setLk } from "./lk.js";
import { setCard } from "./card.js";
import { emptyOrUndefined, processFetchError, processFetchErrorToLogin } from "./utils.js";
import { setForm } from "./form.js";
import { userApi } from "./api.js";
import { LOCAL_STORAGE_USER } from "./localStorage.js";
import { setFiles } from "./files.js";
import { getLink } from "./components/link.js";
import { getTextLabel } from "./components/labels.js";
import { getHr } from "./components/hrs.js";
import { showAboutModal, showContactModal } from "./components/modals.js";

(function (window, document, undefined) {
    window.onload = init

    function init() {
        // Elements
        const mainBlock = document.getElementById('main_block')

        // Url params
        const urlParams = new URLSearchParams(window.location.search)
        const lk = urlParams.get('lk')
        const q = urlParams.get('q')
        const form = urlParams.get('form')
        const files = urlParams.get('files')
        const login = urlParams.get('login')

        // Basic listeners
        initBasicListeners()

        if (login) {
            if (mainBlock) {
                setLoginForm(mainBlock);
            }
        } else if (lk) {
            setLk(lk)
        } else if (q) {
            setCard(q)
        } else if (form) {
            setForm(form)
        } else if (files) {
            setFiles(files)
        } else {
            if (mainBlock) {
                setMainPage(mainBlock)
            }
        }
    }

})(window, document, undefined);

function setMainPage(parent) {
    let mainPage = document.createElement('div')
    mainPage.id = 'main_page'

    mainPage.innerHTML = `
        <p> Данный сайт предназначен для ознакомления с функциональностью системы <span class="color-red">QXYZ</span> </p>

        <video src="videos/demonstration.mp4" controls> </video>
        
        <p>  Полная версия сайта располагается по адресу: <a href="https://qr.qxyz.ru" target="_">QR.QXYZ.RU</a></p>
        <p>  Ознакомиться с документацией можно тут: <a href="https://qr.qxyz.ru/help/doc/qr.qxyz.ru-doc.pdf" target="_"> QXYZ Manifest</a></p>

        <p> В данной версии приложения разработчик не несёт ответственности за сохранность данных, стабильность системы и прочие причененные неудобства </p>
    `

    parent.appendChild(mainPage)
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
    userApi().me()
        .then(resp => {
            avatarDropdownContent.style.display = 'block'
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
            let cardsLink = getLink('Карточки', 'index.html?lk=true', '')
            let templatesLink = getLink('Шаблоны', 'index.html?form=true', '')
            let filesLink = getLink('Загруженные файлы', 'index.html?files=true', '')

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

            let settingsLink = getLink('Настройки', '', '')
            settingsLink.addEventListener('click', (e) => {
                e.preventDefault()
                window.location.href = '?lk=true&settings=true'
            })
            settingsSection.appendChild(settingsLink)

            avatarDropdownContent.appendChild(getHelpSection())
            avatarDropdownContent.appendChild(getHr())
            avatarDropdownContent.appendChild(settingsSection)
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
            let enterSection = document.createElement('div')
            enterSection.className = 'dropdown__content__section'
            let enterLink = getLink('Войти', 'index.html?login=true', '')
            enterSection.appendChild(enterLink)
            avatarDropdownContent.appendChild(getHelpSection())
            avatarDropdownContent.appendChild(getHr())
            avatarDropdownContent.appendChild(enterSection)
        })
    this.removeEventListener('click', showAvatarDropdownContentListener)
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
    let contactLink = getLink('Связаться с нами', '', '')
    contactLink.addEventListener('click', (e) => {
        e.preventDefault()
        showContactModal()
    })
    helpSection.appendChild(contactLink)
    return helpSection
}

function setLoginForm(parent) {
    let loginPart = document.createElement('div')
    loginPart.id = 'main_page'

    loginPart.innerHTML = `
        <h3> Страница входа </h3>
        <label>Логин: </label>
        <input type="text" id="login" placeholder="Введите логин">
        <label>Пароль: </label>
        <input type="password" id="password" placeholder="Введите пароль">
        <input type="submit" id="login_submit" value="Войти">
    `
    parent.appendChild(loginPart)
    document.getElementById("login_submit")
        .addEventListener('click', (e) => {
            login(e)
        })
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') login(e)
    })
}

async function login(e) {
    e.preventDefault()

    const login = document.getElementById('login').value
    const password = document.getElementById('password').value
    if (emptyOrUndefined(login) || emptyOrUndefined(password)) {
        alert("Заполните все поля")
        return
    }
    const authResponse =
        await userApi().login(login, password)
            .catch(processFetchError)
    if (!authResponse.ok) {
        const statusText = authResponse.json();
        processFetchError(JSON.stringify(statusText))
        return
    }

    await userApi()
        .me()
        .then((response) => response.json())
        .then(data => {
            localStorage.setItem(LOCAL_STORAGE_USER, JSON.stringify(data))
        })
        .then(rsp => window.location.href = 'index.html?lk=true')
        .catch(processFetchErrorToLogin)
}
