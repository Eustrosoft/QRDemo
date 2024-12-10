import {fieldToEditFormRow, setQueryParamsAndRefresh, toLoginIfNotAuthorized} from "./utils.js";
import {qrApi} from "./api.js";

var formFields = []
var formId = ''
var blockId = ''

export function setForm(formId) {
    toLoginIfNotAuthorized()
        .then(x => init(formId))
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
        mainBlock.innerHTML = mainBlock.innerHTML.concat(`
            <label>Название формы</label>
            <div> <input type="text" placeholder="Форма без имени" id="formName"> </div>
            <label>Описание формы</label>
            <div> <input type="text" placeholder="Форма без описания" id="formDescription"> </div>
        `)

        let formDiv = document.createElement('div')
        mainBlock.appendChild(formDiv)

        let addElementButton = document.createElement('button')
        addElementButton.className = 'custom_button'
        addElementButton.innerText = '+'
        addElementButton.addEventListener('click', () => {
            const defField = Field.getDefault()
            formFields = Field.htmlToFields(formDiv)
            formFields.push(defField)
            renderForm(formDiv, formFields)
        })
        mainBlock.appendChild(addElementButton)

        let formName = document.getElementById('formName')
        let formDescription = document.getElementById('formDescription')

        let saveFormBtm = document.createElement('button')
        saveFormBtm.className = 'custom_button'
        saveFormBtm.innerText = 'Сохранить'
        saveFormBtm.addEventListener('click', () => {
            if (formName.value === null || formName.value === undefined || formName.value === '') {
                alert("Название формы не может быть пустое")
                return
            }
            const collectedFields = Field.htmlToFields(formDiv);
            if (collectedFields === null || collectedFields.length === 0) {
                alert("В форме не могут отсутствовать поля")
                return
            }

            if (create) {
                qrApi().saveForm(Field.fieldsToSaveForm(formName.value, formDescription.value, collectedFields))
                    .then(resp => {
                        if (resp.ok) {
                            return resp.json()
                        }
                        throw new Error('Ошибка при сохранении')
                    })
                    .then(json => {
                        setQueryParamsAndRefresh([{name: 'form', value: true}, {name: 'id', value: json.id}])
                    })
                    .then(() => alert('Форма была создана!'))
                    .catch(() => alert('Ошибка при создании формы'))
            }
            if (id !== null) {
                qrApi().updateForm(Field.fieldsToSaveForm(formName.value, formDescription.value, collectedFields, formId, blockId))
                    .then(() => alert('Форма была обновлена!'))
                    .catch(() => alert('Ошибка при обновлении формы'))
            }
        })
        mainBlock.appendChild(saveFormBtm)

        if (id) {
            qrApi().getFormById(id)
                .then(resp => resp.json())
                .then(json => {
                    formId = json.id
                    formName.value = json.name
                    formDescription.value = json.description

                    const blocks = json.blocks;
                    for (let i = 0; i < blocks.length; i++) {
                        const block = blocks[i];
                        blockId = block.id
                        const fields = block.fields;
                        for (let j = 0; j < fields.length; j++) {
                            formFields.push(fields[j])
                        }
                    }
                    renderForm(formDiv, formFields)
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
    const toDelete = confirm("Уверены, что хотите удалить форму?")
    if (toDelete) {
        qrApi().deleteForm(id)
            .then(resp => resp.ok)
            .then(ok => location.reload())
            .catch(ex => alert(ex))
    }
}

function renderForm(parentDiv, objects) {
    parentDiv.innerHTML = ''
    for (let index in objects) {
        const formLine = objects[index];
        const input = fieldToEditFormRow(formLine, index);
        parentDiv.appendChild(input)
        document.getElementById(`delete_btn_${index}`)
            .addEventListener('click', () => {
                const toDelete = confirm("Уверены, что хотите удалить поле?")
                if (toDelete) {
                    formFields = Field.htmlToFields(parentDiv)
                    formFields.splice(index, 1)
                    parentDiv.innerHTML = ''
                    renderForm(parentDiv, formFields)
                }
            })
    }
}

function getStartPage() {
    return `
        <div class="toolbar">
            <button class="big_button" id="create_form_button">
                    Создать новую форму
            </button>
            <button class="big_button" id="list_forms_button">
                    Список форм
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

class Field {

    constructor(id, name, placeholder, type, isPublic, isStatic) {
        this.id = Number(id)
        this.name = name
        this.placeholder = placeholder
        this.type = type
        this.isPublic = isPublic
        this.isStatic = isStatic
    }

    static getDefault() {
        return new Field(
            0,
            'Название',
            '',
            'TEXT',
            true,
            true
        )
    }

    static htmlToFields(div) {
        const formFields = div.getElementsByClassName('form_field');
        if (formFields.length === 0) {
            return []
        }

        let fields = []
        for (let i = 0; i < formFields.length; i++) {
            // TODO: not depend on element index
            const type = formFields[i].children[1].value;
            const name = formFields[i].children[3].value;
            const placeholder = formFields[i].children[5].value;
            const isStatic = formFields[i].children[7].checked;
            const isPublic = formFields[i].children[9].checked;
            const id = formFields[i].children[10].value;
            fields.push(new Field(id, name, placeholder, type, isPublic, isStatic))
        }
        return fields
    }

    static fieldsToSaveForm(formName, formDescription, fields, formId, blockId) {
        return {
            name: formName,
            description: formDescription,
            id: Number(formId),
            blocks: [
                {
                    name: 'Block',
                    id: Number(blockId),
                    fields: fields
                }
            ]
        }
    }
}
