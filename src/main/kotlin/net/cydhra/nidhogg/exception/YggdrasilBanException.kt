package net.cydhra.nidhogg.exception

/**
 * Thrown when attempting to log into Yggdrasil account but Yggdrasil rejects the connection because it banned the IP address.
 */
class YggdrasilBanException(message: String) : RuntimeException(message)
