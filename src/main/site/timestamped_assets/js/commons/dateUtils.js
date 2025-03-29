import { emptyOrUndefined } from "./common.js";

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

export function getDateWithoutTime(date) {
    return date.getFullYear() + '/' + (date.getMonth() + 1) + '/' + date.getDate();
}