export class Loader {

    constructor() {
        this.loader = this.#getLoader()
    }

    showLoader() {
        this.loader.classList.add('show')
    }

    hideLoader() {
        this.loader.classList.remove('show')
    }

    destroy() {
        if (this.loader !== null) {
            this.loader.remove()
        }
    }

    get() {
        return this.loader
    }

    #getLoader() {
        let loaderDiv = document.createElement('div')
        loaderDiv.className = 'loader'
        return loaderDiv
    }
}
