import { formatBytes } from "./utils.js";
import { QR_DEMO_API, qrApi } from "./api.js";
import { getInput } from "./components/inputs.js";
import { getModalWindow } from "./components/modals.js";
import { getBigButton } from "./components/buttons.js";
import { copyToClipboard, getOrOther, notEmptyOrUndefined } from "../commons/common.js";
import { getNavigationMenu } from "./components/blocks.js";
import { getTextLabel } from "./components/labels.js";
import { notify } from "./notifications.js";

let fileSelection

export function setFiles(filesParam) {
    init()
}

function init() {
    let files = qrApi().getAllFiles()
    document.title = `QRDemo - My Files`

    fileSelection = window.location.hash.substring(1)

    const mainBlock = document.getElementById('main_block')
    mainBlock.prepend(getNavigationMenu())
    const basicCard = document.createElement('div')
    basicCard.className = 'basic_card'
    mainBlock.appendChild(basicCard)

    basicCard.innerHTML = getStartPage()
    setStartActions()

    files.then(resp => resp.json())
        .then(json => printFilesList(basicCard, json))
}

function printFilesList(parent, json) {
    let tableForms = document.createElement('table')
    tableForms.className = 'compact_table'
    parent.appendChild(tableForms)

    let tableHeaderRow = document.createElement('tr')
    tableHeaderRow.innerHTML = `
        <th>Название</th><th>Оригинальное название</th><th>Описание</th>
        <th>Создан</th><th>Размер</th><th>Публичный</th><th>Место</th><th>Действия</th>
    `
    tableForms.appendChild(tableHeaderRow)

    for (let i = 0; i < json.length; i++) {
        const deleteBtnId = `file_delete_btn_${json[i]?.id}`;
        const downloadBtnId = `file_download_btn_${json[i]?.id}`;
        const editBtnId = `file_edit_btn_${json[i]?.id}`;
        let tableRow = document.createElement('tr')
        tableRow.id = `tr-file-id-${json[i]?.id}`

        let td1 = document.createElement('td')
        td1.innerText = getOrOther(json[i]?.name, '')
        let td2 = document.createElement('td')
        td2.innerText = getOrOther(json[i]?.fileName, '')
        let td3 = document.createElement('td')
        td3.innerText = getOrOther(json[i]?.description, '')
        let td4 = document.createElement('td')
        td4.innerText = new Date(json[i]?.created).toLocaleString()
        let td5 = document.createElement('td')
        td5.innerText = formatBytes(json[i]?.fileSize)
        let td6 = document.createElement('td')
        td6.innerText = json[i]?.isPublic
        let td7 = document.createElement('td')
        td7.innerText = json[i]?.fileStorageType
        let td8 = document.createElement('td')
        let editBtn = getBigButton('Открыть', editBtnId)
        let downloadBtn = getBigButton('Скачать', downloadBtnId)
        let deleteBtn = getBigButton('Удалить', deleteBtnId)
        td8.append(editBtn, downloadBtn, deleteBtn)
        tableRow.append(td1, td2, td3, td4, td5, td6, td7, td8)

        tableForms.appendChild(tableRow)
        document.getElementById(deleteBtnId).addEventListener('click', () => {
            deleteFile(json[i]?.id)
        })
        document.getElementById(downloadBtnId).addEventListener('click', () => {
            if (json[i]?.fileStorageType === 'URL') {
                window.open(json[i]?.storagePath, '_blank')
            } else {
                qrApi().downloadFile(json[i]?.id, json[i]?.fileName)
            }
        })
        document.getElementById(editBtnId).addEventListener('click', () => showEditFileModal(json[i]?.id))

    }
    if (notEmptyOrUndefined(fileSelection)) {
        let desiredRow = document.getElementById(`tr-file-id-${fileSelection}`)
        desiredRow.scrollIntoView({
            behavior: 'smooth',
            block: 'center'
        })
        desiredRow.classList.add('blue_pulse')
        setTimeout(function () {
            desiredRow.classList.remove('blue_pulse')
        }, 3_000);
    }
}

export function showEditFileModal(id, reloadAfterEdit = true) {
    qrApi().getById(id)
        .then(resp => resp.json())
        .then(json => {
            let fileEditForm = document.createElement('div')

            let fileNameInput = getInput('Название файла', 'text', true, 'file_edit_name', 'Введите имя', false, 'off', json?.name)
            let fileDescriptionInput = getInput('Описание файла', 'text', false, 'file_edit_description', 'Введите описание', false, 'off', json?.description)
            let isPublicInput = getInput('Публичный', 'checkbox', true, 'file_edit_public', '', true, 'off', json?.isPublic)
            let isActiveInput = getInput('Доступный', 'checkbox', true, 'file_edit_active', '', true, 'off', json?.isActive)
            let nameInput = fileNameInput.getElementsByTagName('input')[0];
            nameInput.maxLength = '127'
            let descriptionInput = fileDescriptionInput.getElementsByTagName('input')[0];
            descriptionInput.maxLength = '511'
            let updateBtn = getBigButton('Обновить')
            let copyBtn = getBigButton('Скопировать ссылку')
            let changeFileBtn = getBigButton('Заменить файл')

            fileEditForm.append(fileNameInput, fileDescriptionInput)
            if (json?.fileStorageType === 'URL') {
                let fileStoragePath = getInput('Ссылка на файл', 'text', false, 'file_edit_link', 'Введите ссылку на файл', false, 'off', json?.storagePath)
                fileEditForm.append(fileStoragePath)
            }

            fileEditForm.append(isPublicInput, isActiveInput, updateBtn, copyBtn, changeFileBtn)
            let modal = getModalWindow('Редактирование файла', fileEditForm)
            modal.style.display = 'block'

            updateBtn.addEventListener('click', () => {
                qrApi().updateFile(
                    json?.id,
                    {
                        name: document.getElementById('file_edit_name')?.value,
                        description: document.getElementById('file_edit_description')?.value,
                        storagePath: document.getElementById('file_edit_link')?.value,
                        isPublic: document.getElementById('file_edit_public')?.checked,
                        isActive: document.getElementById('file_edit_active')?.checked
                    }
                ).then(resp => {
                    if (resp.ok)
                        return resp.json
                    throw new Error('Неизвестная ошибка')
                }).then(json => {
                    alert('Файл был обновлен')
                    if (reloadAfterEdit) {
                        window.location.reload()
                    }
                }).catch(ex => alert('Неизвестная ошибка при обновлении файла'))
            })

            copyBtn.addEventListener('click', () => {
                let linkRef
                if (json?.fileStorageType === 'URL') {
                    linkRef = json?.storagePath
                } else {
                    linkRef = `${QR_DEMO_API}unsecured/files/${json?.id}/download/${json?.fileName}`
                }
                if (linkRef) {
                    if (notEmptyOrUndefined(linkRef)) {
                        copyToClipboard(linkRef)
                        copyBtn.classList.add('green_pulse')
                        setTimeout(() => {
                            copyBtn.classList.remove('green_pulse')
                        }, 1_000)
                        return
                    }
                    notify('Нечего копировать')
                }
            })

            changeFileBtn.addEventListener('click', () => {
                let fileChangeDiv = document.createElement('div')
                let fileInput = getInput('Файл', 'file', true, 'file_content', '', true)
                let uploadBtn = getBigButton('Загрузить')
                fileChangeDiv.append(fileInput, uploadBtn)
                uploadBtn.addEventListener('click', () => {
                    try {
                        qrApi().reuploadFile(json?.id, { file: document.getElementById('file_content') })
                    } catch (e) {
                        alert(e)
                    }
                })
                let modal = getModalWindow('Замена файла', fileChangeDiv)
                modal.style.display = 'block'
            })
        })
}

export function deleteFile(id) {
    const toDelete = confirm("Уверены, что хотите удалить файл?")
    if (toDelete) {
        qrApi().deleteFile(id)
            .then(resp => resp.ok)
            .then(ok => location.reload())
            .catch(ex => alert(ex))
    }
}

export function downloadFileUnsecured(id, fileName) {
    qrApi().downloadFileUnsecured(id, fileName)
}

function getStartPage() {
    // TODO: create html elements, not static html text
    return `
        <div class="account_card_buttons">
            <button class="big_button" id="upload_file_btn">Загрузить новый файл</button>
            <button class="big_button" id="link_file_btn">Добавить ссылку на файл</button>
        </div>
    
    `
}

function setStartActions() {
    document.getElementById('upload_file_btn')
        .addEventListener('click', () => {
            showUploadFileModal(() => {
                let name = document.getElementById('file_name')
                let description = document.getElementById('file_description')
                let file = document.getElementById('file_content')
                let isPublic = document.getElementById('file_public')
                let isActive = document.getElementById('file_active')

                try {
                    qrApi().uploadFile({
                        name: name.value,
                        description: description.value,
                        file: file,
                        public: isPublic.checked,
                        active: isActive.checked
                    })
                } catch (e) {
                    notify(e, 'Ошибка обработки файла')
                }
            })
        })
    document.getElementById('link_file_btn')
        .addEventListener('click', () => {
            showLinkFileModal(() => {
                let name = document.getElementById('file_name')
                let description = document.getElementById('file_description')
                let fileLink = document.getElementById('file_link')
                let isPublic = document.getElementById('file_public')
                let isActive = document.getElementById('file_active')

                try {
                    qrApi().uploadFile({
                        name: name.value,
                        description: description.value,
                        storagePath: fileLink?.value,
                        fileStorageType: 'URL',
                        public: isPublic.checked,
                        active: isActive.checked
                    })
                } catch (e) {
                    notify('Ошибка обработки ссылки', e)
                }
            })
        })
}

export function showUploadFileModal(uploadFileCallback, chooseUploadOption = false, chooseUploadFileCallback, closeOnComplete = true, closeOnError = false) {
    let fileUploadForm = document.createElement('div')
    let fileUploadNewWindow = document.createElement('div')

    if (chooseUploadOption) {
        fileUploadNewWindow.style.display = 'none'
        let fileChooseOldWindow = document.createElement('div')
        fileChooseOldWindow.style.display = 'none'

        let fileChooseOrUploadWindow = document.createElement('div')
        let chooseOldFileBtn = getBigButton('Выбрать загруженный')
        let textLabel = getTextLabel('или')
        let uploadNewFileBtn = getBigButton('Загрузить новый')
        chooseOldFileBtn.addEventListener('click', () => {
            fileChooseOrUploadWindow.style.display = 'none'
            fileChooseOldWindow.style.display = 'block'

            let fileSelectBtn = getBigButton('Выбрать')
            if (notEmptyOrUndefined(chooseUploadFileCallback)) {
                fileSelectBtn.addEventListener('click', () => {
                    try {
                        chooseUploadFileCallback()
                        if (closeOnComplete) {
                            modal.remove()
                        }
                    } catch (e) {
                        if (closeOnError) {
                            modal.remove()
                        }
                        notify(e, 'Ошибка обработки файла')
                    }
                })
            }
            fileChooseOldWindow.append(getFileChooseSelect(), fileSelectBtn)
        })
        uploadNewFileBtn.addEventListener('click', () => {
            fileUploadNewWindow.style.display = 'block'
            fileChooseOrUploadWindow.style.display = 'none'
        })
        fileChooseOrUploadWindow.append(chooseOldFileBtn, textLabel, uploadNewFileBtn)
        fileUploadForm.append(fileChooseOrUploadWindow, fileChooseOldWindow)
    }

    let fileNameInput = getInput('Название файла', 'text', true, 'file_name')
    let fileDescriptionInput = getInput('Описание файла', 'text', false, 'file_description')
    let isPublicInput = getInput('Публичный', 'checkbox', true, 'file_public', '', true)
    let isActiveInput = getInput('Доступный', 'checkbox', true, 'file_active', '', true)
    let fileInput = getInput('Файл', 'file', true, 'file_content', '', true)
    let uploadFileBtn = getBigButton('Загрузить')

    let nameInput = fileNameInput.getElementsByTagName('input')[0];
    nameInput.maxLength = '127'

    let descriptionInput = fileDescriptionInput.getElementsByTagName('input')[0];
    descriptionInput.maxLength = '511'

    let publicInput = isPublicInput.getElementsByTagName('input')[0];
    publicInput.checked = true
    publicInput.style.width = '11px'
    publicInput.style.height = '11px'

    let activeInput = isActiveInput.getElementsByTagName('input')[0];
    activeInput.checked = true
    activeInput.style.width = '11px'
    activeInput.style.height = '11px'

    fileUploadNewWindow.append(fileNameInput, fileDescriptionInput, isPublicInput, isActiveInput, fileInput, uploadFileBtn)
    fileUploadForm.append(fileUploadNewWindow)
    let modal = getModalWindow('Загрузка файла', fileUploadForm)
    modal.style.display = 'block'

    let fileInpElem = document.getElementById('file_content')
    let fileNameInpElem = document.getElementById('file_name')
    if (fileInpElem && fileNameInpElem) {
        fileInpElem.addEventListener('change', (e) => {
            try {
                fileNameInpElem.value = e.target.files[0].name
                fileNameInpElem.select()
            } catch (e) {
                console.log(e)
            }
        })
    }

    uploadFileBtn.addEventListener('click', () => {
        let fileName = document.getElementById('file_name')
        let file = document.getElementById('file_content')
        if (fileName && notEmptyOrUndefined(fileName.value) && file && file.files[0] != null) {
            try {
                uploadFileCallback()
                if (closeOnComplete) {
                    modal.remove()
                }
            } catch (e) {
                if (closeOnError) {
                    modal.remove()
                }
                notify(e, 'Ошибка обработки файла')
            }
        } else {
            notify('Необходимо ввести название файла и выбрать файл', 'Ошибка')
        }
    })
}

function getFileChooseSelect() {
    let fileLabel = getTextLabel('Файл: ')

    let fileSelectDiv = document.createElement('div')
    let fileSelectElement = document.createElement('select')
    fileSelectElement.id = 'file_select'
    fileSelectElement.style.width = '100%'
    fileSelectElement.style.height = '100%'
    fileSelectElement.style.fontSize = '1.2em'

    const opt = document.createElement('option')
    opt.value = ''
    opt.id = ''
    opt.innerText = ''
    fileSelectElement.append(opt)

    qrApi().getAllFiles()
        .then(resp => resp.json())
        .then(json => {
            for (let i = 0; i < json.length; i++) {
                const opt = document.createElement('option')
                opt.value = json[i]?.name
                opt.id = json[i]?.id
                opt.innerText = `${json[i]?.name} (${json[i]?.description}) [${json[i]?.fileName}]`
                fileSelectElement.append(opt)
            }
        })
    fileSelectDiv.append(fileLabel, fileSelectElement)
    return fileSelectDiv
}

export function showLinkFileModal(uploadFileCallback, closeOnComplete = true, closeOnError = false) {
    let fileLinkForm = document.createElement('div')
    let fileLinkNewWindow = document.createElement('div')

    let fileNameInput = getInput('Название файла', 'text', true, 'file_name')
    let fileDescriptionInput = getInput('Описание файла', 'text', false, 'file_description')
    let isPublicInput = getInput('Публичный', 'checkbox', true, 'file_public', '', true)
    let isActiveInput = getInput('Доступный', 'checkbox', true, 'file_active', '', true)
    let fileInput = getInput('Ссылка на файл', 'text', true, 'file_link', '', true)
    let addFileLinkBtn = getBigButton('Создать')

    let nameInput = fileNameInput.getElementsByTagName('input')[0];
    nameInput.maxLength = '127'
    let descriptionInput = fileDescriptionInput.getElementsByTagName('input')[0];
    descriptionInput.maxLength = '511'
    let publicInput = isPublicInput.getElementsByTagName('input')[0];
    publicInput.checked = true
    publicInput.style.width = '11px'
    publicInput.style.height = '11px'
    let activeInput = isActiveInput.getElementsByTagName('input')[0];
    activeInput.checked = true
    activeInput.style.width = '11px'
    activeInput.style.height = '11px'

    fileLinkNewWindow.append(fileNameInput, fileDescriptionInput, isPublicInput, isActiveInput, fileInput, addFileLinkBtn)
    fileLinkForm.append(fileLinkNewWindow)
    let modal = getModalWindow('Привязка ссылки на файл', fileLinkForm)
    modal.style.display = 'block'

    let fileLink = document.getElementById('file_link')
    let fileName = document.getElementById('file_name')

    if (fileLink && fileName) {
        fileLink.addEventListener('input', (e) => {
            try {
                fileName.value = e.target?.value?.split("/").at(-1)
                fileName.select()
            } catch (e) {
                console.log(e)
            }
        })
    }

    addFileLinkBtn.addEventListener('click', () => {
        let fileLink = document.getElementById('file_link')
        let fileName = document.getElementById('file_name')
        if (fileName && notEmptyOrUndefined(fileName.value) && fileLink && notEmptyOrUndefined(fileLink.value)) {
            try {
                uploadFileCallback()
                if (closeOnComplete) {
                    modal.remove()
                }
            } catch (e) {
                if (closeOnError) {
                    modal.remove()
                }
                notify(e, 'Ошибка обработки ссылки')
            }
        } else {
            notify('Необходимо добавить имя и ввести ссылку', 'Ошибка')
        }
    })
}
