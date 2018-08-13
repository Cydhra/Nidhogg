package net.cydhra.nidhogg.data

/**
 * A profile property containing some base64 encoded data
 *
 * @param name name of the property.
 * @param value base64 encoded property data
 * @param signature optional. base64 encoded signature using Yggdrasil's private key
 */
data class ProfileProperty(val name: String, val value: String, val signature: String?)