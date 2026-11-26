package com.nexo.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF0B1428)
private val Blue = Color(0xFF168BFF)
private val Ice = Color(0xFF79CCFF)
private val Glass = Color(0xFF17345D)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Blue,
                    secondary = Ice,
                    background = Navy,
                    surface = Glass
                )
            ) {
                NexoHome()
            }
        }
    }
}

@Composable
fun NexoHome() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Navy, Color(0xFF10294B), Color(0xFF07101F))
                )
            )
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "NEXO",
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 7.sp
            )

            Text(
                text = "CONNECT WITHOUT INTERNET",
                color = Ice,
                fontSize = 11.sp,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Glass.copy(alpha = 0.72f)
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Tu red. Tus conexiones.",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Comunícate directamente con otros teléfonos, sin internet.",
                        color = Color(0xFFD0DCEC),
                        fontSize = 14.sp
                    )
                }
            }

            Text(
                text = "CONECTIVIDAD",
                color = Ice,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            ConnectionCard("Bluetooth BLE", "Descubrimiento de dispositivos", "Preparado para implementar")
            ConnectionCard("Wi-Fi Direct", "Conexión rápida entre Android", "Preparado para implementar")

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "NEXO 0.1.0  •  RED LOCAL PRIVADA",
                color = Color(0xFF8FA8C8),
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ConnectionCard(title: String, description: String, status: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF142B49).copy(alpha = 0.85f)
        )
    ) {
        Row(
            modifier = Modifier.padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = Color(0xFFD0DCEC), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(7.dp))
                Text(status, color = Ice, fontSize = 11.sp)
            }
        }
    }
}
