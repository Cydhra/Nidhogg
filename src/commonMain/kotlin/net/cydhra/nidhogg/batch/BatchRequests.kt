package net.cydhra.nidhogg.batch

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import net.cydhra.nidhogg.data.SkinProfile
import net.cydhra.nidhogg.mojang.MojangClient

/**
 * Batch-request multiple [SkinProfile]s using multiple [Uuid]s. Due to rate-limiting, no profile may be requested
 * twice using this method, which is why a [Set] of UUIDs is taken.
 *
 * @param uuids a set of different UUIDs
 *
 * @return a list of [SkinProfile] entries. Those entries will contain the account's username and some information
 * like the skin data.
 */
suspend fun MojangClient.batchRequestProfilesByUuid(uuids: Set<Uuid>): List<SkinProfile> = coroutineScope {
    // no additional measures are taken to abide by the rate limits, since there seem to be no rate limits for
    // querying multiple different profiles. To avoid state, the rate limit of requesting the same profile multiple 
    // times is taken care of by the user.
    return@coroutineScope uuids
            .map { async { this@batchRequestProfilesByUuid.getProfileByUUID(it) } }
            .map { it.await() }
}