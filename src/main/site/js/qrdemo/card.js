import { fieldToHtml, getQRImageDiv, longToHex, toLoginIfNotAuthorized } from "./utils.js";
import { qrApi } from "./api.js";
import { getInput } from "./components/inputs.js";
import { getModalWindow } from "./components/modals.js";

const mainBlock = document.getElementById('main_block')
let divCard = document.createElement('div')
let edit = false
let create = false

var qrId
var qrCode

divCard.setAttribute('id', 'my_lk')

export function setCard(q, creating = false) {
    document.title = `QRDemo - Карточка ${q}`
    const urlParams = new URLSearchParams(window.location.search)
    if (urlParams.get('edit')) {
        edit = urlParams.get('edit')
        toLoginIfNotAuthorized()
    }
    if (urlParams.get('create') || creating) {
        create = true
        toLoginIfNotAuthorized()
    }

    mainBlock.appendChild(divCard)
    setQRCard(q, divCard)
}

function setQRCard(q, div) {
    if (q && div) {
        setQRInfo(q, div)
    }
}

function setQRInfo(q, div) {
    let promiseToResolve = edit ? qrApi().getQr(q) : qrApi().getQrPublic(q)

    promiseToResolve
        .then(resp => resp.json())
        .then(json => {
            qrId = json?.id
            qrCode = json?.code

            let divCardInfo = document.createElement('div')
            let cardInfoHtml = getCardInfoHtml(json)
            divCardInfo.className = 'code_info'

            // const qrImageDiv = getQRImageDiv(longToHex(json?.code));
            // divCardInfo.append(qrImageDiv)
        if (cardInfoHtml.innerHTML === null || cardInfoHtml.innerHTML === undefined || cardInfoHtml.innerHTML === '') {
            cardInfoHtml.innerHTML = '<h1>Нет информации для этой карточки</h1>'
        }

            divCardInfo.append(cardInfoHtml)
            div.appendChild(divCardInfo)
            addCodeBtnListeners()
        })
        .catch(ex => alert(ex))
}

function getCardInfoHtml(qr) {
    const qrForm = qr?.form
    const fields = qr?.form?.fields
    const name = qr?.name
    const description = qr?.description

    let codeDiv = document.createElement('div')
    codeDiv.className = 'code_block'

    let nameLabel = document.createElement('label')
    nameLabel.innerHTML = 'Название:'
    let nameElem = document.createElement('input')
    nameElem.type = 'text'
    nameElem.value = name
    nameElem.id = 'name_input'
    nameElem.placeholder = 'Введите имя карточки'
    if (!edit) {
        nameElem.readOnly = true
    }
    let descriptionLabel = document.createElement('label')
    descriptionLabel.innerHTML = 'Описание:'
    let descriptionElem = document.createElement('input')
    descriptionElem.type = 'text'
    descriptionElem.id = 'description_input'
    descriptionElem.value = description
    descriptionElem.placeholder = 'Введите описание карточки'
    if (!edit) {
        descriptionElem.readOnly = true
    }

    if (edit) {
        let formLabel = document.createElement('label')
        formLabel.innerHTML = 'Шаблон для отображения карточки:'
        let formChooseElement = document.createElement('select')
        formChooseElement.id = 'form_select'
        const opt = document.createElement('option');
        opt.value = ''
        opt.id = ''
        opt.innerHTML = ''
        formChooseElement.append(opt)

        qrApi().getAllForms().then(resp => resp.json())
            .then(json => {
                for (let i = 0; i < json.length; i++) {
                    const opt = document.createElement('option');
                    opt.value = json[i].name
                    opt.id = json[i].id
                    opt.innerHTML = json[i].name
                    if (qrForm !== null && qrForm?.id === json[i].id) {
                        opt.selected = true
                    }
                    formChooseElement.append(opt)
                }
            })
            .catch(ex => alert(ex))

        codeDiv.append(formLabel)
        codeDiv.append(formChooseElement)
    }

    if (edit) {
        codeDiv.appendChild(nameLabel)
        codeDiv.appendChild(nameElem)
        codeDiv.appendChild(descriptionLabel)
        codeDiv.appendChild(descriptionElem)
    }

    // Header for block
    // let codeHeader = document.createElement('h3')
    // codeHeader.innerHTML = bk?.name
    // codeDiv.append(codeHeader)

    for (let field in fields) {
        const formFieldDiv = document.createElement('div');
        formFieldDiv.className = 'form_field_div'
        formFieldDiv.innerHTML = fieldToHtml(
            fields[field],
            getDataFromForm(qr, fields[field]?.name),
            edit,
            fields[field]?.id,
            qrId
        )
        codeDiv.append(formFieldDiv)
    }


    if (edit) {
        const saveBtn = document.createElement('input');
        saveBtn.value = 'Сохранить'
        saveBtn.type = 'submit'
        saveBtn.className = 'big_button'
        saveBtn.id = 'save_code_btn'
        codeDiv.append(saveBtn)

        const showPublicBtn = document.createElement('button');
        showPublicBtn.innerHTML = 'Посмотреть публичную версию'
        showPublicBtn.className = 'big_button'
        showPublicBtn.id = 'show_public_code_btn'
        showPublicBtn.style = 'width: 97%'
        codeDiv.append(showPublicBtn)

        const showOnPhoneBtn = document.createElement('button');
        showOnPhoneBtn.innerHTML = 'Посмотреть публичную версию на смартфоне'
        showOnPhoneBtn.className = 'big_button'
        showOnPhoneBtn.id = 'show_public_code_phone_btn'
        showOnPhoneBtn.style = 'width: 97%'
        codeDiv.append(showOnPhoneBtn)
    }

    return codeDiv
}

let filesInputArray = []

function addCodeBtnListeners() {
    const saveCodeBtn = document.getElementById('save_code_btn');
    if (saveCodeBtn) {
        saveCodeBtn.addEventListener('click', () => {
            let nameElem = document.getElementById('name_input')
            let descriptionElem = document.getElementById('description_input')
            let formElement = document.getElementById('form_select')

            const data = collectFormData();
            qrApi().saveQr({
                id: qrId,
                code: qrCode,
                name: nameElem.value,
                description: descriptionElem.value,
                form: { id: formElement.options[formElement.selectedIndex].id },
                data: data
            }).then(resp => {
                if (resp.ok) {
                    alert('Карточка была обновлена!')
                    location.reload()
                } else {
                    alert(resp.json())
                }
            }).catch(ex => alert(ex))
        })
    }
    const showPublicBtn = document.getElementById('show_public_code_btn')
    if (showPublicBtn) {
        showPublicBtn.addEventListener('click', () => {
            location.href = `?q=${Number(qrCode).toString(16)}`
        })
    }
    const showPublicPhoneBtn = document.getElementById('show_public_code_phone_btn')
    if (showPublicPhoneBtn) {
        showPublicPhoneBtn.addEventListener('click', () => {
            let iframe = document.createElement('iframe')
            iframe.id = 'phone_iframe'
            iframe.src = `?q=${Number(qrCode).toString(16)}`
            iframe.style.width = '500px'
            iframe.style.height = '882px'
            let modal = getModalWindow('Просмотр с телефона', iframe)
            modal.style.display = 'block'
            modal.firstChild.style.width = '500px'
            modal.firstChild.style.height = '932px'
        })
    }
}

const processedTags = ['INPUT']

function collectFormData() {
    let data = {}
    const inputDivs = document.getElementsByClassName('form_field_div');
    for (let i in inputDivs) {
        const inputDiv = inputDivs[i];
        const children = inputDiv.childNodes;
        for (let chI in children) {
            const child = children[chI];
            if (processedTags.includes(child.nodeName)) {
                if (child.type === 'file') {
                    filesInputArray.push(child)
                    if (child.files.length !== 0) {
                        data[child.name] = child.files[0].name
                    }
                } else {
                    data[child.name] = child.value
                }
            }
        }
    }
    return data
}

function getDataFromForm(form, key) {
    if (form?.data === null || form?.data === undefined) {
        return ''
    }
    try {
        const data = form?.data[key];
        if (data === undefined || data === null) {
            return ''
        }
        return data
    } catch (ex) {
        return ''
    }
}
