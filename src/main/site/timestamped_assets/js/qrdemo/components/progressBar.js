export function tryAppendPercentListener(
    progressBar, progressPercent, requestForListen, 
    textBeforeUploaded, textAfterUploaded
) {
    if (progressBar && progressPercent && requestForListen) {
        requestForListen.upload.addEventListener("progress", function (e) {
            if (e.lengthComputable) {
                let percent = Math.round(e.loaded / e.total * 100);

                if (percent < 100) {
                    progressBar.style.width = percent + '%';
                    progressPercent.innerText = `${textBeforeUploaded} - ${percent}%`;
                } else {
                    progressBar.style.width = '100%';
                    progressPercent.innerText = `${textAfterUploaded}`;
                }
            }
        }, false)
    }
}

export class ProgressBar {

    constructor(textBeforeUploaded = '', textAfterUploaded = 'Uploaded') {
        this.progressBar = this.#getProgressBar()
        this.textBeforeUploaded = textBeforeUploaded
        this.textAfterUploaded = textAfterUploaded
    }

    showLoader() {
        this.progressBar.classList.add('show')
    }

    hideLoader() {
        this.progressBar.classList.remove('show')
    }

    destroy() {
        if (this.progressBar !== null) {
            this.progressBar.remove()
        }
    }

    start(request, partIndex = 0, totalParts = 1) {
        if (this.bar && this.percent && this.requests) {
            request.upload.addEventListener("progress", function (e) {
                if (e.lengthComputable) {
                    let pc = Math.round(e.loaded / e.total * 100)
                            / totalParts + (partIndex * 100 / totalParts);
    
                    if (pc < 100) {
                        this.bar.style.width = pc + '%';
                        this.percent.innerText = `${this.textBeforeUploaded} - ${pc}%`;
                    } else {
                        this.bar.style.width = '100%';
                        this.percent.innerText = `${this.textAfterUploaded}`;
                    }
                }
            }, false)
        }
    }

    get() {
        return this.progressBar
    }

    #getProgressBar() {
        let progressWrapper = document.createElement('div')
        progressWrapper.classList.add('progress_wrapper')

        let progressPercent = document.createElement('div')
        progressPercent.classList.add('progress_percent')
        let progressBar = document.createElement('div')
        progressBar.classList.add('progress_bar')

        progressWrapper.append(progressPercent, progressBar)

        this.wrapper = progressWrapper
        this.bar = progressBar
        this.percent = progressPercent
        return progressWrapper
    }
}
