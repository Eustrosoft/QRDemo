export function getAccordion(div, text = 'Accordion', textIsHtml = false, expanded = false) {
  let accordion = document.createElement('div')
  let btn = document.createElement('button')
  if (textIsHtml) {
    btn.appendChild(text)
  } else {
    btn.innerText = text
  }
  btn.classList = 'accordion'
  div.classList.add('panel')
  accordion.append(btn, div)

  btn.addEventListener('click', (e) => {
    let accordionElement = findParentByClassName(e.target, 'accordion')
    if (accordionElement) {
      accordionElement.classList.toggle("active");
      let panel = accordionElement.nextElementSibling;
      if (panel.style.maxHeight) {
        panel.style.maxHeight = null;
      } else {
        panel.style.maxHeight = panel.scrollHeight + "px";
      }
    }
  })

  if (expanded) {
    btn.classList.toggle("active");
    let panel = btn.nextElementSibling;
    if (panel.style.maxHeight) {
      panel.style.maxHeight = null;
    } else {
      panel.style.maxHeight = "fit-content";
    }
  }

  return accordion
}

export function findParentByClassName(element, className) {
  let current = element

  while (current && current !== document.body) {
    if (current.classList && current.classList.contains(className)) {
      return current
    }
    current = current.parentElement
  }

  return null
}
