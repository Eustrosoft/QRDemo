import { emptyOrUndefined } from "./common";

export function getDateTime(date) {
    if (emptyOrUndefined(date)) {
        return ''
    }
    return date.toLocaleString()
}