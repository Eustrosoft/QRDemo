import { emptyOrUndefined, fieldToHtmlItems, formatBytes, setQueryParamsAndRefresh, toLoginIfNotAuthorized } from "./utils.js";
import { dictionaryApi, qrApi } from "./api.js";
import { notEmptyOrUndefined } from "../commons/common.js";
import { getTextLabel } from "./components/labels.js";
import { getInput } from "./components/inputs.js";
import { DICTIONARIES } from "./domain/dictionaries.js";
import { getFileScrollComponent } from "./components/fileScroll.js";
import { getTable, getTr, TableHead } from "./components/tables.js";
import { downloadFile, showUploadFileModal } from "./files.js";

var formFields = []
var fieldTypes = []
var formId = ''
var lastElementIndex = 0

export function setForm(formId) {
    toLoginIfNotAuthorized()
        .then(x => {
            dictionaryApi().getDictionariesByCode(DICTIONARIES.INPUT_TYPES)
                .then(resp => resp.json())
                .then(json => fieldTypes = json)
                .then(x => init(formId))
                .catch(ex => alert(ex))
        })
}

function init(formId) {
    document.title = `QRDemo - Form ${formId}`
    formFields = []

    const mainBlock = document.getElementById('main_block')

    mainBlock.innerHTML = getStartPage()
    setStartActions()

    const urlParams = new URLSearchParams(window.location.search)
    const create = urlParams.get('create')
    const id = urlParams.get('id')

    if (create || id) {
        mainBlock.appendChild(getTextLabel('Название шаблона'))
        mainBlock.appendChild(getInput(null, 'text', false, 'formName', 'Шаблон без имени'))
        mainBlock.appendChild(getTextLabel('Описание шаблона'))
        mainBlock.appendChild(getInput(null, 'text', false, 'formDescription', 'Шаблон без описания'))

        let formDiv = document.createElement('div')
        mainBlock.appendChild(formDiv)

        let formName = document.getElementById('formName')
        let formDescription = document.getElementById('formDescription')

        let saveFormBtn = document.createElement('button')
        saveFormBtn.className = 'custom_button'
        saveFormBtn.innerText = 'Сохранить'
        saveFormBtn.addEventListener('click', () => {
            if (formName.value === null || formName.value === undefined || formName.value === '') {
                alert("Название шаблона не может быть пустое")
                return
            }
            const collectedFields = Field.htmlToFields(formDiv);
            const collectedFiles = Field.htmlToFiles(formDiv);

            if (create) {
                qrApi().saveForm(Field.fieldsToSaveForm(formName.value, formDescription.value, collectedFields, collectedFiles))
                    .then(resp => {
                        if (resp.ok) {
                            return resp.json()
                        }
                        throw new Error('Ошибка при сохранении')
                    })
                    .then(json => {
                        setQueryParamsAndRefresh([{ name: 'form', value: true }, { name: 'id', value: json.id }])
                    })
                    .then(() => alert('Шаблон был создан!'))
                    .catch(() => alert('Ошибка при создании шаблона'))
            }
            if (id !== null) {
                qrApi().updateForm(Field.fieldsToSaveForm(formName.value, formDescription.value, collectedFields, collectedFiles, formId))
                    .then((resp) => {
                        if (resp.ok) {
                            alert('Шаблон был обновлен!')
                            window.location.reload()
                        } else {
                            throw Error('unexpected error')
                        }
                    })
                    .catch(() => {
                        alert('Ошибка при обновлении шаблона, проверьте одинаковые поля')
                    })
            }
        })
        mainBlock.appendChild(saveFormBtn)

        if (id) {
            qrApi().getFormById(id)
                .then(resp => resp.json())
                .then(json => {
                    formId = json.id
                    formName.value = json.name
                    formDescription.value = json.description

                    const fields = json?.fields;
                    if (notEmptyOrUndefined(fields)) {
                        for (let j = 0; j < fields.length; j++) {
                            formFields.push(fields[j])
                        }
                    }
                    formFields = Field.sortFields(formFields)
                    renderForm(formDiv, formFields, json)
                })


        }
    } else {
        qrApi().getAllForms().then(resp => resp.json())
            .then(json => printFormsList(mainBlock, json))
            .catch(ex => alert(ex))
    }
}

function printFormsList(parent, json) {
    let tableForms = document.createElement('table')
    parent.appendChild(tableForms)

    let tableHeaderRow = document.createElement('tr')
    tableHeaderRow.innerHTML = `<th>Название</th><th>Описание</th><th>Создана</th><th>Обновлена</th><th>Действия</th>`
    tableForms.appendChild(tableHeaderRow)

    for (let i = 0; i < json.length; i++) {
        const getBtnId = `form_get_btn_${json[i].id}`;
        const deleteBtnId = `form_delete_btn_${json[i].id}`;
        let tableRow = document.createElement('tr')
        tableRow.innerHTML =
            `<tr>
                   <td>${json[i].name}</td>
                   <td>${json[i].description}</td>
                   <td>${new Date(json[i].created).toLocaleString()}</td>
                   <td>${new Date(json[i].updated).toLocaleString()}</td>
                   <td>
                        <button id="${getBtnId}" class="big_button fs-18rem">Перейти</button>
                        <button id="${deleteBtnId}" class="big_button fs-18rem">Удалить</button>
                   </td>
            </tr>`

        tableForms.appendChild(tableRow)
        document.getElementById(deleteBtnId).addEventListener('click', () => {
            deleteForm(json[i].id)
        })
        document.getElementById(getBtnId).addEventListener('click', () => {
            location.href = `?form=true&id=${json[i].id}`
        })
    }
}

export function deleteForm(id) {
    const toDelete = confirm("Уверены, что хотите удалить шаблон?")
    if (toDelete) {
        qrApi().deleteForm(id)
            .then(resp => resp.ok)
            .then(ok => location.reload())
            .catch(ex => alert(ex))
    }
}

function renderForm(parentDiv, objects, json) {
    parentDiv.innerHTML = ''

    let headers = [
        new TableHead('Тип данных', '10%'), new TableHead('Название поля', '20%'),
        new TableHead('Значение', '20%'), new TableHead('Статическое', '9%'),
        new TableHead('Публичное', '8%'), new TableHead('Поз.', '6%'),
        new TableHead('Удалить', '8%')
    ]
    let items = []
    for (let index in objects) {
        const formLine = objects[index];
        const item = fieldToHtmlItems(formLine, index, fieldTypes);
        items.push(item)
    }

    parentDiv.appendChild(getTextLabel('Поля:'))
    let table = getTable(
        headers,
        items,
        'formFieldRow',
        'fields_table'
    )
    let addElementButton = document.createElement('button')
    addElementButton.className = 'custom_button'
    addElementButton.innerText = '+'

    addElementButton.addEventListener('click', () => {
        formFields = Field.htmlToFields(parentDiv)
        lastElementIndex = Field.getLastFieldsIndex(formFields) + 1
        const defField = Field.getDefault(lastElementIndex)
        formFields.push(defField)
        renderForm(parentDiv, formFields, json)
    })

    let tr = getTr(addElementButton, headers.length)
    table.appendChild(tr)

    parentDiv.appendChild(table)

    // processing same field names
    let fieldNames = document.getElementsByClassName('field_name')
    let incorrectFields = Field.getIncorrectFields(formFields)
    Field.printRedBorderOnIncorrectFields(incorrectFields)
    for (let fieldName in fieldNames) {
        if (fieldNames[fieldName] instanceof HTMLElement) {
            fieldNames[fieldName].addEventListener('input', (e) => {
                formFields[fieldName].name = e.target.value
                let incorrectFields = Field.getIncorrectFields(formFields)
                Field.printRedBorderOnIncorrectFields(incorrectFields)
            })
        }
    }
    // end processing same field names

    parentDiv.appendChild(getTextLabel('Файлы:'))
    let filesHeaders = [
        new TableHead('Название', '10%'), new TableHead('Оригинальное название', '10%'),
        new TableHead('Описание', '20%'), new TableHead('Размер', '9%'),
        new TableHead('Публичный', '9%'), new TableHead('Создан', '8%'),
        new TableHead('Удалить', '8%')
    ]

    let files = json?.files

    let fileItems = []
    for (let index in files) {
        const file = files[index];
        fileItems.push({
            name: `<input name="id" type="hidden" value="${file?.id}"/>` + file?.name,
            fileName: file?.fileName,
            description: file?.description,
            fileSize: formatBytes(file?.fileSize),
            isPublic: file?.isPublic,
            created: file?.created,
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

            qrApi().uploadFormFile(json?.id,
                {
                    name: name.value,
                    description: description.value,
                    file: file,
                    public: isPublic.checked
                }
            )
            alert('Файл успешно загружен!')

            formFields = Field.htmlToFields(parentDiv)
            qrApi().getFormById(json?.id)
                .then(resp => {
                    return resp.json()
                }).then(json => {
                    renderForm(parentDiv, formFields, json)
                })
        })
    })

    let fileTr = getTr(addFileButton, filesHeaders.length)
    tableFiles.appendChild(fileTr)

    parentDiv.appendChild(tableFiles)

    addDeleteRowActions(parentDiv, objects, json)
    addDeleteFileRowActions()
}

function addDeleteRowActions(parentDiv, objects, json) {
    let rows = document.getElementsByClassName('formFieldRow')
    for (let i = 0; i < rows.length; i++) {
        let elem = document.getElementById(`delete_btn_${i}`)
        if (elem) {
            elem.addEventListener('click', () => {
                elem.parentElement.parentElement.remove()
                formFields.splice(i, 1)
                renderForm(parentDiv, Field.htmlToFields(parentDiv), json)
            })
        }
    }
}

export function addDeleteFileRowActions() {
    let rows = document.getElementsByClassName('formFileRow')
    for (let i = 0; i < rows.length; i++) {
        let elem = document.getElementById(`delete_file_btn_${i}`)
        if (elem) {
            elem.addEventListener('click', () => {
                elem.parentElement.parentElement.remove()
            })
        }
    }
}

function getStartPage() {
    return `
        <div class="toolbar">
            <button class="big_button" id="create_form_button">
                    Создать новый шаблон
            </button>
            <button class="big_button" id="list_forms_button">
                    Список шаблонов
            </button>
        
        </div>
    
    `
}

function setStartActions() {
    document.getElementById('create_form_button')
        .addEventListener('click', () => {
            location.href = '?form=true&create=true'
        })
    document.getElementById('list_forms_button')
        .addEventListener('click', () => {
            location.href = '?form=true'
        })
}

export class Field {

    constructor(id, name, placeholder, fieldType, isPublic, isStatic, fieldOrder = 0) {
        this.id = Number(id)
        this.name = name
        this.placeholder = placeholder
        this.fieldType = fieldType
        this.fieldOrder = parseInt(fieldOrder)
        this.isPublic = isPublic
        this.isStatic = isStatic
    }

    static getDefault(order = 0) {
        return new Field(
            0,
            'Название',
            '',
            'TEXT',
            true,
            true,
            parseInt(order)
        )
    }

    static htmlToFields(div) {
        const formFields = div.getElementsByClassName('formFieldRow')
        if (formFields.length === 0) {
            return []
        }

        let fields = []
        for (let i = 0; i < formFields.length; i++) {
            // TODO: not depend on element index
            const fieldId = formFields[i].children[0].firstElementChild.value
            const fieldType = formFields[i].children[0].children[1].value
            const name = formFields[i].children[1].firstElementChild.value
            const placeholder = formFields[i].children[2].firstElementChild.value
            const isStatic = formFields[i].children[3].firstElementChild.checked
            const isPublic = formFields[i].children[4].firstElementChild.checked
            const order = formFields[i].children[5].firstElementChild.value
            fields.push(new Field(fieldId, name, placeholder, fieldType, isPublic, isStatic, order))
        }
        return fields
    }

    static htmlToFiles(div) {
        const formFiles = div.getElementsByClassName('formFileRow')
        if (formFiles.length === 0) {
            return []
        }

        let files = []
        for (let i = 0; i < formFiles.length; i++) {
            let formFileIdElement = formFiles[i].children[0].firstElementChild
            if (formFileIdElement) {
                const fileId = formFileIdElement.value
                files.push({ id: fileId })
            }
        }
        return files
    }

    static getLastFieldsIndex(fields) {
        if (fields == null || fields == undefined || fields.length == 0) {
            return 0
        }
        let orders = fields.map(field => field.fieldOrder);
        return orders.reduce((accumulator, currentValue) => { return Math.max(accumulator, currentValue); },
            orders[0]
        );
    }

    static sortFields(fields) {
        if (fields === null || fields === undefined || fields == []) {
            return []
        }
        return fields.sort(function (a, b) { return a.fieldOrder - b.fieldOrder })
    }

    static fieldsToSaveForm(formName, formDescription, fields, files, formId) {
        return {
            name: formName,
            description: formDescription,
            id: Number(formId),
            files: files,
            fields: fields
        }
    }

    static getIncorrectFields(formFields) {
        if (formFields === null) {
            return []
        }
        const incorrectFields = new Set()
        const fieldsSet = new Set()
        for (let i = 0; i < formFields.length; i++) {
            const name = formFields[i].name;
            if (emptyOrUndefined(name)) {
                incorrectFields.add(formFields[i])
            }
            if (fieldsSet.has(name)) {
                incorrectFields.add(formFields[i])
            }
            fieldsSet.add(name)
        }
        return incorrectFields
    }

    static printRedBorderOnIncorrectFields(formFields) {
        if (formFields === null) {
            return
        }
        let fieldNames = document.getElementsByClassName('field_name')
        let formFieldNames = []
        for (let ff of formFields.keys()) {
            formFieldNames.push(ff.name)
        }
        for (let fn in fieldNames) {
            try {
                let parentTr = fieldNames[fn].parentElement.parentElement
                if (formFieldNames.includes(fieldNames[fn].value)) {
                    parentTr.classList = 'formFieldRow red_border'
                } else {
                    parentTr.classList = 'formFieldRow'
                }
            } catch (e) { }
        }
    }
}
