import { emptyOrUndefined } from "./common";

export function getDateTime(date) {
    if (emptyOrUndefined(date)) {
        return ''
    }
    return date.toLocaleString()
}

export function formatDate(str) {
    if (emptyOrUndefined(str)) {
        return ''
    }
    return new Date(str).toLocaleString()
}