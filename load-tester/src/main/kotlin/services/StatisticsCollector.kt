package com.Backend_RMP.services

import com.Backend_RMP.config.RequestCategory
import kotlinx.atomicfu.atomic

class StatisticsCollector {
    private val stats = RequestCategory.values().associateWith {
        Pair(atomic(0), atomic(0))
    }

    fun incrementSuccess(category: RequestCategory) {
        stats[category]?.first?.incrementAndGet()
    }

    fun incrementFailure(category: RequestCategory) {
        stats[category]?.second?.incrementAndGet()
    }

    fun getResults(): Map<RequestCategory, Pair<Int, Int>> {
        return stats.mapValues { (_, v) -> v.first.value to v.second.value }
    }
}