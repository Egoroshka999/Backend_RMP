package models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime


@Serializable
data class LogMessage(
    val timestamp: String = LocalDateTime.now().toString(),
    val message: String,
    val level: String = "INFO",
    var source : String,
)