package com.example.greenhousetemp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Define a top-level property for the DataStore instance using the preferencesDataStore delegate
// The name "settings" will be the filename for the DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    // Define Preference Keys (these are type-safe keys)
    companion object {
        val API_URL_KEY = stringPreferencesKey("api_url")
        val PORT_KEY = intPreferencesKey("port")
        val REFRESH_INTERVAL_KEY = intPreferencesKey("refresh_interval_minutes")
        val COLD_LIMIT_KEY = floatPreferencesKey("cold_limit_celsius")
        val HOT_LIMIT_KEY = floatPreferencesKey("hot_limit_celsius")

        // Default values
        const val DEFAULT_API_URL = "http://192.168.1.121"
        const val DEFAULT_PORT = 8080
        const val DEFAULT_REFRESH_INTERVAL = 5 // minutes
        const val DEFAULT_COLD_LIMIT = 15.0f // °C
        const val DEFAULT_HOT_LIMIT = 35.0f // °C
    }

    // Flow to read the API URL
    val apiUrlFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[API_URL_KEY] ?: DEFAULT_API_URL
        }

    // Function to save the API URL
    suspend fun saveApiUrl(apiUrl: String) {
        context.dataStore.edit { preferences ->
            preferences[API_URL_KEY] = apiUrl
        }
    }

    val portFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PORT_KEY] ?: DEFAULT_PORT
        }

    suspend fun savePort(port: Int) {
        context.dataStore.edit { preferences ->
            preferences[PORT_KEY] = port
        }
    }

    // Flow to read the Refresh Interval
    val refreshIntervalFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[REFRESH_INTERVAL_KEY] ?: DEFAULT_REFRESH_INTERVAL
        }

    // Function to save the Refresh Interval
    suspend fun saveRefreshInterval(interval: Int) {
        context.dataStore.edit { preferences ->
            preferences[REFRESH_INTERVAL_KEY] = interval
        }
    }

    // Flow to read the Cold Limit
    val coldLimitFlow: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[COLD_LIMIT_KEY] ?: DEFAULT_COLD_LIMIT
        }

    // Function to save the Cold Limit
    suspend fun saveColdLimit(limit: Float) {
        context.dataStore.edit { preferences ->
            preferences[COLD_LIMIT_KEY] = limit
        }
    }

    // Flow to read the Hot Limit
    val hotLimitFlow: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[HOT_LIMIT_KEY] ?: DEFAULT_HOT_LIMIT
        }

    // Function to save the Hot Limit
    suspend fun saveHotLimit(limit: Float) {
        context.dataStore.edit { preferences ->
            preferences[HOT_LIMIT_KEY] = limit
        }
    }

    // Optional: A combined function to save all settings at once
    suspend fun saveAllSettings(
        apiUrl: String,
        refreshInterval: Int,
        coldLimit: Float,
        hotLimit: Float
    ) {
        context.dataStore.edit { preferences ->
            preferences[API_URL_KEY] = apiUrl
            preferences[REFRESH_INTERVAL_KEY] = refreshInterval
            preferences[COLD_LIMIT_KEY] = coldLimit
            preferences[HOT_LIMIT_KEY] = hotLimit
        }
    }
}