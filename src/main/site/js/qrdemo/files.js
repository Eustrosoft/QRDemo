import { fieldToHtmlItems, formatBytes, toLoginIfNotAuthorized } from "./utils.js";
import { qrApi } from "./api.js";
import { getInput } from "./components/inputs.js";
import { getTable, TableHead } from "./components/tables.js";
import { getModalWindow } from "./components/modals.js";
import { getBigButton } from "./components/buttons.js";
import { notEmptyOrUndefined } from "../commons/common.js";

export function setFiles(formId) {
    toLoginIfNotAuthorized()
        .then(x => init())
        .catch(ex => alert(ex))
}

function init() {
    document.title = `QRDemo - My Files`

    const mainBlock = document.getElementById('main_block')

    mainBlock.innerHTML = getStartPage()
    setStartActions()

    qrApi().getAllFiles().then(resp => resp.json())
        .then(json => printFilesList(mainBlock, json))
        .catch(ex => alert(ex))
}

function printFilesList(parent, json) {
    let tableForms = document.createElement('table')
    parent.appendChild(tableForms)

    let tableHeaderRow = document.createElement('tr')
    tableHeaderRow.innerHTML = `<th>Название</th><th>Оригинальное название</th><th>Описание</th><th>Создан</th><th>Размер</th><th>Действия</th>`
    tableForms.appendChild(tableHeaderRow)

    for (let i = 0; i < json.length; i++) {
        const deleteBtnId = `file_delete_btn_${json[i]?.id}`;
        const downloadBtnId = `file_download_btn_${json[i]?.id}`;
        const openBtnId = `file_open_btn_${json[i]?.id}`;
        let tableRow = document.createElement('tr')

        tableRow.innerHTML =
        `<tr>
               <td>${json[i]?.name}</td>
               <td>${json[i]?.fileName}</td>
               <td>${json[i]?.description}</td>
               <td>${new Date(json[i]?.created).toLocaleString()}</td>
               <td>${formatBytes(json[i]?.fileSize)}</td>
               <td>
                    <button id="${deleteBtnId}" class="big_button fs-18rem">Удалить</button>
                    <button id="${downloadBtnId}" class="big_button fs-18rem">Скачать</button>
                    <button id="${openBtnId}" class="big_button fs-18rem">Открыть</button>
               </td>
        </tr>`

        tableForms.appendChild(tableRow)
        document.getElementById(deleteBtnId).addEventListener('click', () => {
            deleteFile(json[i].id)
        })
        document.getElementById(downloadBtnId).addEventListener('click', () => {
            downloadFile(json[i].id)
        })
        document.getElementById(openBtnId).addEventListener('click', () => {
            openFile(json[i].id)
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

export function openFile(id) {
    qrApi().openFile(id)
}

export function downloadFile(id) {
    qrApi().downloadFile(id)
}

export function downloadFileUnsecured(id) {
    qrApi().downloadFileUnsecured(id)
}

function getStartPage() {
    return `
        <div class="toolbar">
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
