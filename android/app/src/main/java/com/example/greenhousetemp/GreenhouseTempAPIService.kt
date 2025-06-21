package com.example.greenhousetemp

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class GreenhouseTempAPIService {
    private val client = HttpClient(Android) { // get the default android http client
        install(ContentNegotiation) { // setup for parsing json
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val baseURL = "http://192.168.1.121:8080"

    // suspend -> can be paused
    suspend fun getGreenhouseData(): GreenhouseData? {
        return try {
            // call the api endpoint
            val response = client.get("$baseURL/data/latest")

            // check the response
            if (response.status.value == 200) {
                response.bodyAsText().let { jsonString ->
                    Json.decodeFromString<GreenhouseData>(jsonString)
                }
            } else {
                println("API Request failed with status: ${response.status.value}")
                null
            }
        } catch (e: Exception) {
            println("Error fetching greenhouse data: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}