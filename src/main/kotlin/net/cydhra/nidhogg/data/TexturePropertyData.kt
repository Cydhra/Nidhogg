package net.cydhra.nidhogg.data

import java.net.URL

/**
 * Data encoded within the [ProfileProperty.value] containing all data about player skins
 *
 * @param timestamp a java timestamp in ms of the request. Possibly in the past, probably due to caching.
 * @param profileId the profile UUID without hyphens of this object's profile
 * @param signatureRequired present and true, if the [ProfileProperty] containing this object is signed
 * @param textures a [Textures] compound object
 */
data class TexturePropertyData(val timestamp: Long, val profileId: String, val profileName: String, val signatureRequired: Boolean?, val textures: Textures)

/**
 * Compound object for player textures within [TexturePropertyData]
 *
 * @param SKIN player skin [Texture] or null if player has default skin
 * @param CAPE player cape [Texture] or null if player has no cape texture
 */
data class Textures(val SKIN: Texture?, val CAPE: Texture?)

/**
 * Texture object for [Textures] compound object in [TexturePropertyData]. Contains a URL pointing to the texture resource and optional
 * metadata.
 *
 * @param url texture URL
 * @param metadata only present for slim player skin model
 */
data class Texture(val url: URL, val metadata: ModelMetadata?)

/**
 * Metadata about a player skin model. Only present in [Texture] if [model] == "slim"
 *
 * @param model always "slim"
 */
data class ModelMetadata(val model: String)