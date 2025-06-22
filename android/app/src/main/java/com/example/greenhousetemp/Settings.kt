package com.example.greenhousetemp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsPage(modifier: Modifier = Modifier) {
    var apiUrl by remember { mutableStateOf("http://192.168.1.121") }
    var port by remember { mutableStateOf("8080") }

    // in °C
    var coldLimit by remember { mutableStateOf("15") }
    var hotLimit by remember { mutableStateOf("35") }

    // in minutes
    var interval by remember { mutableStateOf("5") }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Text(
            text = "Paramètres",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "URL & Port de l'API",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            SettingsURLItem(
                label = "URL",
                value = apiUrl,
                onValueChange = { apiUrl = it }
            )
            SettingsNumberItem(
                label = "Port",
                value = port,
                onValueChange = { port = it }
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Avertissements de température",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            SettingsNumberItem(
                label = "Température basse (°C)",
                value = coldLimit,
                onValueChange = { coldLimit = it }
            )
            SettingsNumberItem(
                label = "Température haute (°C)",
                value = hotLimit,
                onValueChange = { hotLimit = it }
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Intervale de raffraîchissement",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            SettingsNumberItem(
                label = "Intervale (minutes)",
                value = interval,
                onValueChange = { interval = it }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPagePreview() {
    SettingsPage()
}

@Composable
fun SettingsURLItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsURLItemPreview() {
    SettingsURLItem(
        label = "URL",
        value = "https://example.com",
        onValueChange = {}
    )
}

@Composable
fun SettingsNumberItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.defaultMinSize(minWidth = 10.dp).padding(start = 8.dp),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsNumberItemPreview() {
    SettingsNumberItem(
        label = "Port",
        value = "8080",
        onValueChange = {}
    )
}