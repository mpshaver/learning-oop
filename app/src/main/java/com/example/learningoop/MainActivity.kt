package com.example.learningoop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningoop.ui.theme.LearningOOPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LearningOOPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SmartHomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SmartHomeScreen(modifier: Modifier = Modifier) {
    val tv = remember { SmartTvDevice("Android TV", "Entertainment") }
    val light = remember { SmartLightDevice("Google Light", "Utility") }
    val home = remember { SmartHome(tv, light) }

    var lines by remember {
        ConsoleOutput.lines.clear()
        mutableStateOf(listOf<String>())
    }

    fun update(action: () -> Unit) {
        action()
        lines = ConsoleOutput.lines.toList()
    }

    val tvIsOn = tv.deviceStatus == "on"
    val lightIsOn = light.deviceStatus == "on"

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Smart Home",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${home.deviceTurnOnCount} of 2 devices on",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            DeviceCard(device = tv) {
                Button(onClick = { update { if (tvIsOn) home.turnOffTv() else home.turnOnTv() } }) {
                    Text(if (tvIsOn) "Turn off" else "Turn on")
                }
                OutlinedButton(onClick = { update { home.increaseTvVolume() } }, enabled = tvIsOn) {
                    Text("Volume +")
                }
                OutlinedButton(onClick = { update { home.changeTvChannelToNext() } }, enabled = tvIsOn) {
                    Text("Channel +")
                }
            }
        }

        item {
            DeviceCard(device = light) {
                Button(onClick = { update { if (lightIsOn) home.turnOffLight() else home.turnOnLight() } }) {
                    Text(if (lightIsOn) "Turn off" else "Turn on")
                }
                OutlinedButton(onClick = { update { home.increaseLightBrightness() } }, enabled = lightIsOn) {
                    Text("Brightness +")
                }
            }
        }

        item {
            OutputCard(lines = lines)
        }
    }
}

@Composable
fun DeviceCard(
    device: SmartDevice,
    modifier: Modifier = Modifier,
    buttons: @Composable () -> Unit
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = device.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${device.deviceType} · ${device.category}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusPill(status = device.deviceStatus)
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                buttons()
            }
        }
    }
}

@Composable
fun StatusPill(status: String, modifier: Modifier = Modifier) {
    val isOn = status == "on"
    val background = if (isOn) Color(0xFF2E7D32) else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isOn) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Text(
        text = status.uppercase(),
        color = textColor,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
fun OutputCard(lines: List<String>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Console output",
                color = Color(0xFF9E9E9E),
                style = MaterialTheme.typography.labelLarge
            )
            if (lines.isEmpty()) {
                Text(
                    text = "Tap a button to see output.",
                    color = Color(0xFF6E6E6E),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
            }
            lines.forEachIndexed { index, line ->
                Row {
                    Text(
                        text = "${index + 1}",
                        color = Color(0xFF6A9955),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = line,
                        color = Color(0xFFD4D4D4),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SmartHomeScreenPreview() {
    LearningOOPTheme {
        SmartHomeScreen()
    }
}
