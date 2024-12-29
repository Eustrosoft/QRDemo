import { fieldToHtml, formatBytes, getQRImageDiv, longToHex, toLoginIfNotAuthorized } from "./utils.js";
import { qrApi } from "./api.js";
import { getInput } from "./components/inputs.js";
import { getModalWindow } from "./components/modals.js";
import { getTable, getTr, TableHead } from "./components/tables.js";
import { getBigButton } from "./components/buttons.js";
import { downloadFileUnsecured, showUploadFileModal } from "./files.js";
import { addDeleteFileRowActions, Field } from "./form.js";
import { getTextLabel } from "./components/labels.js";

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
            let cardInfoHtml = edit
                ? getEditCardInfoHtml(json)
                : getViewCardInfoHtml(json)
            divCardInfo.className = 'code_info'

            // const qrImageDiv = getQRImageDiv(longToHex(json?.code));
            // divCardInfo.append(qrImageDiv)
            if (cardInfoHtml.innerHTML === null || cardInfoHtml.innerHTML === undefined || cardInfoHtml.innerHTML === '') {
                cardInfoHtml.innerHTML = '<h1>Нет информации для этой карточки</h1>'
            }
            divCardInfo.append(cardInfoHtml)
            div.appendChild(divCardInfo)
            if (edit) {
                addCodeBtnListeners()
                addDeleteFileRowActions()
            } else {
                addDownloadPublicFileRowActions()
            }
        })
        .catch(ex => alert(ex))
}

function getEditCardInfoHtml(qr) {
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

    let descriptionLabel = document.createElement('label')
    descriptionLabel.innerHTML = 'Описание:'
    let descriptionElem = document.createElement('input')
    descriptionElem.type = 'text'
    descriptionElem.id = 'description_input'
    descriptionElem.value = description
    descriptionElem.placeholder = 'Введите описание карточки'

    let formLabel = document.createElement('label')
    formLabel.innerHTML = 'Шаблон:'

    let formSelectDiv = document.createElement('div')
    formSelectDiv.className = 'flex'
    let formChooseElement = document.createElement('select')
    formChooseElement.id = 'form_select'
    let formViewBtn = getBigButton('Открыть шаблон')

    const opt = document.createElement('option')
    opt.value = ''
    opt.id = ''
    opt.innerHTML = ''
    formChooseElement.append(opt)

    qrApi().getAllForms().then(resp => resp.json())
        .then(json => {
            for (let i = 0; i < json.length; i++) {
                const opt = document.createElement('option')
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
    formSelectDiv.append(formChooseElement)
    formSelectDiv.append(formViewBtn)
    codeDiv.appendChild(formSelectDiv)

    formViewBtn.addEventListener('click', () => {
        let formElement = document.getElementById('form_select')
        let formId = formElement.options[formElement.selectedIndex].id
        if (formId) {
            window.open(`?form=true&id=${formId}`, '_')
        }
    })

    codeDiv.appendChild(nameLabel)
    codeDiv.appendChild(nameElem)
    codeDiv.appendChild(descriptionLabel)
    codeDiv.appendChild(descriptionElem)

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

    let filesHeaders = [
        new TableHead('Название', '10%'), new TableHead('Оригинальное название', '10%'),
        new TableHead('Описание', '20%'), new TableHead('Размер', '9%'),
        new TableHead('Создан', '8%'), new TableHead('Публичный', '8%'),
        new TableHead('Удалить', '8%')
    ]
    let files = qr?.files

    let fileItems = []
    for (let index in files) {
        const file = files[index];
        fileItems.push({
            name: `<input name="id" type="hidden" value="${file?.id}"/>` + file?.name,
            fileName: file?.fileName,
            description: file?.description,
            fileSize: formatBytes(file?.fileSize),
            created: file?.created,
            isPublic: file?.isPublic,
            actions: `<button class="big_button" id="delete_file_btn_${index}">X</button>`
        })
    }
    let tableFiles = getTable(
        filesHeaders,
        fileItems,
        'formFileRow',
        'files_table'
    )

    let addFileButton = document.createElement('button')
    addFileButton.className = 'custom_button'
    addFileButton.innerText = '+'
    addFileButton.addEventListener('click', () => {
        showUploadFileModal(() => {
            let name = document.getElementById('file_name')
            let description = document.getElementById('file_description')
            let file = document.getElementById('file_content')
            let isPublic = document.getElementById('file_public')

            try {
                qrApi().uploadQRFile(qr?.id, { name: name.value, description: description.value, file: file, public: isPublic.checked })
                alert('Файл успешно загружен!')
                window.location.reload()
            } catch (e) {
                alert(e)
            }
        })
    })
    let fileTr = getTr(addFileButton, filesHeaders.length)
    tableFiles.appendChild(fileTr)
    codeDiv.appendChild(tableFiles)

    const showOnPhoneBtn = document.createElement('button');
    showOnPhoneBtn.innerHTML = 'Просмотр карточки'
    showOnPhoneBtn.className = 'big_button'
    showOnPhoneBtn.id = 'show_public_code_phone_btn'
    showOnPhoneBtn.style = 'width: 97%'
    codeDiv.append(showOnPhoneBtn)

    const saveBtn = document.createElement('input');
    saveBtn.value = 'Сохранить'
    saveBtn.type = 'submit'
    saveBtn.className = 'big_button'
    saveBtn.id = 'save_code_btn'
    codeDiv.append(saveBtn)

    return codeDiv
}

function getViewCardInfoHtml(qr) {
    let cardDiv = document.createElement('div')
    cardDiv.className = 'code_block'

    let fields = qr?.form?.fields
    let data = qr?.data

    let fieldsHeaders = []
    let fieldsItems = []
    for (let index in fields) {
        const field = fields[index];
        let key = field?.name
        let value = (data === null || data[field?.name] === '' || data[field?.name] === undefined)
            ? field?.placeholder
            : data[field?.name]
        fieldsItems.push({
            key: key,
            value: value
        })
    }
    let tableFields = getTable(
        fieldsHeaders,
        fieldsItems,
        'qrRows',
        'qrTable'
    )

    if (fieldsItems.length > 0) {
        cardDiv.appendChild(tableFields)
    }

    let filesHeaders = [
        new TableHead('Название', '50%'),
        new TableHead('Скачать', '50%')
    ]
    let files = qr?.files

    if (qr?.form?.files != null) {
        files?.push(...qr?.form?.files)
    }

    let fileItems = []
    for (let index in files) {
        const file = files[index];
        fileItems.push({
            name: `<input name="id" type="hidden" value="${file?.id}"/>` + file?.name,
            actions: `<button class="big_button" id="download_file_btn_${index}">Скачать</button>`
        })
    }
    let tableFiles = getTable(
        filesHeaders,
        fileItems,
        'formFileRow',
        'files_table'
    )

    if (fileItems.length > 0) {
        cardDiv.appendChild(getTextLabel('Файлы:'))
        cardDiv.appendChild(tableFiles)
    }

    return cardDiv
}

let filesInputArray = []

function addCodeBtnListeners() {
    const saveCodeBtn = document.getElementById('save_code_btn');
    if (saveCodeBtn) {
        saveCodeBtn.addEventListener('click', () => {
            let nameElem = document.getElementById('name_input')
            let descriptionElem = document.getElementById('description_input')
            let formElement = document.getElementById('form_select')

            const collectedFiles = Field.htmlToFiles(document.getElementById('files_table'));
            const data = collectFormData();
            qrApi().saveQr({
                id: qrId,
                code: qrCode,
                name: nameElem.value,
                description: descriptionElem.value,
                form: { id: formElement.options[formElement.selectedIndex].id },
                files: collectedFiles,
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

    const showPublicPhoneBtn = document.getElementById('show_public_code_phone_btn')
    if (showPublicPhoneBtn) {
        showPublicPhoneBtn.addEventListener('click', () => {
            let iframe = document.createElement('iframe')
            iframe.id = 'phone_iframe'
            iframe.src = `?q=${Number(qrCode).toString(16)}`
            iframe.style.width = '436px'
            iframe.style.height = '567px'
            let modal = getModalWindow('Просмотр с телефона', iframe)
            modal.style.display = 'block'
            modal.firstChild.style.width = '440px'
            modal.firstChild.style.height = '622px'
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

function addDownloadPublicFileRowActions() {
    let rows = document.getElementsByClassName('formFileRow')
    for (let i = 0; i < rows.length; i++) {
        let fileId = rows[i]?.firstElementChild?.firstElementChild?.value // TODO
        let elem = document.getElementById(`download_file_btn_${i}`)
        if (elem) {
            elem.addEventListener('click', () => downloadFileUnsecured(fileId))
        }
    }
}
