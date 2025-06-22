package com.example.greenhousetemp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.greenhousetemp.ui.theme.GreenhouseTempTheme
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GreenhouseTempTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun InformationCard(title: String, description: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            modifier = modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = description,
            fontSize = 64.sp,
            fontWeight = FontWeight.Thin,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InformationPreview() {
    InformationCard("Température", "35°C")
}

@Composable
fun InformationPage(modifier: Modifier = Modifier) {
    var greenhouseData by remember { mutableStateOf<GreenhouseData?>(null) }
    val apiService = remember { GreenhouseTempAPIService() }
    val lifecycleOwner = LocalLifecycleOwner.current

    // check every five seconds in a coroutine
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (isActive) {
                val data = apiService.getGreenhouseData()
                if (data != null) {
                    greenhouseData = data
                } else {
                    println("Failed to fetch greenhouse data, keeping previous state.")
                }
                delay(5000)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        InformationCard(
            title = "Température",
            description = greenhouseData?.let { data ->
                "${data.temperature}°C"
            } ?: "..." )

        InformationCard(
            title = "Humidité", description = greenhouseData?.let { data ->
                "${data.humidity * 100} %"
            } ?: "...")
    }
}

@Composable
fun MainScreen(defaultPage: Int = 0) {
    var selectedItem by remember { mutableStateOf(defaultPage) }
    val items = listOf("Maison", "Historique", "Paramètres")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.DateRange, Icons.Filled.Settings)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index}
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedItem) {
            0 -> InformationPage()
            1 -> Text(text = "Work in Progress: History", modifier = Modifier.padding(innerPadding))
            2 -> SettingsPage(Modifier.padding(innerPadding))
        }
    }
}