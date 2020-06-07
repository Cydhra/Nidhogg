package net.cydhra.nidhogg.data

import kotlinx.serialization.Serializable

typealias MetricKey = String

/**
 * A collection of metrics that can be requested at the Mojang API and some convenience functions to collect multiple
 * metrics that often belong together
 */
object MetricKeys {
    const val MINECRAFT_ITEMS_SOLD: MetricKey = "item_sold_minecraft"
    const val MINECRAFT_PREPAID_REDEEMED: MetricKey = "prepaid_card_redeemed_minecraft"

    const val COBALT_ITEMS_SOLD: MetricKey = "item_sold_cobalt"
    const val COBALT_PREPAID_REDEEMED: MetricKey = "prepaid_card_redeemed_cobalt"

    const val SCROLLS_ITEMS_SOLD: MetricKey = "item_sold_scrolls"

    const val DUNGEONS_ITEMS_SOLD: MetricKey = "item_sold_dungeons"

    /**
     * Generate a list of all metric keys
     */
    fun all(): List<MetricKey> = listOf(
            MINECRAFT_ITEMS_SOLD,
            MINECRAFT_PREPAID_REDEEMED,
            COBALT_ITEMS_SOLD,
            COBALT_PREPAID_REDEEMED,
            SCROLLS_ITEMS_SOLD,
            DUNGEONS_ITEMS_SOLD
    )

    /**
     * Generate a list of all Minecraft metrics
     */
    fun minecraft(): List<MetricKey> = listOf(
            MINECRAFT_ITEMS_SOLD,
            MINECRAFT_PREPAID_REDEEMED
    )

    /**
     * Generate a list of all Cobalt metrics
     */
    fun cobalt(): List<MetricKey> = listOf(
            COBALT_ITEMS_SOLD,
            COBALT_PREPAID_REDEEMED
    )

    /**
     * Generate a list of all Scrolls metrics
     */
    fun scrolls(): List<MetricKey> = listOf(
            SCROLLS_ITEMS_SOLD
    )

    /**
     * Generate a list of all Minecraft Dungeons metrics
     */
    fun dungeons(): List<MetricKey> = listOf(
            DUNGEONS_ITEMS_SOLD
    )
}

/**
 * The sum of all requested metrics.
 *
 * @param total total summed sales of the requested metrics
 * @param last24h total summed sales of all requested metrics within the last 24 hours
 * @param saleVelocityPerSeconds average sales per second of all requested metrics
 */
@Serializable
data class SaleMetrics(
        val total: Int,
        val last24h: Int,
        val saleVelocityPerSeconds: Double
)