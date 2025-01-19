import { formatBytes } from "./utils.js";
import { qrApi } from "./api.js";
import { getInput } from "./components/inputs.js";
import { getModalWindow } from "./components/modals.js";
import { getBigButton } from "./components/buttons.js";
import { notEmptyOrUndefined } from "../commons/common.js";
import { getNavigationMenu } from "./components/blocks.js";

export function setFiles(filesParam) {
    init()
}

function init() {
    let files = qrApi().getAllFiles()
    document.title = `QRDemo - My Files`

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
    tableHeaderRow.innerHTML = `<th>Название</th><th>Оригинальное название</th><th>Описание</th><th>Создан</th><th>Размер</th><th>Публичный</th><th>Действия</th>`
    tableForms.appendChild(tableHeaderRow)

    for (let i = 0; i < json.length; i++) {
        const deleteBtnId = `file_delete_btn_${json[i]?.id}`;
        const downloadBtnId = `file_download_btn_${json[i]?.id}`;
        const editBtnId = `file_edit_btn_${json[i]?.id}`;
        let tableRow = document.createElement('tr')

        tableRow.innerHTML =
        `<tr>
               <td>${json[i]?.name}</td>
               <td>${json[i]?.fileName}</td>
               <td>${json[i]?.description}</td>
               <td>${new Date(json[i]?.created).toLocaleString()}</td>
               <td>${formatBytes(json[i]?.fileSize)}</td>
               <td>${json[i]?.isPublic}</td>
               <td>
                    <button id="${deleteBtnId}" class="big_button fs-18rem">Удалить</button>
                    <button id="${downloadBtnId}" class="big_button fs-18rem">Скачать</button>
                    <button id="${editBtnId}" class="big_button fs-18rem">Редактировать</button>
               </td>
        </tr>`

        tableForms.appendChild(tableRow)
        document.getElementById(deleteBtnId).addEventListener('click', () => {
            deleteFile(json[i]?.id)
        })
        document.getElementById(downloadBtnId).addEventListener('click', () => {
            qrApi().downloadFile(json[i]?.id)
        })
        document.getElementById(editBtnId).addEventListener('click', () => {
            qrApi().getById(json[i]?.id)
                .then(resp => resp.json())
                .then(json => {
                    let fileEditForm = document.createElement('div')

                    let fileNameInput = getInput('Имя файла', 'text', true, 'file_edit_name', 'Введите имя', false, 'off', json?.name)
                    let fileDescriptionInput = getInput('Описание файла', 'text', false, 'file_edit_description', 'Введите описание', false, 'off', json?.description)
                    let isPublicInput = getInput('Публичный', 'checkbox', true, 'file_edit_public', '', true, 'off', json?.isPublic)
                    let isActiveInput = getInput('Доступный', 'checkbox', true, 'file_edit_active', '', true, 'off', json?.isActive)
                    let nameInput = fileNameInput.getElementsByTagName('input')[0];
                    nameInput.maxLength = '127'
                    let descriptionInput = fileDescriptionInput.getElementsByTagName('input')[0];
                    descriptionInput.maxLength = '511'
                    let updateBtn = getBigButton('Обновить')
                
                    fileEditForm.append(fileNameInput, fileDescriptionInput, isPublicInput, isActiveInput, updateBtn)
                    let modal = getModalWindow('Редактирование файла', fileEditForm)
                    modal.style.display = 'block'

                    updateBtn.addEventListener('click', () => {
                        qrApi().updateFile(
                            json?.id,
                            {
                                name: document.getElementById('file_edit_name')?.value,
                                description: document.getElementById('file_edit_description')?.value,
                                isPublic: document.getElementById('file_edit_public')?.checked,
                                isActive: document.getElementById('file_edit_active')?.checked
                            }
                        ).then(resp => {
                            if (resp.ok)
                                return resp.json
                            throw new Error('Неизвестная ошибка')
                        }).then(json => {
                            alert('Файл был обновлен')
                            window.location.reload()
                        }).catch(ex => alert('Неизвестная ошибка при обновлении файла'))
                    })
                })
        })
    }
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

export function downloadFileUnsecured(id) {
    qrApi().downloadFileUnsecured(id)
}

function getStartPage() {
    return `
        <div class="account_card_buttons">
            <button class="big_button" id="upload_file_btn">
                    Загрузить новый файл
            </button>
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

            try {
                qrApi().uploadFile({ name: name.value, description: description.value, file: file, public: isPublic.checked })
                alert('Файл успешно загружен!')
                window.location.reload()
            } catch (e) {
                alert(e)
            }
        })
    })
}

export function showUploadFileModal(uploadFileCallback, closeOnComplete = true, closeOnError = false) {
    let fileUploadForm = document.createElement('div')

    let fileNameInput = getInput('Имя файла', 'text', true, 'file_name')
    let fileDescriptionInput = getInput('Описание файла', 'text', false, 'file_description')
    let isPublicInput = getInput('Публичный', 'checkbox', true, 'file_public', '', true)
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

    fileUploadForm.append(fileNameInput, fileDescriptionInput, isPublicInput, fileInput, uploadFileBtn)

    let modal = getModalWindow('Загрузка файла', fileUploadForm)
    modal.style.display = 'block'

    uploadFileBtn.addEventListener('click', () => {
        let fileName = document.getElementById('file_name')
        let file = document.getElementById('file_content')
        if (fileName && notEmptyOrUndefined(fileName.value) && file && file.files[0] != null) {
            try {
                uploadFileCallback()
                if (closeOnComplete) {
                    modal.remove()
                }
            } catch(e) {
                if (closeOnError) {
                    modal.remove()
                }
            }
        } else {
            alert('Необходимо ввести имя файла и выбрать файл')
        }
    })
}
