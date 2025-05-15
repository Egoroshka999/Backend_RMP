package com.Backend_RMP.config

object AppConfig {
    const val TARGET_URL = "http://localhost:8080"
    const val TOTAL_REQUESTS = 10_000
    const val PARALLEL_COROUTINES = 100
    const val REQUEST_TIMEOUT = 5000L
    const val LOG_DIR_PATH = "logs"
    const val TEST_USER = "user1"
    const val TEST_PASSWORD = "test1234"
    const val TEST_AGE = 30
    const val TEST_WEIGHT = 72.5
    const val TEST_HEIGHT = 178.0
    const val TEST_GENDER = "male"
    const val TEST_GOAL = "похудеть"

    const val rabbitHost = "localhost"
    const val rabbitPort = 5672
    const val rabbitUsername = "guest"
    const val rabbitPassword = "guest"
    const val rabbitQueueName = "log_queue"
    const val rabbitVirtualHost = "/"
}
