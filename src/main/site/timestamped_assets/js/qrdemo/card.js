import { emptyOrUndefined, fieldToHtml, formatBytes } from "./utils.js";
import { QR_DEMO_API, qrApi, userApi } from "./api.js";
import { getModalWindow } from "./components/modals.js";
import { ActionColumn, getComplexTable, getTable, getTr, TableHead } from "./components/tables.js";
import { getBigButton, getCustomButton } from "./components/buttons.js";
import { downloadFileUnsecured, showEditFileModal, showUploadFileModal } from "./files.js";
import { addDeleteFileRowActions, Field } from "./form.js";
import { get2TextLabels, getTextLabel } from "./components/labels.js";
import { getInput, getSelect, getSingleInput } from "./components/inputs.js";
import { booleanToString, notEmptyOrUndefined } from "../commons/common.js";
import { getNavigationMenu } from "./components/blocks.js";
import { formatDate } from "../commons/dateUtils.js";
import { getLink } from "./components/link.js";
import { notify } from "./notifications.js";
import { getAccordion } from "./components/accordion.js";

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
    }
    if (urlParams.get('create') || creating) {
        create = true
    }
    if (!edit) {
        document.getElementsByClassName('header')[0].style.display = 'none'
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
            let cardInfoHtml = edit
                ? getEditCardInfoHtml(json)
                : getViewCardInfoHtml(json)
            divCardInfo.className = 'code_info'
            if (edit) {
                div.prepend(getNavigationMenu())
                divCardInfo.classList.add('basic_card')
            }

            // QR image
            // const qrImageDiv = getQRImageDiv(longToHex(json?.code));
            // divCardInfo.append(qrImageDiv)
            if (cardInfoHtml.innerHTML === null || cardInfoHtml.innerHTML === undefined || cardInfoHtml.innerHTML === '') {
                cardInfoHtml.innerHTML = '<h1>Нет информации для этой карточки</h1>'
                cardInfoHtml.style.textAlign = 'center'
                cardInfoHtml.style.justifyContent = 'center'
                cardInfoHtml.style.height = 'calc(100vh - 8vw)'
            }
            divCardInfo.append(cardInfoHtml)
            div.appendChild(divCardInfo)
            if (edit) {
                addCodeBtnListeners()
            } else {
                addDownloadPublicFileRowActions()
            }
        })
}

function getEditCardInfoHtml(qr) {
    const qrForm = qr?.form
    const fields = qr?.form?.fields
    const name = qr?.name
    const description = qr?.description
    const action = qr?.action
    const redirect = qr?.redirect

    let codeDiv = document.createElement('div')
    codeDiv.className = 'code_block'

    let basicFieldsDiv = document.createElement('div')

    let nameInput = getInput('Название:', 'text', false, 'name_input', 'Введите имя карточки', true, false, name)
    let descriptionInput = getInput('Описание:', 'text', false, 'description_input', 'Введите описание карточки', true, false, description)

    let actionInput = getSelect('Действие:', 'action_select', ['STANDARD', 'REDIRECT', 'QRSVC'], action, false, true)
    let redirectInput = getInput('Перенаправить на:', 'text', false, 'redirect_input', 'Введите ссылку для перенаправления', true, false, redirect)
    if (action === 'STANDARD') {
        let redirInp = redirectInput.childNodes[1]
        redirInp.classList.add('color-grey')
        redirInp.readOnly = true
    }

    let formLabel = document.createElement('label')
    formLabel.innerText = 'Шаблон:'

    let formSelectDiv = document.createElement('div')
    formSelectDiv.className = 'flex'
    let formChooseElement = document.createElement('select')
    formChooseElement.id = 'form_select'
    let formViewBtn = getBigButton('Открыть шаблон', null, 'fs-14rem')

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
                if (qrForm !== null && qrForm?.id === json[i].id) {
                    opt.selected = true
                }
                formChooseElement.append(opt)
            }
        })

    formSelectDiv.append(formChooseElement, formViewBtn)

    basicFieldsDiv.appendChild(formLabel)
    basicFieldsDiv.appendChild(formSelectDiv)
    basicFieldsDiv.appendChild(nameInput)
    basicFieldsDiv.appendChild(descriptionInput)
    basicFieldsDiv.appendChild(actionInput)
    basicFieldsDiv.appendChild(redirectInput)
    let accordion = getAccordion(basicFieldsDiv, get2TextLabels('QR Код: ', Number(qr?.code).toString(16)).innerText)

    codeDiv.appendChild(accordion)

    formViewBtn.addEventListener('click', () => {
        let formElement = document.getElementById('form_select')
        let formId = formElement.options[formElement.selectedIndex].id
        if (formId) {
            window.open(`?form=true&id=${formId}`, '_blank')
        }
    })

    let fieldsArray = []
    for (let field in fields) {
        let f = fields[field]
        let labelText = f?.name
        if (notEmptyOrUndefined(f?.caption)) {
            labelText = f?.caption
        }
        let isPublic = notEmptyOrUndefined(f?.isPublic) ? f?.isPublic : true
        if (notEmptyOrUndefined(isPublic) && !isPublic) {
            labelText = labelText.concat(' *')
        }
        let placeholder = f?.isStatic ? f?.placeholder : '';
        let input = getSingleInput(f?.fieldType, false, f?.id, placeholder)
        input.name = f?.name
        input.value = getDataFromForm(qr, f?.name)
        input.readOnly = edit === 'true' ? false : true

        fieldsArray.push({ name: labelText, input: input })
    }

    let fieldHeaders = [
        new TableHead('Название', '20%', 'name'),
        new TableHead('Значение', '80%', 'value')
    ]

    let tableFields = getTable(
        fieldHeaders, fieldsArray,
        'formFieldRow',
        'fields_table',
        'compact_table',
        null, null, ['input']
    )
    codeDiv.appendChild(tableFields)

    const filesDiv = document.createElement('div')
    renderCardFiles(filesDiv, qr)
    codeDiv.appendChild(filesDiv)

    const showOnPhoneBtn = getBigButton('Просмотр карточки', 'show_public_code_phone_btn');
    const saveBtn = getBigButton('Сохранить', 'save_code_btn');
    codeDiv.append(showOnPhoneBtn)
    codeDiv.append(saveBtn)

    return codeDiv
}

function renderCardFiles(parentDiv, qr) {
    parentDiv.innerHTML = ''
    let filesHeaders = [
        new TableHead('Название', '8%', 'name'),
        new TableHead('Оригинальное название', '10%', 'fileName'),
        new TableHead('Описание', '18%', 'description'),
        new TableHead('Размер', '7%', 'fileSize', formatBytes),
        new TableHead('Создан', '8%', 'created', formatDate),
        new TableHead('Публичный', '8%', 'isPublic', booleanToString)
    ]
    let fileItems = []

    let formFiles = qr?.form?.files
    if (formFiles) {
        for (let index in formFiles) {
            const file = formFiles[index];
            let actionCol = document.createElement('td')
            let openBtn = getBigButton('Скачать')
            openBtn.addEventListener('click', () => {
                qrApi().downloadFile(file?.id, file?.fileName)
            })
            let editBtn = getBigButton('Открыть')
            editBtn.addEventListener('click', () => {
                showEditFileModal(file?.id, true)
            })
            actionCol.append(editBtn, openBtn)
            file['actions'] = actionCol
            fileItems.push(file)
        }
    }
    let files = qr?.files
    for (let index in files) {
        const file = files[index];
        let actionCol = document.createElement('td')
        let openBtn = getBigButton('Скачать')
        openBtn.addEventListener('click', () => {
            qrApi().downloadFile(file?.id, file?.fileName)
        })
        let editBtn = getBigButton('Открыть')
        editBtn.addEventListener('click', () => {
            showEditFileModal(file?.id, true)
        })
        let removeBtn = getBigButton('Открепить')
        removeBtn.addEventListener('click', () => {
            removeBtn.parentElement.parentElement.remove()
        })
        actionCol.append(editBtn, openBtn, removeBtn)
        file['actions'] = actionCol
        fileItems.push(file)
    }
    let tableFiles = getComplexTable(
        filesHeaders,
        fileItems,
        'formFileRow',
        'files_table',
        'compact_table',
        'id',
        null, true
    )

    let addFileButton = document.createElement('button')
    addFileButton.className = 'custom_button'
    addFileButton.innerText = '+'
    addFileButton.addEventListener('click', () => {
        showUploadFileModal(
            () => {
                let name = document.getElementById('file_name')
                let description = document.getElementById('file_description')
                let file = document.getElementById('file_content')
                let isPublic = document.getElementById('file_public')
                let isActive = document.getElementById('file_active')

                try {
                    userApi().getSettings()
                        .then(resp => resp.json())
                        .then(settingsJson => {
                            qrApi().uploadQRFile(qr?.id, {
                                name: name.value,
                                description: description.value,
                                file: file,
                                public: isPublic.checked,
                                active: isActive.checked
                            }, settingsJson)
                        }).then(resp => alert('Файл успешно загружен!'))
                        .then(e => window.location.reload())
                        .catch(e => notify(e, 'Ошибка загрузки файла'))
                } catch (e) {
                    notify(ex)
                }
            }, true,
            () => {
                let fileSelect = document.getElementById('file_select')
                qrApi().connectFileToQR(qr?.id, fileSelect?.options[fileSelect?.selectedIndex]?.id)
                    .then(resp => {
                        if (!resp.ok) {
                            throw new Error('Ошибка при приклеплении файла. Возможно, такой файл уже прикреплен')
                        }
                        return resp.text()
                    })
                    .then(text => {
                        notify('Файл успешно загружен!')
                        window.location.reload()
                    })
                    .catch(ex => {
                        notify(ex)
                    })

            })
    })
    let fileTr = getTr(addFileButton, filesHeaders.length + 1)
    tableFiles.appendChild(fileTr)
    parentDiv.appendChild(getTextLabel('Файлы:'))
    parentDiv.appendChild(tableFiles)
}

function getViewCardInfoHtml(qr) {
    let cardDiv = document.createElement('div')
    cardDiv.className = 'code_block'

    let fields = qr?.form?.fields
    let data = qr?.data

    let fieldsHeaders = []
    let fieldsItems = []
    let domIndexes = []
    for (let index in fields) {
        const field = fields[index];
        let key = field?.name
        if (notEmptyOrUndefined(field?.caption)) {
            key = field?.caption
        }
        let isStaticField = field?.isStatic
        let fieldType = field?.fieldType?.toLowerCase()
        let value
        if (isStaticField) {
            value = (data === null || data[field?.name] === '' || data[field?.name] === undefined)
                ? field?.placeholder
                : data[field?.name]
        } else {
            let dataVal = data?.[field?.name]
            value = dataVal === undefined ? '' : dataVal
        }
        if (notEmptyOrUndefined(value) || isStaticField) {
            if (fieldType == 'url' || fieldType == 'email' || fieldType == 'phone') {
                domIndexes.push(fieldsItems.length.toString())
            }
            value = postProcessValue(value, fieldType)
            fieldsItems.push({
                key: key,
                value: value
            })
        }
    }
    let tableFields = getTable(
        fieldsHeaders,
        fieldsItems,
        'qrRows',
        'qrTable',
        'compact_table',
        null, false, domIndexes
    )

    if (fieldsItems.length > 0) {
        cardDiv.appendChild(tableFields)
    }

    let files = qr?.files

    if (qr?.form?.files != null) {
        files?.push(...qr?.form?.files)
    }

    let fileItems = []
    for (let index in files) {
        const file = files[index];
        fileItems.push({
            file: `<a target='_blank' href='${QR_DEMO_API}unsecured/files/${file?.id}/download/${file?.fileName}'>${file?.name}</a>`
        })
    }
    let tableFiles = getTable(
        [],
        fileItems,
        'fileRow',
        'files_table',
        'compact_table',
        null, true
    )

    if (fileItems.length > 0) {
        cardDiv.appendChild(tableFiles)
    }
    return cardDiv
}

let filesInputArray = []

function addCodeBtnListeners() {
    const saveCodeBtn = document.getElementById('save_code_btn')
    if (saveCodeBtn) {
        saveCodeBtn.addEventListener('click', () => {
            let nameElem = document.getElementById('name_input')
            let descriptionElem = document.getElementById('description_input')
            let formElem = document.getElementById('form_select')
            let actionElem = document.getElementById('action_select')
            let redirectElem = document.getElementById('redirect_input')

            const collectedFiles = Field.htmlToFilesFromComplexTable(document.getElementById('files_table'), isCardFile)
            const data = Field.htmlToFieldsFromTable(document.getElementById('fields_table'))
            qrApi().saveQr({
                id: qrId,
                code: qrCode,
                name: nameElem.value,
                description: descriptionElem.value,
                action: actionElem.value,
                redirect: redirectElem.value,
                formId: Number(formElem.options[formElem.selectedIndex].id),
                filesIds: collectedFiles.map(f => f.id),
                data: data
            }).then(resp => {
                if (resp.ok) {
                    alert('Карточка была обновлена!')
                    location.reload()
                } else {
                    alert(resp.json())
                }
            })
        })
    }

    const showPublicPhoneBtn = document.getElementById('show_public_code_phone_btn')
    if (showPublicPhoneBtn) {
        showPublicPhoneBtn.addEventListener('click', () => {
            let iframe = document.createElement('iframe')
            iframe.id = 'phone_iframe'
            iframe.src = `/qr?q=${Number(qrCode).toString(16)}`
            iframe.style.width = '436px'
            iframe.style.height = '567px'
            let modal = getModalWindow('Просмотр с телефона', iframe)
            modal.style.display = 'block'
            modal.firstChild.style.width = '440px'
            modal.firstChild.style.height = '622px'
        })
    }

    let actSelect = document.getElementById('action_select');
    actSelect.addEventListener('change', (e) => {
        let redirInp = document.getElementById('redirect_input')
        if (e.target.value === 'STANDARD') {
            redirInp.classList.add('color-grey')
            redirInp.readOnly = true
        } else {
            redirInp.classList.remove('color-grey')
            redirInp.readOnly = false
        }
    })
}

// TODO: change logic cardinally
function isCardFile(tableRow) {
    if (emptyOrUndefined(tableRow)) {
        return false
    }
    let lastChild = tableRow.lastChild
    if (emptyOrUndefined(lastChild.innerHTML) || lastChild?.children?.length == 2) {
        return false
    }
    return true
}

const processedTags = ['INPUT', 'TEXTAREA']

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

function addDownloadPublicFileRowActions() {
    let rows = document.getElementsByClassName('fileRow')
    for (let i = 0; i < rows.length; i++) {
        let row = rows[i]
        let fileId = row?.firstElementChild?.firstElementChild?.value
        if (fileId) {
            row.addEventListener('click', () => downloadFileUnsecured(fileId, null))
        }
    }
}

function postProcessValue(value, fieldType) {
    if (emptyOrUndefined(value) || emptyOrUndefined(fieldType)) {
        return ''
    }
    switch (fieldType.toLowerCase()) {
        case 'url': return getLink(value, value).outerHTML
        case 'phone': return getLink(value, `tel:${value}`).outerHTML
        case 'email': return getLink(value, `mailto:${value}`).outerHTML
        default: return value
    }
}
