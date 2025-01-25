import { emptyOrUndefined, getQRImage, hasAdminRole, isUpperCase, longToHex, processFetchErrorToLogin, toLoginIfNotAuthorized } from "./utils.js";
import { adminApi, dictionaryApi, QR_PRINTER_URL, qrApi, userApi } from "./api.js";
import { LOCAL_STORAGE_USER } from "./localStorage.js";
import { getBigButton } from "./components/buttons.js";
import { getModalWindow, showGenerateRandomPasswordModal } from "./components/modals.js";
import { getInput, getSelect, getSingleInput, getSwitch, getTextArea } from "./components/inputs.js";
import { Column, LANGUAGES, ParticipantSettings, QR_TABLE_COLUMNS, Settings } from "./domain/participantSettings.js";
import { getOrOther, notEmptyOrUndefined, USER_ROLES } from "../commons/common.js";
import { get2TextLabels, getTextLabel } from "./components/labels.js";
import { DICTIONARIES } from "./domain/dictionaries.js";
import { getHr } from "./components/hrs.js";
import { Loader } from "./components/loader.js";
import { DOWNRAISING_INDEX, getComplexTable, getTable, TableHead } from "./components/tables.js";
import { getNavigationMenu } from "./components/blocks.js";

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
        if (!admin) {
            div.prepend(getNavigationMenu())
        }
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
    let qrPrintTextDiv
    let defaultQrPrintTextInputUp
    let defaultQrPrintTextInputDown

    let existedDefaultQrPrintText = settingsJson?.settings?.defaultQrPrintText
    let existedDefaultQrPrintTextDown = settingsJson?.settings?.defaultQrPrintTextDown
    let existedQrTableSettings = settingsJson?.settings?.qrTableColumns
    if (!admin) {
        qrPrintTextDiv = document.createElement('div')
        let defaultQrPrintTextLabelUp = getTextLabel('Текст для печатной формы QR (сверху):')
        defaultQrPrintTextInputUp = getSingleInput('text', false, 'qr_print_form_input', '')
        defaultQrPrintTextInputUp.style.width = '100%'
        defaultQrPrintTextInputUp.maxLength = 128
        if (existedDefaultQrPrintText) {
            defaultQrPrintTextInputUp.value = existedDefaultQrPrintText
        }

        let defaultQrPrintTextLabelDown = getTextLabel('Текст для печатной формы QR (снизу):')
        defaultQrPrintTextInputDown = getSingleInput('text', false, 'qr_print_form_input_down', '')
        defaultQrPrintTextInputDown.style.width = '100%'
        defaultQrPrintTextInputDown.maxLength = 128
        if (existedDefaultQrPrintTextDown) {
            defaultQrPrintTextInputDown.value = existedDefaultQrPrintTextDown
        }
        qrPrintTextDiv.appendChild(defaultQrPrintTextLabelUp)
        qrPrintTextDiv.appendChild(defaultQrPrintTextInputUp)
        qrPrintTextDiv.appendChild(defaultQrPrintTextLabelDown)
        qrPrintTextDiv.appendChild(defaultQrPrintTextInputDown)

        qrTableSettingsDiv = document.createElement('div')
        printSettingsTableAttribute(qrTableSettingsDiv, existedQrTableSettings, settingsJson)
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
                defaultQrPrintTextInputUp?.value,
                defaultQrPrintTextInputDown?.value
            )
        )

        userApi().updateSettings(stgs)
            .then(resp => {
                if (resp.ok) {
                    alert('Настройки обновлены')
                    location.reload()
                }
            })
            .catch(ex => alert(ex))
    })

    let changePasswordButton = getBigButton('Изменить пароль')
    changePasswordButton.addEventListener('click', () => {
        let innerDiv = document.createElement('div')
        innerDiv.className = 'modal-content-inner'

        const oldPassword = getInput('Старый пароль', 'password', true, 'old_password', 'Введите старый пароль...')
        const passw1 = getInput('Новый пароль', 'password', true, 'new_password_1', 'Введите пароль...', false, 'new-password')
        const passw2 = getInput('Повтор нового пароля', 'password', true, 'new_password_2', 'Повторите новый пароль...', false, 'new-password')
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

        let passGenerateBtn = getBigButton('Сгенерировать пароль')
        passGenerateBtn.addEventListener('click', () => showGenerateRandomPasswordModal())

        innerDiv.append(oldPassword, passw1, passw2, saveBtn, passGenerateBtn)

        const modal = getModalWindow('Изменение пароля', innerDiv);
        modal.style.display = 'block'
    })

    divSettings.appendChild(changePasswordButton)
    divSettings.appendChild(saveSettingsButton)

    div.appendChild(divSettings)
}

function printSettingsTableAttribute(parent, existedQrTableSettings, settingsJson) {
    parent.innerHTML = ''
    let qrTableSettingsHeader = getTextLabel('Настройки таблицы карточек')
    parent.appendChild(qrTableSettingsHeader)
    parent.appendChild(document.createElement('br'))

    let attributesList = notEmptyOrUndefined(existedQrTableSettings)
        ? existedQrTableSettings
        : QR_TABLE_COLUMNS
    for (let col in attributesList) {
        let currentCol = attributesList[col]
        let columnName = document.createElement('label');
        let colName = notEmptyOrUndefined(currentCol?.name) ? currentCol.name : currentCol.fieldName
        columnName.innerHTML = " - " + colName
        let columnEnable = document.createElement('input')
        columnEnable.type = 'checkbox'
        let removeAttrBtn = getBigButton('-')

        if (notEmptyOrUndefined(existedQrTableSettings) && notEmptyOrUndefined(existedQrTableSettings[col])) {
            if (existedQrTableSettings[col].enable) {
                columnEnable.checked = true
            }
        } else if (currentCol.enable) {
            columnEnable.checked = true
        }

        parent.appendChild(columnName)
        parent.appendChild(columnEnable)
        parent.appendChild(removeAttrBtn)
        columnEnable.addEventListener('change', function () {
            if (this.checked) {
                if (notEmptyOrUndefined(existedQrTableSettings)
                    && notEmptyOrUndefined(existedQrTableSettings[col])) {
                    existedQrTableSettings[col].enable = true
                } else {
                    QR_TABLE_COLUMNS[col].enable = true
                }
            } else {
                if (notEmptyOrUndefined(existedQrTableSettings)
                    && notEmptyOrUndefined(existedQrTableSettings[col])) {
                    existedQrTableSettings[col].enable = false
                } else {
                    QR_TABLE_COLUMNS[col].enable = false
                }
            }
        })
        removeAttrBtn.addEventListener('click', () => {
            if (notEmptyOrUndefined(existedQrTableSettings)
                && notEmptyOrUndefined(existedQrTableSettings[col])) {
                existedQrTableSettings.splice(col, 1)
            } else {
                QR_TABLE_COLUMNS.splice(col, 1)
            }
            printSettingsTableAttribute(parent, existedQrTableSettings, settingsJson)
        })
        parent.appendChild(document.createElement('br'))
    }

    let addSelfAttributeBtn = getBigButton('Добавить атрибут')
    parent.appendChild(addSelfAttributeBtn)

    addSelfAttributeBtn.addEventListener('click', () => {
        let attributeChooseDiv = document.createElement('div')
        let chooseAttributeModal = getModalWindow('Выбор атрибута', attributeChooseDiv)
        let attributesLoader = new Loader()
        attributeChooseDiv.appendChild(attributesLoader.get())

        chooseAttributeModal.style.display = 'block'
        attributesLoader.showLoader()
        qrApi().getAllFormFields()
            .then(resp => {
                attributesLoader.hideLoader()
                return resp.json()
            })
            .then(json => {
                attributeChooseDiv.appendChild(getTextLabel('Базовые поля:'))
                attributeChooseDiv.appendChild(document.createElement('br'))
                attributeChooseDiv.appendChild(document.createElement('hr'))
                for (let i = 0; i < QR_TABLE_COLUMNS.length; i++) {
                    let col = QR_TABLE_COLUMNS[i]
                    let addAttributeBtn = getBigButton('+')
                    attributeChooseDiv.appendChild(getTextLabel(col.name))
                    attributeChooseDiv.appendChild(addAttributeBtn)
                    attributeChooseDiv.appendChild(document.createElement('br'))
                    addAttributeBtn.addEventListener('click', () => {
                        attributesList.push(new Column(col.type, col.fieldName, col.name, col.enable))
                        chooseAttributeModal.remove()
                        printSettingsTableAttribute(parent, existedQrTableSettings, settingsJson)
                    })
                }
                attributeChooseDiv.appendChild(getTextLabel('Созданные поля:'))
                attributeChooseDiv.appendChild(document.createElement('br'))
                attributeChooseDiv.appendChild(document.createElement('hr'))
                for (let i = 0; i < json.length; i++) {
                    let name = json[i]?.name
                    let caption = json[i]?.caption
                    let type = json[i]?.fieldType
                    let textName = notEmptyOrUndefined(caption) ? caption : name
                    let addAttributeBtn = getBigButton('+')
                    attributeChooseDiv.appendChild(getTextLabel(textName))
                    attributeChooseDiv.appendChild(addAttributeBtn)
                    attributeChooseDiv.appendChild(document.createElement('br'))
                    addAttributeBtn.addEventListener('click', () => {
                        attributesList.push(new Column(type, name, caption, false))
                        chooseAttributeModal.remove()
                        printSettingsTableAttribute(parent, existedQrTableSettings, settingsJson)
                    })
                }
            }).catch(e => {
                attributesLoader.hideLoader()
                alert(e)
            })
    })
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

    let createQrBtn = getBigButton('Создать карточку', 'qrs_create_part')
    createQrBtn.addEventListener('click', (e) => {
        qrApi().createQR('', '')
            .then(resp => {
                if (resp.ok) {
                    alert('Карточка была создана!')
                    location.reload()
                } else if (resp.status === 500) {
                    alert("Вы достигли лимита карточек")
                } else {
                    alert('Ошибка при создании карточки')
                }
                document.activeElement.blur()
            })
            .catch(ex => alert(ex))
    })

    buttonsDiv.appendChild(createQrBtn)
    divQrsPart.appendChild(buttonsDiv)

    let qrsTable = document.createElement('table')
    qrsTable.classList = 'qrs_table compact_table'

    qrApi().getQrs()
        .then(resp => resp.json())
        .then(json => {
            let colSettings = settings?.settings?.qrTableColumns
            if (json.length > 0) {
                qrsTable.append(getTableHeader(colSettings))
            }
            for (let i = 0; i < json.length; i++) {
                const qrLine = getQRRow(json[i], settings?.settings)
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
            let fieldName = colSettings[cs]?.fieldName
            let fieldType = colSettings[cs]?.type

            if (isUpperCase(fieldType)) {
                let dataAttr = data['data']?.[fieldName]
                let fieldAttr = data['form']?.fields
                    ?.filter(field => fieldName === field['name'])
                    .map(field => field['placeholder'])
                if (notEmptyOrUndefined(dataAttr)) {
                    td.innerHTML = dataAttr
                } else if (notEmptyOrUndefined(fieldAttr)) {
                    td.innerHTML = fieldAttr
                } else {
                    td.innerHTML = ''
                }
            } else {
                switch (fieldType) {
                    case "text": {
                        td.innerHTML = data[fieldName]
                        break
                    }
                    case "number": {
                        td.innerHTML = data[fieldName]
                        break
                    }
                    case "qr_code": {
                        td.innerHTML = `<a style='text-decoration:none;' target='_blank' href="?q=${q}">${q}</a>`;
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
            }
            qrLine.appendChild(td)
        }
    }

    let printFormText = settings?.defaultQrPrintText == undefined
        ? ''
        : settings?.defaultQrPrintText
    let printFormTextDown = settings?.defaultQrPrintTextDown == undefined
        ? ''
        : settings?.defaultQrPrintTextDown

    let td = document.createElement('td')
    let editBtn = getBigButton('Открыть', `edit_${q}`)
    let printBtn = getBigButton('Распечатать QR-код', `print_${q}`)
    editBtn.addEventListener('click', () => {
        window.open(`?q=${q}&edit=true`, '_self')
    })
    printBtn.addEventListener('click', () => {
        window.open(`${QR_PRINTER_URL}?q=${q}&text=${encodeURIComponent(printFormText)}&textDown=${encodeURIComponent(printFormTextDown)}`)
    })

    td.appendChild(editBtn)
    td.appendChild(printBtn)
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
            let headerName = getOrOther(tableSettings[ts]?.name, tableSettings[ts]?.fieldName)
            header.innerHTML = headerName
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

        const username = getInput('Имя пользователя', 'text', true, 'create_participant_username', 'Введите имя пользователя...', false, 'off')
        const passw1 = getInput('Пароль', 'password', true, 'create_participant_password_1', 'Введите пароль...', false, 'new-password')
        const passw2 = getInput('Повтор пароля', 'password', true, 'create_participant_password_2', 'Повторите пароль...', false, 'new-password')
        const email = getInput('Почта', 'email', true, 'create_participant_email', 'Введите электронную почту...')
        const lei = getInput('ИНН', 'text', false, 'create_participant_lei', 'ИНН...')
        const address = getInput('Адрес', 'text', false, 'create_participant_address', 'Введите адрес...')
        const organization = getInput('Организация', 'text', false, 'create_participant_organization', 'Введите организацию...')
        const website = getInput('Вебсайт', 'url', false, 'create_participant_website', 'Введите вебсайт...')
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
                        lei: document.getElementById('create_participant_lei').value,
                        address: document.getElementById('create_participant_address').value,
                        organization: document.getElementById('create_participant_organization').value,
                        website: document.getElementById('create_participant_website').value,
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

        let passGenerateBtn = getBigButton('Сгенерировать пароль')
        passGenerateBtn.addEventListener('click', () => showGenerateRandomPasswordModal())

        dictionaryApi().getDictionariesByCode(DICTIONARIES.ROLES)
            .then(resp => resp.json())
            .then(json => {
                roles = json
                swtch = getSelect('Роль', 'role', roles.map((role => role.name)), USER_ROLES.USER)
                innerDiv.append(username, passw1, passw2, email, lei, address, organization, website, swtch, saveBtn, passGenerateBtn)
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
            let username = get2TextLabels('Имя: ', json?.username)
            let email = get2TextLabels('Email: ', json?.email)
            let lei = get2TextLabels('ИНН: ', json?.lei)
            let address = get2TextLabels('Адрес: ', json?.address)
            let website = get2TextLabels('Вебсайт: ', json?.website)
            let organization = get2TextLabels('Организация: ', json?.organization)
            let blocked = get2TextLabels('Заблокирован:', json?.banned ? ' Да' : ' Нет')
            let description = get2TextLabels('Описание: ', json?.description)
            let changePasswordBtn = getBigButton('Изменить пароль', 'change_participant_password')
            let changeParticipantData = getBigButton('Изменить данные пользователя', 'change_participant_data')
            let participantActionsDiv = document.createElement('div')
            participantActionsDiv.appendChild(changeParticipantData)
            participantActionsDiv.appendChild(changePasswordBtn)
            if (json.banned !== undefined) {
                let btnText = json.banned ? 'Разблокировать' : 'Заблокировать'
                let blockingBtn = getBigButton(btnText, 'block_user_btn')
                participantActionsDiv.appendChild(blockingBtn)

                if (json.banned) {
                    blockingBtn.addEventListener('click', () => {
                        const cf = confirm('Разблокировать пользователя?')
                        if (cf) {
                            adminApi().unblockUser(json?.id)
                                .then(resp => {
                                    if (resp.ok) {
                                        alert('Пользователь разблокирован')
                                        setUserPanel(parenDiv, participantId)
                                        return
                                    }
                                    return resp.json()
                                })
                                .then(json => {
                                    if (json) {
                                        alert(JSON.stringify(json))
                                    }
                                })
                        }
                    })
                } else {
                    blockingBtn.addEventListener('click', () => {
                        let blockContent = document.createElement('div')
                        let reasonLabel = getTextLabel('Причина')
                        let reasonInput = getSingleInput('text', false, 'reason_text')
                        let blockBtn = getBigButton('Подтвердить')
                        blockContent.appendChild(reasonLabel)
                        blockContent.appendChild(reasonInput)
                        blockContent.appendChild(blockBtn)

                        let modal = getModalWindow('Блокировка пользователя пользователя', blockContent)
                        modal.style.display = 'block'

                        blockBtn.addEventListener('click', () => {
                            const cf = confirm('Заблокировать пользователя?')
                            if (cf) {
                                adminApi().blockUser(json?.id, reasonInput?.value)
                                    .then(resp => {
                                        if (resp.ok) {
                                            alert('Пользователь заблокирован')
                                            setUserPanel(parenDiv, participantId)
                                            modal.remove()
                                            return
                                        }
                                        return resp?.json()
                                    })
                                    .then(json => {
                                        if (json) {
                                            alert(JSON.stringify(json))
                                        }
                                    })
                            }
                        })
                    })
                }
            }
            changePasswordBtn.addEventListener('click', (e) => {
                let blockContent = document.createElement('div')
                let passwordLabel = getTextLabel('Пароль')
                let passwordInput = getSingleInput('password', false, 'password')
                passwordInput.autocomplete = 'new-password'
                let confirmPasswordLabel = getTextLabel('Повтор пароля')
                let confirmPasswordInput = getSingleInput('password', false, 'confirm_password')
                confirmPasswordInput.autocomplete = 'new-password'
                let changePasswordBtn = getBigButton('Подтвердить')
                blockContent.appendChild(passwordLabel)
                blockContent.appendChild(passwordInput)
                blockContent.appendChild(confirmPasswordLabel)
                blockContent.appendChild(confirmPasswordInput)
                blockContent.appendChild(changePasswordBtn)
                let passGenerateBtn = getBigButton('Сгенерировать пароль')
                passGenerateBtn.addEventListener('click', () => showGenerateRandomPasswordModal())
                blockContent.appendChild(passGenerateBtn)

                let modal = getModalWindow('Смена пароля пользователю', blockContent)
                modal.style.display = 'block'

                changePasswordBtn.addEventListener('click', () => {
                    const cf = confirm('Сменить пароль пользователю?')
                    if (cf) {
                        adminApi().changeParticipantPassword(json?.id, passwordInput?.value, confirmPasswordInput?.value)
                            .then(resp => {
                                if (resp.ok) {
                                    alert('Пароль изменен')
                                    setUserPanel(parenDiv, participantId)
                                    modal.remove()
                                    return
                                }
                                return resp?.json()
                            })
                            .then(json => {
                                if (json) {
                                    alert(JSON.stringify(json))
                                }
                            })
                    }
                })
            })
            changeParticipantData.addEventListener('click', () => {
                let blockContent = document.createElement('div')
                const usernameInp = getInput('Имя пользователя', 'text', true, 'change_participant_username', 'Введите имя пользователя...', false, 'off', json?.username)
                const emailInp = getInput('Почта', 'email', true, 'change_participant_email', 'Введите электронную почту...', false, 'off', json?.email)
                const leiInp = getInput('ИНН', 'text', false, 'change_participant_lei', 'ИНН...', false, 'off', json?.lei)
                const addressInp = getInput('Адрес', 'text', false, 'change_participant_address', 'Введите адрес...', false, 'off', json?.address)
                const organizationInp = getInput('Организация', 'text', false, 'change_participant_organization', 'Введите организацию...', false, 'off', json?.organization)
                const websiteInp = getInput('Вебсайт', 'url', false, 'change_participant_website', 'Введите вебсайт...', false, 'off', json?.website)
                const descrInp = getTextArea('Описание', false, 'change_participant_description', 'Введите описание...', false, json?.description)
                const changeDataBtn = getBigButton('Подтвердить')
                blockContent.appendChild(usernameInp)
                blockContent.appendChild(emailInp)
                blockContent.appendChild(leiInp)
                blockContent.appendChild(addressInp)
                blockContent.appendChild(organizationInp)
                blockContent.appendChild(websiteInp)
                blockContent.appendChild(descrInp)
                blockContent.appendChild(changeDataBtn)

                let modal = getModalWindow('Смена данных пользователя', blockContent)
                modal.style.display = 'block'

                changeDataBtn.addEventListener('click', () => {
                    const cf = confirm('Сменить данные пользователя?')
                    if (cf) {
                        adminApi().updateParticipant(
                            json?.id,
                            {
                                username: document.getElementById('change_participant_username').value,
                                email: document.getElementById('change_participant_email').value,
                                lei: document.getElementById('change_participant_lei').value,
                                address: document.getElementById('change_participant_address').value,
                                organization: document.getElementById('change_participant_organization').value,
                                website: document.getElementById('change_participant_website').value,
                                description: document.getElementById('change_participant_description').value
                            }
                        ).then(resp => {
                            if (resp.ok) {
                                alert('Данные изменены')
                                setUserPanel(parenDiv, participantId)
                                modal.remove()
                                return
                            }
                            return resp?.json()
                        }).then(json => {
                            if (json) {
                                alert(JSON.stringify(json))
                            }
                        })
                    }
                })
            })

            let rangesLabel = getTextLabel('Диапазоны: ')
            let rangesHeader = [
                new TableHead('ID', '10%'), new TableHead('От', '20%'),
                new TableHead('До', '20%'), new TableHead('Создан', '20%')
            ]
            let rangesBody = []
            for (let i in json?.ranges) {
                let range = json.ranges[i]
                rangesBody.push(
                    {
                        id: range?.id,
                        from: Number(range?.from).toString(16),
                        to: Number(range?.to).toString(16),
                        created: range?.created
                    }
                )
            }
            let rangesTable = getTable(rangesHeader, rangesBody, '', '', 'compact_table')

            let qrsLabel = getTextLabel('Карточки: ')
            let qrsHeaders = [
                new TableHead('ID', '10%'), new TableHead('Код', '10%'), new TableHead('Имя', '20%'),
                new TableHead('Описание', '20%'), new TableHead('Создана', '20%')
            ]
            let qrsBody = []
            for (let i in json?.qrs) {
                let qr = json.qrs[i]
                qrsBody.push(
                    {
                        id: qr?.id,
                        code: Number(qr?.code).toString(16),
                        name: qr?.name,
                        description: qr?.description,
                        created: qr?.created
                    }
                )
            }
            let qrsTable = getTable(qrsHeaders, qrsBody, 'qr_item_row', '', 'compact_table')

            parenDiv.appendChild(username)
            parenDiv.appendChild(email)
            parenDiv.appendChild(blocked)
            parenDiv.appendChild(lei)
            parenDiv.appendChild(address)
            parenDiv.appendChild(organization)
            parenDiv.appendChild(website)
            parenDiv.appendChild(description)
            parenDiv.appendChild(participantActionsDiv)
            if (json?.banned) {
                parenDiv.append(get2TextLabels('Причина блокировки: ', json?.bannedReason))
            }
            parenDiv.appendChild(document.createElement('br'))

            parenDiv.appendChild(rangesLabel)
            parenDiv.appendChild(rangesTable)
            parenDiv.appendChild(qrsLabel)
            parenDiv.appendChild(qrsTable)

            let qrRows = document.getElementsByClassName('qr_item_row')
            for (let i = 0; i < qrRows?.length; i++) {
                let qrRow = qrRows[i]
                qrRow.addEventListener('click', (e) => {
                    let qrCode = qrRow?.childNodes[1]?.textContent
                    if (qrCode) {
                        window.open(`?q=${qrCode}`)
                    } else {
                        alert('Ошибка при получении qr кода')
                    }
                })
            }
        })
        .catch(ex => alert(ex))
}

function setUsersPanel(parentDiv) {
    parentDiv.innerHTML = ''

    let headers = [
        new TableHead('№', '3%', DOWNRAISING_INDEX),
        new TableHead('Имя', '10%', 'username'),
        new TableHead('Почта', '10%', 'email'),
        new TableHead('Организация', '10%', 'organization'),
        new TableHead('Роли', '10%', 'roles', getRolesCallback),
        new TableHead('Создан', '10%', 'created', getDateCallback),
        new TableHead('Диапазоны', '15%', 'ranges', getRangesCallback)
    ]

    adminApi().getParticipants()
        .then(resp => {
            if (!resp.ok)
                throw new Error('Ошибка при получении участников')
            return resp.json()
        })
        .then(json => {
            let table = getComplexTable(
                headers,
                json,
                'participantRow',
                'participantsTable',
                'compact_table',
                'id',
                (e) => {
                    let pId = e.currentTarget.getAttribute('key')
                    let selection = document.getSelection()
                    if (selection.type !== "Range") {
                        setUserPanel(parentDiv, pId)
                    }
                }
            )
            parentDiv.append(table)
        })
        .catch(ex => alert(ex))
}

function getRangesCallback(ranges) {
    return ranges?.map(getRangeList).join(', ')
}

function getRolesCallback(roles) {
    return roles?.map(getRoleName)
}

function getDateCallback(date) {
    return new Date(date)?.toLocaleString()
}

function getRoleName(role) {
    return [role.name].join(", ")
}

function getRangeList(range) {
    return [Number(range?.from)?.toString(16), Number(range?.to)?.toString(16)].join(" - ")
}
