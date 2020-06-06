package net.cydhra.nidhogg.util

/**
 * Extension function provided through actual implementations to decode a base64-encoded string. Actual
 * implementations will be replaced as soon as a common implementation is available.
 */
expect fun String.decodeBase64(): String

/**
 * Extension function provided through actual implementations to encode a string as a base64-string. Actual
 * implementations will be replaced as soon as a common implementation is available.
 */
expect fun String.encodeBase64(): String