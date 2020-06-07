package net.cydhra.nidhogg.exception

/**
 * Returned by Yggdrasil API server, if the account is migrated, but the provided username was not an email address but
 * the account's username.
 */
class UserMigratedException(message: String) : ForbiddenOperationException(message)