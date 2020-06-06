package net.cydhra.nidhogg.util

import kotlin.browser.window

actual fun String.encodeBase64(): String {
    return window.btoa(this)
}

actual fun String.decodeBase64(): String {
    return window.atob(this)
}