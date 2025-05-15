package com.Backend_RMP.models

import com.Backend_RMP.config.RequestCategory

data class LoadTestResult(
    val timestamp: String,
    val totalRequests: Int,
    val byCategory: Map<RequestCategory, Pair<Int, Int>>,
    val duration: String
)