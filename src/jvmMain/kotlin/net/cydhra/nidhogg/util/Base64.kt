package net.cydhra.nidhogg.util

import java.util.*

actual fun String.encodeBase64(): String {
    return String(Base64.getEncoder().encode(this.toByteArray()))
}

actual fun String.decodeBase64(): String {
    return String(Base64.getDecoder().decode(this))
}