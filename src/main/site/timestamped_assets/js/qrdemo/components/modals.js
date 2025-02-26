import { copyToClipboard, generateRandomPassword, notEmptyOrUndefined } from "../../commons/common.js"
import { qrApi } from "../api.js"
import { notify } from "../notifications.js"
import { longToHex } from "../utils.js"
import { APP_VERSION } from "../version.js"
import { getBigButton, getCustomButton } from "./buttons.js"
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
    modalHeaderName.innerText = headerName
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
            versionSpan.innerText = `${text}_b ${APP_VERSION}_f`
        })
        .catch(ex => {
            versionSpan.innerText = ex
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
    let emailSpan = getSpan('qrdemo@eustrosoft.org', 'contact_address')

    emailSpan.addEventListener('click', (e) => {
        window.open(`mailto:${e.target.innerText}`);
    })

    contactEmailParagraph.appendChild(emailSpan)

    let contactPhoneParagraph = getParagraph(`Или звоните на номер: `, 'fs-14rem fw-400 fs-italic margin-10')
    let phoneSpan = getSpan('+7(995)116-16-01', 'contact_address')

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

export function showCreateQrModal(renderQRsTableCallback, div, settings) {
    const createQrModal = document.createElement('div')

    let nameBlock = getInput('Название', 'text', false, 'create_qr_name', 'Введите имя для карточки...', false)
    let descriptionBlock = getInput('Описание', 'text', false, 'create_qr_description', 'Введите описание для карточки...', false)

    let formLabel = document.createElement('label')
    formLabel.innerText = 'Шаблон:'

    let formChooseElement = document.createElement('select')
    formChooseElement.id = 'form_select'

    const opt = document.createElement('option')
    opt.value = ''
    opt.id = ''
    opt.innerText = ''
    formChooseElement.append(opt)

    qrApi().getAllForms().then(resp => resp.json())
        .then(json => {
            for (let i = 0; i < json.length; i++) {
                const opt = document.createElement('option')
                opt.value = json[i].name
                opt.id = json[i].id
                opt.innerText = json[i].name
                formChooseElement.append(opt)
            }
        })

    let rangeLabel = getTextLabel('Диапазон:')

    let rangeSelect = document.createElement('select')
    rangeSelect.id = 'range_select'

    const opt2 = document.createElement('option')
    opt2.value = ''
    opt2.id = ''
    opt2.innerText = ''
    rangeSelect.append(opt2)

    qrApi().getRanges().then(resp => resp.json())
        .then(json => {
            for (let i = 0; i < json.length; i++) {
                const opt = document.createElement('option')
                opt.value = json[i].id
                opt.id = json[i].id
                opt.innerText = longToHex(json[i]?.from) + ' - ' + longToHex(json[i]?.to)
                rangeSelect.append(opt)
            }
        })

    let createBtn = getBigButton('Создать')
    createBtn.addEventListener('click', () => {
        let formElement = document.getElementById('form_select')
        let formId = formElement.options[formElement.selectedIndex].id
        let rangeElement = document.getElementById('range_select')
        let rangeId = rangeElement.options[rangeElement.selectedIndex].id

        qrApi().createQR(
            {
                name: document.getElementById('create_qr_name')?.value,
                description: document.getElementById('create_qr_description')?.value,
                rangeId: rangeId,
                formId: formId,
            }
        )
        .then(resp => {
            if (resp.ok) {
                notify('Карточка была создана!')
                renderQRsTableCallback(div, settings)
            } else if (resp.status === 500) {
                alert("Вы достигли лимита карточек")
            } else {
                alert('Ошибка при создании карточки')
            }
            document.activeElement.blur()
        })
        .catch(ex => alert(ex))
    })

    createQrModal.append(nameBlock, descriptionBlock, formLabel, formChooseElement, rangeLabel, rangeSelect, createBtn)
    let modal = getModalWindow('Создание карточки', createQrModal)
    modal.style.display = 'block'
}