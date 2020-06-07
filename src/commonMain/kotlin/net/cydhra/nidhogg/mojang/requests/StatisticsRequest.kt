package net.cydhra.nidhogg.mojang.requests

import kotlinx.serialization.Serializable
import net.cydhra.nidhogg.data.MetricKey

/**
 * Request entity for statistics. It simply takes all the statistics as a list
 */
@Serializable
internal data class StatisticsRequest(
        val metricKeys: List<MetricKey>
)