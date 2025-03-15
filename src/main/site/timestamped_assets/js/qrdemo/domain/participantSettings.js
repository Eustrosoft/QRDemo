
export class ParticipantSettings {

    constructor(settings) {
        this.settings = settings
    }
}

export class Settings {

    constructor(language, qrTableColumns, defaultQrPrintText, defaultQrPrintTextDown, checkUploadSize) {
        this.language = language
        this.qrTableColumns = qrTableColumns
        this.defaultQrPrintText = defaultQrPrintText
        this.defaultQrPrintTextDown = defaultQrPrintTextDown
        this.checkUploadSize = checkUploadSize
    }
}

export class Column {

    constructor(type, fieldName, name, enable) {
        this.type = type
        this.fieldName = fieldName
        this.name = name
        this.enable = enable
    }
}

export const QR_TABLE_COLUMNS = [
    new Column("qr_image", "code_picture", "QR Картинка", true),
    new Column("qr_code", "code", "QR Код", true),
    new Column("text", "name", "Имя", true),
    new Column("text", "description", "Описание", true),
    new Column("date", "created", "Создана", false),
    new Column("date", "updated", "Отредактирована", false)
]

export const LANGUAGES = [{code: "RU", name: "Русский"}]
