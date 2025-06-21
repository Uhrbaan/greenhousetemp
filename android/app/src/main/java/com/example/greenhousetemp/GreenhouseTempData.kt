package com.example.greenhousetemp

import kotlinx.serialization.Serializable
import java.sql.Timestamp

/**
 * {
 *     "timestamp": "2025-06-21T14:34:20Z",
 *     "temperature": -10,
 *     "humidity": 0.4
 * }
 */
@Serializable
data class GreenhouseData(
    val timestamp: String,
    val temperature: Double,
    val humidity: Double,
)