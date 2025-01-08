import { emptyOrUndefined, getQRImage, hasAdminRole, longToHex, processFetchErrorToLogin, toLoginIfNotAuthorized } from "./utils.js";
import { adminApi, dictionaryApi, QR_PRINTER_URL, qrApi, userApi } from "./api.js";
import { LOCAL_STORAGE_USER } from "./localStorage.js";
import { getBigButton } from "./components/buttons.js";
import { getModalWindow } from "./components/modals.js";
import { getInput, getSelect, getSingleInput } from "./components/inputs.js";
import { LANGUAGES, ParticipantSettings, QR_TABLE_COLUMNS, Settings } from "./domain/participantSettings.js";
import { notEmptyOrUndefined, USER_ROLES } from "../commons/common.js";
import { get2TextLabels, getTextLabel } from "./components/labels.js";
import { DICTIONARIES } from "./domain/dictionaries.js";
import { getHr } from "./components/hrs.js";

const mainBlock = document.getElementById('main_block')
let divLk = document.createElement('div')
divLk.setAttribute('id', 'my_lk')

let me;
let roles;

// admin parameters
let admin;
let participants;
let createParticipant;
let ranges;
let settings;

export function setLk() {
    toLoginIfNotAuthorized()
        .then(resp => {
            document.title = 'QRDemo - Личный кабинет'

            const urlParams = new URLSearchParams(window.location.search)
            participants = urlParams.get('participants')
            createParticipant = urlParams.get('createParticipant')
            ranges = urlParams.get('ranges')
            settings = urlParams.get('settings')

            me = localStorage.getItem(LOCAL_STORAGE_USER)
            let userDetails = JSON.parse(me)
            roles = userDetails.roles
            admin = hasAdminRole(roles)

            mainBlock.appendChild(divLk)
            setUserAccount(userDetails, divLk)
        })
}

function setUserAccount(userDetails, div) {
    if (userDetails && div) {
        if (settings) {
            userApi()
                .getSettings()
                .then(resp => resp.json())
                .then(json => setSettings(div, json, userDetails))
                .catch(ex => {
                    console.log(ex)
                    setSettings(div, null, userDetails)
                })
        } else {
            if (!admin) {
                setupQrs(div)
            } else {
                setupAdminPanel(div)
            }
        }
    }
}

function setSettings(div, settingsJson, userDetails) {
    let divSettings = document.createElement('div')
    divSettings.className = 'basic_card'

    let languageLabel = document.createElement('label')
    languageLabel.innerHTML = 'Язык: '
    let languageSelect = document.createElement('select')
    languageSelect.id = 'lang_select'

    let existedLang = settingsJson?.settings?.language
    for (let lang in LANGUAGES) {
        let opt = document.createElement('option');
        opt.innerHTML = LANGUAGES[lang].code + ' ' + LANGUAGES[lang].name
        opt.value = LANGUAGES[lang].code
        if (existedLang === opt.value) {
            opt.selected = true
        }
        languageSelect.appendChild(opt)
    }

    let qrTableSettingsDiv
    let existedQrTableSettings

    let qrPrintTextDiv
    let defaultQrPrintTextInput
    let existedDefaultQrPrintText = settingsJson?.settings?.defaultQrPrintText
    if (!admin) {
        qrPrintTextDiv = document.createElement('div')
        let defaultQrPrintTextLabel = getTextLabel('Текст для печатной формы QR по умолчанию:')
        defaultQrPrintTextInput = getSingleInput('text', false, 'qr_print_form_input', '')
        defaultQrPrintTextInput.style.width = '100%'
        defaultQrPrintTextInput.maxLength = 128
        if (existedDefaultQrPrintText) {
            defaultQrPrintTextInput.value = existedDefaultQrPrintText
        }
        qrPrintTextDiv.appendChild(defaultQrPrintTextLabel)
        qrPrintTextDiv.appendChild(defaultQrPrintTextInput)

        qrTableSettingsDiv = document.createElement('div')
        let qrTableSettingsHeader = getTextLabel('Настройки таблицы QR')
        qrTableSettingsDiv.appendChild(qrTableSettingsHeader)
        qrTableSettingsDiv.appendChild(document.createElement('br'))

        existedQrTableSettings = settingsJson?.settings?.qrTableColumns
        for (let col in QR_TABLE_COLUMNS) {
            let currentCol = QR_TABLE_COLUMNS[col]
            let columnName = document.createElement('label');
            columnName.innerHTML = " - " + currentCol.name
            let columnEnable = document.createElement('input')
            columnEnable.type = 'checkbox'

            if (notEmptyOrUndefined(existedQrTableSettings)) {
                if (existedQrTableSettings[col].enable) {
                    columnEnable.checked = true
                }
            } else if (currentCol.enable) {
                columnEnable.checked = true
            }

            qrTableSettingsDiv.appendChild(columnName)
            qrTableSettingsDiv.appendChild(columnEnable)
            columnEnable.addEventListener('change', function () {
                if (this.checked) {
                    if (notEmptyOrUndefined(existedQrTableSettings)) {
                        existedQrTableSettings[col].enable = true
                    } else {
                        QR_TABLE_COLUMNS[col].enable = true
                    }
                } else {
                    if (notEmptyOrUndefined(existedQrTableSettings)) {
                        existedQrTableSettings[col].enable = false
                    } else {
                        QR_TABLE_COLUMNS[col].enable = false
                    }
                }
            })

            qrTableSettingsDiv.appendChild(document.createElement('br'))
        }
    }

    divSettings.appendChild(languageLabel)
    divSettings.appendChild(languageSelect)
    divSettings.appendChild(document.createElement('br'))

    if (!admin) {
        divSettings.appendChild(qrPrintTextDiv)
        let ranges = userDetails?.ranges
        if (ranges) {
            divSettings.appendChild(getHr())
            let rangesSpan = getTextLabel('Выделенные диапазоны:')
            divSettings.appendChild(rangesSpan)
            for (let index in ranges) {
                let range = ranges[index]
                let rangeSpan = getTextLabel(` - ${range?.from}-${range?.to}`)
                divSettings.appendChild(rangeSpan)
            }
            divSettings.appendChild(getHr())
        }

        divSettings.appendChild(qrTableSettingsDiv)
    }

    let saveSettingsButton = getBigButton('Сохранить')
    saveSettingsButton.addEventListener('click', () => {
        let stgs = new ParticipantSettings(
            new Settings(
                languageSelect.value,
                admin ? null : existedQrTableSettings == null ? QR_TABLE_COLUMNS : existedQrTableSettings,
                defaultQrPrintTextInput?.value
            )
        )

        userApi().updateSettings(stgs)
            .then(resp => {
                if (resp.ok) {
                    alert('Настройки обновлены')
                    location.reload()
                }
            }).catch(ex => alert(ex))
    })

    let changePasswordButton = getBigButton('Изменить пароль')
    changePasswordButton.addEventListener('click', () => {
        let innerDiv = document.createElement('div')
        innerDiv.className = 'modal-content-inner'

        const oldPassword = getInput('Старый пароль', 'password', true, 'old_password', 'Введите старый пароль...')
        const passw1 = getInput('Новый пароль', 'password', true, 'new_password_1', 'Введите пароль...')
        const passw2 = getInput('Повтор нового пароля', 'password', true, 'new_password_2', 'Повторите новый пароль...')
        const saveBtn = getBigButton('Обновить пароль')

        saveBtn.addEventListener('click', () => {
            userApi()
                .changePassword(
                    {
                        oldPassword: document.getElementById('old_password').value,
                        newPassword: document.getElementById('new_password_1').value,
                        confirmNewPassword: document.getElementById('new_password_2').value
                    }
                ).then(resp => {
                    if (!resp.ok)
                        throw new Error('Ошибка при обновлении пароля')
                }).then(resp => {
                    alert('Пароль обновлен!')
                }).catch(ex => alert(ex))
        })

        innerDiv.append(oldPassword, passw1, passw2, saveBtn)

        const modal = getModalWindow('Изменение пароля', innerDiv);
        modal.style.display = 'block'
    })

    divSettings.appendChild(changePasswordButton)
    divSettings.appendChild(saveSettingsButton)

    div.appendChild(divSettings)
}

function setupQrs(div) {
    userApi().getSettings()
        .then(resp => resp.json())
        .then(settings => setupQrsPart(div, settings))
        .catch(ex => {
            console.log(ex)
            setupQrsPart(div, null)
        })
}

function setupQrsPart(div, settings) {
    if (emptyOrUndefined(settings)) {
        settings = new ParticipantSettings(new Settings('RU', QR_TABLE_COLUMNS))
    }
    let divQrsPart = document.createElement('div')
    divQrsPart.className = 'basic_card'

    let buttonsDiv = document.createElement('div')
    buttonsDiv.className = 'account_card_buttons'

    let divQrCreatePart = document.createElement('button')
    divQrCreatePart.id = 'qrs_create_part'
    divQrCreatePart.className = 'big_button'
    divQrCreatePart.innerHTML = `<a>Создать карточку</a>`
    divQrCreatePart.addEventListener('click', () => {
        qrApi().createQR('', '')
            .then(resp => {
                if (resp.ok) {
                    alert('Карточка была создана!')
                    location.reload()
                } else {
                    alert("Карточка не была создана")
                }
            })
            .catch(ex => alert(ex))
    })

    let divFormCreatePart = document.createElement('button')
    divFormCreatePart.id = 'forms_create_part'
    divFormCreatePart.className = 'big_button'
    divFormCreatePart.innerHTML = `Шаблоны`
    divFormCreatePart.addEventListener('click', () => {
        window.location = '?form=true'
    })

    buttonsDiv.appendChild(divQrCreatePart)
    buttonsDiv.appendChild(divFormCreatePart)
    divQrsPart.appendChild(buttonsDiv)

    let qrsTable = document.createElement('table')
    qrsTable.className = 'qrs_table'

    qrApi().getQrs()
        .then(resp => resp.json())
        .then(json => {
            let colSettings = settings?.settings?.qrTableColumns
            if (json.length > 0) {
                qrsTable.append(getTableHeader(colSettings))
            }
            for (let i = 0; i < json.length; i++) {
                let code = json[i]?.code
                let name = json[i]?.name
                let description = json[i]?.description
                let created = json[i]?.created
                let updated = json[i]?.updated
                const qrLine = getQRRow({ code: code, name: name, description: description, created: created, updated: updated }, settings?.settings)
                qrsTable.appendChild(qrLine)
            }
        }).catch(ex => alert(ex))
    divQrsPart.appendChild(qrsTable)
    div.appendChild(divQrsPart)
}

function getQRRow(data, settings) {
    let qrLine = document.createElement('tr')
    const q = Number(data?.code).toString(16);

    let colSettings = settings?.qrTableColumns
    for (let cs in colSettings) {
        if (colSettings[cs].enable) {
            let td = document.createElement('td')
            let fieldName = colSettings[cs].fieldName
            let fieldType = colSettings[cs].type

            switch (fieldType) {
                case "text": {
                    td.innerHTML = data[fieldName]
                    break
                }
                case "qr_code": {
                    td.innerHTML = q;
                    break
                }
                case "qr_image": {
                    td.innerHTML = getQRImage(q, 125)
                    break
                }
                case "date": {
                    td.innerHTML = new Date(data[fieldName]).toLocaleString()
                    break
                }
            }
            qrLine.appendChild(td)
        }
    }

    let printFormText = settings?.defaultQrPrintText == undefined
        ? ''
        : settings?.defaultQrPrintText

    let td = document.createElement('td')
    td.innerHTML = `
        <div class="custom_button fs-08rem"><a href="?q=${q}&edit=true">Редактировать</a></div>
        <div class="custom_button fs-08rem"><a href="${QR_PRINTER_URL}?q=${q}&text=${printFormText}" target="_">Распечатать QR-код</a></div>
    `
    qrLine.appendChild(td)

    return qrLine
}

function getTableHeader(tableSettings) {
    if (tableSettings === null || tableSettings === undefined) {
        let tableHeader = document.createElement('tr')

        let qrTd = document.createElement('th')
        qrTd.innerHTML = 'QR'
        tableHeader.append(qrTd)

        let qrCode = document.createElement('th')
        qrCode.innerHTML = 'Код'
        tableHeader.append(qrCode)

        let qrName = document.createElement('th')
        qrName.innerHTML = 'Название'
        tableHeader.append(qrName)

        let qrDescription = document.createElement('th')
        qrDescription.innerHTML = 'Описание'
        tableHeader.append(qrDescription)

        let actions = document.createElement('th')
        actions.innerHTML = 'Действия'
        tableHeader.append(actions)

        return tableHeader
    }

    let tableHeader = document.createElement('tr')

    for (let ts in tableSettings) {
        if (tableSettings[ts].enable) {
            let header = document.createElement('th')
            header.innerHTML = tableSettings[ts]?.name
            tableHeader.append(header)
        }
    }
    let header = document.createElement('th')
    header.innerHTML = 'Действия'
    tableHeader.append(header)

    return tableHeader
}

function setupAdminPanel(div) {
    let adminDiv = document.createElement('div')
    adminDiv.className = 'basic_card'

    let buttonsDiv = document.createElement('div')
    buttonsDiv.className = 'account_card_buttons'

    let createParticipantButton = getBigButton('Создать пользователя')

    buttonsDiv.appendChild(createParticipantButton)

    let getParticipantsButton = getBigButton('Список пользователей')

    let viewDiv = document.createElement('div')
    viewDiv.id = 'admin_panel'

    getParticipantsButton.addEventListener('click', () => {
        setUsersPanel(viewDiv)
    })

    createParticipantButton.addEventListener('click', () => {
        let innerDiv = document.createElement('div')
        innerDiv.className = 'modal-content-inner'

        const username = getInput('Имя пользователя', 'text', true, 'create_participant_username', 'Введите имя пользователя...')
        const passw1 = getInput('Пароль', 'password', true, 'create_participant_password_1', 'Введите пароль...')
        const passw2 = getInput('Повтор пароля', 'password', true, 'create_participant_password_2', 'Повторите пароль...')
        const email = getInput('Емейл', 'email', true, 'create_participant_email', 'Введите электронную почту...')
        let swtch = getSelect('Роль', 'role', ['ROLE_USER', 'ROLE_ADMIN'], 'ROLE_USER')
        const saveBtn = getBigButton('Создать пользователя')
        saveBtn.style = 'margin-top: 8px;'

        let roles = null

        saveBtn.addEventListener('click', () => {
            adminApi()
                .createParticipant(
                    {
                        username: document.getElementById('create_participant_username').value,
                        password: document.getElementById('create_participant_password_1').value,
                        confirmPassword: document.getElementById('create_participant_password_2').value,
                        email: document.getElementById('create_participant_email').value,
                        roles: [roles.find(x => x.name === document.getElementById('role').value)]
                    }
                ).then(resp => {
                    if (!resp.ok)
                        throw new Error('Ошибка при создании пользователя')
                    return resp.json()
                }).then(json => {
                    alert('Пользователь был создан!')
                }).catch(ex => alert(ex))
        })

        dictionaryApi().getDictionariesByCode(DICTIONARIES.ROLES)
            .then(resp => resp.json())
            .then(json => {
                roles = json
                swtch = getSelect('Роль', 'role', roles.map((role => role.name)), USER_ROLES.USER)
                innerDiv.append(username, passw1, passw2, email, swtch, saveBtn)
                const modal = getModalWindow('Создание нового пользователя', innerDiv);
                modal.style.display = 'block'
            })
    })

    buttonsDiv.appendChild(getParticipantsButton)

    adminDiv.append(buttonsDiv)

    adminDiv.append(viewDiv)

    div.appendChild(adminDiv)
}

function setUserPanel(parenDiv, participantId) {
    parenDiv.innerHTML = ''

    adminApi().getParticipant(participantId)
        .then(resp => resp.json())
        .then(json => {
            let usern = get2TextLabels('Username: ', json?.username)
            let ranges = getTextLabel('Ranges: ')
            let preRanges = document.createElement('pre')
            preRanges.innerHTML = JSON.stringify(json?.ranges)

            let qrs = getTextLabel('QRS: ')
            let preQRS = document.createElement('pre')
            preQRS.innerHTML = JSON.stringify(json?.qrs)

            parenDiv.appendChild(usern)
            parenDiv.appendChild(document.createElement('br'))
            parenDiv.appendChild(ranges)
            parenDiv.appendChild(preRanges)

            parenDiv.appendChild(qrs)
            parenDiv.appendChild(preQRS)
        })
        .catch(ex => alert(ex))
}

function setUsersPanel(parenDiv) {
    parenDiv.innerHTML = ''

    let participantsTable = document.createElement('table')

    let tableHeader = document.createElement('tr')
    let td1 = document.createElement('th')
    td1.innerHTML = 'Имя пользователя'
    let th2 = document.createElement('th')
    th2.innerHTML = 'Почта'
    let th3 = document.createElement('th')
    th3.innerHTML = 'Создан'
    let th4 = document.createElement('th')
    th4.innerHTML = 'Роли'
    let th5 = document.createElement('th')
    th5.innerHTML = 'Диапазоны'

    tableHeader.append(td1, th2, th3, th4, th5)
    participantsTable.append(tableHeader)

    adminApi().getParticipants()
        .then(resp => {
            if (!resp.ok)
                throw new Error('Ошибка при получении участников')
            return resp.json()
        })
        .then(json => {
            for (let part in json) {
                participantsTable.append(getParticipantTr(json[part], parenDiv))
            }
        })
        .catch(ex => alert(ex))

    parenDiv.append(participantsTable)
}

function getParticipantTr(participantJson, parentDiv) {
    const username = participantJson?.username;
    const email = participantJson?.email;
    const roles = participantJson?.roles;
    const ranges = participantJson?.ranges;
    const created = participantJson?.created

    let tr = document.createElement('tr')

    let td1 = document.createElement('td')
    td1.innerHTML = username
    let td2 = document.createElement('td')
    td2.innerHTML = email
    let td3 = document.createElement('td')
    td3.innerHTML = new Date(created).toLocaleString()
    let td4 = document.createElement('td')
    td4.innerHTML = roles?.map(getRoleName)
    let td5 = document.createElement('td')
    td5.innerHTML = ranges?.map(getRangeList)

    tr.style = 'cursor: pointer;'
    tr.className = 'hoverable'
    tr.addEventListener('click', () => {
        setUserPanel(parentDiv, participantJson?.id)
    })

    tr.append(td1, td2, td3, td4, td5)

    return tr;
}

function getRoleName(role) {
    return [role.name].join(", ");
}

function getRangeList(range) {
    return [range.from, range.to].join(" - ");
}
