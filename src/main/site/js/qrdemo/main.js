import {setLk} from "./lk.js";
import {setCard} from "./card.js";
import {emptyOrUndefined, processFetchError, processFetchErrorToLogin} from "./utils.js";
import {setForm} from "./form.js";
import {userApi} from "./api.js";
import {LOCAL_STORAGE_USER} from "./localStorage.js";

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
        const login = urlParams.get('login')

        if (login) {
            setLoginForm(mainBlock);
        } else if (lk) {
            setLk(lk)
        } else if (q) {
            setCard(q)
        } else if (form) {
            setForm(form)
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
        const statusText = await authResponse.json();
        processFetchError(JSON.stringify(statusText))
        return
    }

    await userApi()
        .me()
        .then((response) => response.json())
        .then(data => {
            console.log(data)
            localStorage.setItem(LOCAL_STORAGE_USER, JSON.stringify(data))
        })
        .then(rsp => window.location.href = 'index.html?lk=true')
        .catch(processFetchErrorToLogin)
}
