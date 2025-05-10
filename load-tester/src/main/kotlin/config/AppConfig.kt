package com.Backend_RMP.config

object AppConfig {
    const val TARGET_URL = "http://localhost:8080"
    const val TOTAL_REQUESTS = 10_000
    const val PARALLEL_COROUTINES = 100
    const val REQUEST_TIMEOUT = 5000L
    const val LOG_FILE_PATH = "logs/load_test.log"
    const val TEST_USER = "user1"
    const val TEST_PASSWORD = "test1234"
}