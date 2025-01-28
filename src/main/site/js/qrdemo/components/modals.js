import { copyToClipboard, generateRandomPassword, notEmptyOrUndefined } from "../../commons/common.js"
import { qrApi } from "../api.js"
import { APP_VERSION } from "../version.js"
import { getCustomButton } from "./buttons.js"
import { getHr } from "./hrs.js"
import { getInput } from "./inputs.js"
import { getTextLabel } from "./labels.js"
import { getParagraph, getSpan } from "./texts.js"

export function getModalWindow(modalName, modalInner) {
    let modalWindow = document.createElement('div')
    modalWindow.className = 'modal'

    let modalContent = document.createElement('div')

    modalContent.className = 'modal-content'
    modalContent.prepend(getModalHeader(modalWindow, modalName))
    modalInner.className = 'modal-content-inner'
    modalContent.append(modalInner)

    modalWindow.appendChild(modalContent)
    document.getElementsByTagName('body')[0].appendChild(modalWindow)

    window.addEventListener('mousedown', (event) => {
        if (event.target == modalWindow) {
            modalWindow.remove();
        }
    })

    return modalWindow
}

function getModalHeader(modal, headerName) {
    let modalContentHeader = document.createElement('div')
    modalContentHeader.className = 'modal-header'

    let modalHeaderName = document.createElement('span')
    modalHeaderName.innerHTML = headerName
    modalHeaderName.className = 'title'

    let modalHeaderClose = document.createElement('span')
    modalHeaderClose.innerHTML = '&times;'
    modalHeaderClose.className = 'close'

    modalContentHeader.appendChild(modalHeaderName)
    modalContentHeader.appendChild(modalHeaderClose)

    modalHeaderClose.addEventListener('click', () => {
        modal.remove();
    })

    return modalContentHeader
}

export function showGenerateRandomPasswordModal() {
    let generatePasswordBlock = document.createElement('div')
    let randomPasswordInput = getInput('Пароль', 'text', false, 'random_password_input')
    let randomPasswordGenerateBtn = getCustomButton('Сгенерировать')
    let copyToBufferBtn = getCustomButton('Скопировать')
    generatePasswordBlock.append(randomPasswordInput, copyToBufferBtn, randomPasswordGenerateBtn)

    randomPasswordGenerateBtn.addEventListener('click', async () => {
        const randomPassword = generateRandomPassword()
        let input = document.getElementById('random_password_input')
        if (input) {
            input.value = randomPassword
        }

    })
    copyToBufferBtn.addEventListener('click', () => {
        let input = document.getElementById('random_password_input')
        if (input) {
            if (notEmptyOrUndefined(input.value)) {
                copyToClipboard(input.value)
                copyToBufferBtn.classList.add('green_pulse')
                setTimeout(() => {
                    copyToBufferBtn.classList.remove('green_pulse')
                }, 1_000)
                return
            }
            alert('Нечего копировать')
        }
    })

    let modal = getModalWindow('Генерация случайного пароля', generatePasswordBlock)
    modal.style.display = 'block'
    let rndPassInput = document.getElementById('random_password_input')
    if (rndPassInput) {
        const randomPassword = generateRandomPassword()
        rndPassInput.value = randomPassword
    }
}

export function showAboutModal() {
    let aboutBlock = document.createElement('div')

    let serviceLabel = getTextLabel('QXYZ - Демонстрационный стенд')
    aboutBlock.appendChild(serviceLabel)
    aboutBlock.appendChild(getHr())

    let aboutParagraph = getParagraph(`Краткое пояснение к системе:`, 'fs-14rem fw-400 fs-italic margin-10')
    let aboutPar1 = getParagraph(`- В этой системе вы можете ознакомиться пройти демонстрационный период для ознакомления с возможностями, предоставляемыми системой`, 'fs-14rem fw-400 fs-italic margin-10')

    aboutBlock.appendChild(aboutParagraph)
    aboutBlock.appendChild(aboutPar1)
    aboutBlock.appendChild(getHr())

    let versionSpan = getSpan('', 'color-grey')
    qrApi().getAppVersion()
        .then(resp => {
            if (resp.ok) {
                return resp.text()
            }
            throw new Error('Ошибка при получении версии приложения')
        })
        .then(text => {
            versionSpan.innerHTML = `${text}_b ${APP_VERSION}_f`
        })
        .catch(ex => {
            versionSpan.innerHTML = ex
        })

    aboutBlock.appendChild(versionSpan)
    let modal = getModalWindow('О сервисе', aboutBlock)
    modal.style.display = 'block'
}

export function showContactModal() {
    let contactBlock = document.createElement('div')

    let serviceLabel = getTextLabel('QXYZ - Поддержка')
    contactBlock.appendChild(serviceLabel)
    contactBlock.appendChild(getHr())

    let contactEmailParagraph = getParagraph(`По всем вопросам пишите на e-mail: `, 'fs-14rem fw-400 fs-italic margin-10')
    let emailSpan = getSpan('denis2211@yandex.ru', 'contact_address')

    emailSpan.addEventListener('click', (e) => {
        window.open(`mailto:${e.target.innerText}`);
    })

    contactEmailParagraph.appendChild(emailSpan)

    let contactPhoneParagraph = getParagraph(`Или звоните на номер: `, 'fs-14rem fw-400 fs-italic margin-10')
    let phoneSpan = getSpan('+7(916)666-24-90', 'contact_address')

    phoneSpan.addEventListener('click', (e) => {
        window.open(`tel:${e.target.innerText}`);
    })

    contactEmailParagraph.appendChild(emailSpan)
    contactPhoneParagraph.appendChild(phoneSpan)

    contactBlock.appendChild(contactEmailParagraph)
    contactBlock.appendChild(contactPhoneParagraph)

    let modal = getModalWindow('Связаться с нами', contactBlock)
    modal.style.display = 'block'
}
