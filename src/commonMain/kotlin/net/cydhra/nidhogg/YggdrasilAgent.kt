package net.cydhra.nidhogg

import kotlinx.serialization.Serializable

/**
 * The agent used for authentication. It defines for which service of Yggdrasil you are authenticating. Use
 * [MinecraftAgent] and [ScrollsAgent] for convenience.
 *
 * @param name name of the service/game you are authenticating at
 * @param version agent version. This is currently always "1", but this may change in future.
 */
@Serializable
open class YggdrasilAgent(val name: String, val version: Int)

/**
 * Convenience agent instance for Minecraft.
 */
object MinecraftAgent : YggdrasilAgent("Minecraft", 1)

/**
 * Convenience agent instance for Scrolls.
 */
object ScrollsAgent : YggdrasilAgent("Scrolls", 1)