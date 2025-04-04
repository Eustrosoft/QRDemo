import { setURLParams } from "../../commons/common.js"
import { renderFilesPage } from "../files.js"
import { renderFormPage } from "../form.js"
import { renderLkPage } from "../lk.js"
import { getBigButton } from "./buttons.js"

export function getNavigationMenu() {
    let navigation = document.createElement('div')
    navigation.className = 'basic_card'

    let navButtons = document.createElement('div')
    navButtons.className = 'nav_menu'

    let cardsBtn = getBigButton('Карточки')
    let formsBtn = getBigButton('Шаблоны')
    let filesBtn = getBigButton('Файлы')

    let mainBlock = document.getElementById('main_block')
    if (mainBlock) {
        mainBlock.innerHTML = ''
    }

    let url = new URL(window.location.href);

    cardsBtn.addEventListener('click', () => {
        setURLParams('page=lk')
        renderLkPage()
    })
    formsBtn.addEventListener('click', () => {
        setURLParams('page=forms')
        renderFormPage()
    })
    filesBtn.addEventListener('click', () => {
        setURLParams('page=files')
        renderFilesPage()
    })

    navButtons.append(cardsBtn, formsBtn, filesBtn)
    navigation.append(navButtons)
    return navigation
}