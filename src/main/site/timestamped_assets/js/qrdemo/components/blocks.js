import { getBigButton } from "./buttons.js"

export function getNavigationMenu() {
    let navigation = document.createElement('div')
    navigation.className = 'basic_card'

    let navButtons = document.createElement('div')
    navButtons.className = 'nav_menu'

    let cardsBtn = getBigButton('Карточки')
    let formsBtn = getBigButton('Шаблоны')
    let filesBtn = getBigButton('Файлы')

    cardsBtn.addEventListener('click', () => {
        window.location = "?lk=true"
    })
    formsBtn.addEventListener('click', () => {
        window.location = "?form=true"
    })
    filesBtn.addEventListener('click', () => {
        window.location = "?files=true"
    })

    navButtons.append(cardsBtn, formsBtn, filesBtn)
    navigation.append(navButtons)
    return navigation
}