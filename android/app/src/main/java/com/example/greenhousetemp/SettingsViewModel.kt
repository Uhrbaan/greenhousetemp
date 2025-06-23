package com.example.greenhousetemp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    // Get an instance of SettingsDataStore
    private val settingsDataStore = SettingsDataStore(application.applicationContext)

    // Expose settings as StateFlow so Composables can observe them
    val apiUrl: StateFlow<String> = settingsDataStore.apiUrlFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Keep active for 5s after last subscriber
            initialValue = SettingsDataStore.DEFAULT_API_URL // Initial value before first emission
        )

    val port: StateFlow<Int> = settingsDataStore.portFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsDataStore.DEFAULT_PORT
        )

    val refreshInterval: StateFlow<Int> = settingsDataStore.refreshIntervalFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SettingsDataStore.DEFAULT_REFRESH_INTERVAL
        )

    val coldLimit: StateFlow<Float> = settingsDataStore.coldLimitFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SettingsDataStore.DEFAULT_COLD_LIMIT
        )

    val hotLimit: StateFlow<Float> = settingsDataStore.hotLimitFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SettingsDataStore.DEFAULT_HOT_LIMIT
        )

    // --- Functions to update settings ---

    fun updateApiUrl(newUrl: String) {
        viewModelScope.launch {
            settingsDataStore.saveApiUrl(newUrl)
        }
    }

    fun updateRefreshInterval(newInterval: String) { // Take String to handle TextField input
        newInterval.toIntOrNull()?.let {
            viewModelScope.launch {
                settingsDataStore.saveRefreshInterval(it)
            }
        }
    }

    fun updateColdLimit(newLimit: String) {
        newLimit.toFloatOrNull()?.let {
            viewModelScope.launch {
                settingsDataStore.saveColdLimit(it)
            }
        }
    }

    fun updateHotLimit(newLimit: String) {
        newLimit.toFloatOrNull()?.let {
            viewModelScope.launch {
                settingsDataStore.saveHotLimit(it)
            }
        }
    }
}