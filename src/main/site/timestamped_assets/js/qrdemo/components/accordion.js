export function getAccordion(div, text = 'Accordion') {
    let accordion = document.createElement('div')
    let btn = document.createElement('button')
    btn.innerText = text
    btn.classList = 'accordion'
    div.classList.add('panel')
    accordion.append(btn, div)

    btn.addEventListener('click', (e) => {
        e.target.classList.toggle("active");
        let panel = e.target.nextElementSibling;
        if (panel.style.maxHeight) {
          panel.style.maxHeight = null;
        } else {
          panel.style.maxHeight = panel.scrollHeight + "px";
        }
    })

    return accordion
}
