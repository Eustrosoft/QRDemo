export function getFileScrollComponent(files) {
    let scrollableDiv = document.createElement('div')
    scrollableDiv.className = 'scrollable-x'

    let divUpperMenu = document.createElement('div')
    divUpperMenu.className = 'upper_menu'

    let fileUploadImage = document.createElement('div')
    fileUploadImage.className = 'upload-file-button'
    fileUploadImage.innerText = 'Загрузить Файл'
    divUpperMenu.appendChild(fileUploadImage)

    let divFilesContent = document.createElement('div')
    divFilesContent.className = 'files_list'

    scrollableDiv.appendChild(divUpperMenu)
    scrollableDiv.appendChild(divFilesContent)

    for (let i = 0; i < files.length; i++) {
        divFilesContent.appendChild(getFileComponent(files[i]))
    }

    return scrollableDiv
}

export function getFileComponent(file) {
    let divFile = document.createElement('div')
    divFile.className = 'file-item'

    let divFileIcon = document.createElement('div')
    divFileIcon.className = 'file-icon'

    let divFileMetadata = document.createElement('div')
    divFileMetadata.className = 'file-metadata'
    divFileMetadata.innerText = `Name: ${file?.name}\nSize: ${file?.size}`

    divFile.appendChild(divFileIcon)
    divFile.appendChild(divFileMetadata)

    return divFile
}