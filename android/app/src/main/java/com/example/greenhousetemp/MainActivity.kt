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
import androidx.compose.material3.Button
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
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.app.PendingIntent
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.core.app.NotificationManagerCompat


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create the notification channel
        createNotificationChannel()
        enableEdgeToEdge()
        setContent {
            GreenhouseTempTheme {
                MainScreen()
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(
                                    this, // 'this' refers to MainActivity context
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                showNotification(this) // Pass 'this' as context
                            } else {
                                askNotificationPermission()
                            }
                        } else {
                            // For Android versions below 13, permission is granted at install time
                            showNotification(this) // Pass 'this' as context
                        }
                    },
                    // ... other Button parameters ...
                    content = { Text("Show Notification") } // Added example content for the button
                )
            }
        }
    }

    companion object {
        private var CHANNEL_ID = "greenhousetemp_notification_channel_id"
    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted. You can now send notifications.
            println("Notification permission granted!")
        } else {
            // Permission denied. Explain to the user why you need the permission
            // or disable features that rely on notifications.
            println("Notification permission denied.")
        }
    }

    fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission already granted
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Explain to the user why you need the permission
                // Show an educational UI here
                // Then launch the permission request
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Directly request the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Channel Name" // User-visible name
            val descriptionText = "Description of my notification channel" // User-visible description
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("My Notification Title")
            .setContentText("This is the detailed text of my notification.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set priority for older Android versions

        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
        val notificationId = 1 // A unique ID for this notification
        notificationManager.notify(notificationId, builder.build())
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
    InformationPage()
}