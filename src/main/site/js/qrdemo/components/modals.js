
export function getModalWindow(modalName, modalInner) {
    let modalWindow = document.createElement('div')
    modalWindow.className = 'modal'

    let modalContent = document.createElement('div')

    modalContent.className = 'modal-content'
    modalContent.prepend(getModalHeader(modalWindow, modalName))
    modalInner.className = 'modal-content-inner'
    modalContent.append(modalInner)

    modalWindow.appendChild(modalContent)
    document.getElementsByTagName('body')[0].appendChild(modalWindow)

    window.addEventListener('mousedown', (event) => {
        if (event.target == modalWindow) {
            modalWindow.remove();
        }
    })

    return modalWindow
}

function getModalHeader(modal, headerName) {
    let modalContentHeader = document.createElement('div')
    modalContentHeader.className = 'modal-header'

    let modalHeaderName = document.createElement('span')
    modalHeaderName.innerHTML = headerName
    modalHeaderName.className = 'title'

    let modalHeaderClose = document.createElement('span')
    modalHeaderClose.innerHTML = '&times;'
    modalHeaderClose.className = 'close'

    modalContentHeader.appendChild(modalHeaderName)
    modalContentHeader.appendChild(modalHeaderClose)

    modalHeaderClose.addEventListener('click', () => {
        modal.remove();
    })

    return modalContentHeader
}