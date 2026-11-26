package com.nexo.messenger

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

private val Night = Color(0xFF07111F)
private val DeepBlue = Color(0xFF102947)
private val Cyan = Color(0xFF75D7FF)
private val White = Color(0xFFF2F7FF)
private val Muted = Color(0xFF9CB2CC)
private val GlassLine = Color(0xFF8CCFFF).copy(alpha = 0.22f)

data class Chat(
    val name: String,
    val message: String,
    val time: String,
    val initials: String,
    val unread: Int = 0,
    val color: Color = Color(0xFF287BC5)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Cyan,
                    background = Night,
                    surface = DeepBlue
                )
            ) {
                NexoApp()
            }
        }
    }
}

@Composable
fun NexoApp() {
    var selectedTab by remember { mutableStateOf("Chats") }
    var selectedChat by remember { mutableStateOf<Chat?>(null) }
    var search by remember { mutableStateOf("") }
    var showAddContact by remember { mutableStateOf(false) }

    val chats = remember {
        mutableStateListOf(
            Chat("María", "¿Cómo estás? 😊", "1:42 PM", "M", 2),
            Chat("Carlos", "Nos vemos más tarde", "12:30 PM", "C", 1, Color(0xFF7255C7)),
            Chat("Familia", "Ya llegamos a casa ❤️", "11:18 AM", "F", 3, Color(0xFF168F87)),
            Chat("Ana", "Te envié las fotos", "Ayer", "A", 0, Color(0xFFB36A9B)),
            Chat("Pedro", "Perfecto, gracias", "Ayer", "P", 0, Color(0xFFB47B3D))
        )
    }

    val messages = remember { mutableStateListOf<String>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF102D50),
                        Night,
                        Color(0xFF081323)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (selectedChat == null) {
                when (selectedTab) {
                    "Chats" -> {
                        ChatHome(
                            chats = chats,
                            search = search,
                            onSearch = { search = it },
                            onOpenChat = {
                                selectedChat = it
                                messages.clear()
                                messages.add("¡Hola! Bienvenido a NEXO 👋")
                                messages.add("Esta conversación está lista para conectarse.")
                            },
                            onAddContact = { showAddContact = true }
                        )
                    }
                    "Contactos" -> SimplePage(
                        "Contactos",
                        "Personas con las que podrás comunicarte directamente."
                    )
                    "Conexiones" -> SimplePage(
                        "Dispositivos cercanos",
                        "Bluetooth BLE y Wi-Fi Direct\n\nLos servicios de conexión se implementarán en la siguiente etapa."
                    )
                    else -> SimplePage(
                        "Tu perfil",
                        "NEXO • Red privada sin internet"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                BottomNavigation(
                    selected = selectedTab,
                    onSelect = { selectedTab = it }
                )
            } else {
                ChatScreen(
                    chat = selectedChat!!,
                    messages = messages,
                    onBack = { selectedChat = null },
                    onSend = { message ->
                        if (message.isNotBlank()) messages.add(message)
                    }
                )
            }
        }
    }
}

@Composable
fun ChatHome(
    chats: List<Chat>,
    search: String,
    onSearch: (String) -> Unit,
    onOpenChat: (Chat) -> Unit,
    onAddContact: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "NEXO",
                    color = White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 5.sp
                )
                Text(
                    "TU RED. SIN INTERNET.",
                    color = Cyan,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )
            }
            GlassPanel(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onAddContact() },
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("＋", color = Cyan, fontSize = 27.sp)
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Tus conversaciones", color = White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                Text("Mensajes entre dispositivos cercanos", color = Muted, fontSize = 12.sp)
            }
            Text("●", color = Color(0xFF51E0B0), fontSize = 11.sp)
            Spacer(Modifier.width(5.dp))
            Text("LOCAL", color = Cyan, fontSize = 10.sp, letterSpacing = 1.sp)
        }

        Spacer(Modifier.height(20.dp))

        GlassPanel(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⌕", color = Cyan, fontSize = 23.sp)
                Spacer(Modifier.width(10.dp))
                BasicTextField(
                    value = search,
                    onValueChange = onSearch,
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = White,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        Box {
                            if (search.isEmpty()) {
                                Text("Buscar conversaciones...", color = Muted, fontSize = 14.sp)
                            }
                            inner()
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(25.dp))

        Text(
            "MENSAJES RECIENTES",
            color = Cyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(11.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(chats.filter {
                it.name.contains(search, ignoreCase = true) ||
                    it.message.contains(search, ignoreCase = true)
            }) { chat ->
                ChatRow(chat = chat, onClick = { onOpenChat(chat) })
            }
        }
    }
}

@Composable
fun ChatRow(chat: Chat, onClick: () -> Unit) {
    GlassPanel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(53.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(chat.color, chat.color.copy(alpha = 0.45f))
                        ),
                        CircleShape
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(chat.initials, color = White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(chat.name, color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text(chat.message, color = Muted, fontSize = 12.sp, maxLines = 1)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(chat.time, color = Muted, fontSize = 10.sp)
                Spacer(Modifier.height(8.dp))
                if (chat.unread > 0) {
                    Box(
                        modifier = Modifier
                            .background(Cyan, CircleShape)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text("${chat.unread}", color = Night, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatScreen(
    chat: Chat,
    messages: List<String>,
    onBack: () -> Unit,
    onSend: (String) -> Unit
) {
    var draft by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("‹", color = Cyan, fontSize = 36.sp, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(43.dp)
                    .background(chat.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(chat.initials, color = White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(chat.name, color = White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("Conversación local", color = Cyan, fontSize = 11.sp)
            }
        }

        HorizontalDivider(color = GlassLine)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            messages.forEachIndexed { index, message ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (index >= 2) Arrangement.End else Arrangement.Start
                ) {
                    GlassPanel(
                        modifier = Modifier.widthIn(max = 290.dp),
                        shape = RoundedCornerShape(18.dp),
                        tint = if (index >= 2) Color(0xFF155A91) else Color(0xFF1A2D48)
                    ) {
                        Text(
                            message,
                            color = White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }

        GlassPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    textStyle = androidx.compose.ui.text.TextStyle(color = White, fontSize = 14.sp),
                    decorationBox = { inner ->
                        Box {
                            if (draft.isEmpty()) Text("Escribe un mensaje...", color = Muted, fontSize = 14.sp)
                            inner()
                        }
                    }
                )
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .background(Cyan, CircleShape)
                        .clickable {
                            onSend(draft)
                            draft = ""
                        }
                        .padding(horizontal = 15.dp, vertical = 9.dp)
                ) {
                    Text("➤", color = Night, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SimplePage(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(22.dp)
    ) {
        Spacer(Modifier.height(22.dp))
        Text("NEXO", color = Cyan, fontSize = 13.sp, letterSpacing = 4.sp)
        Spacer(Modifier.height(28.dp))
        Text(title, color = White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        GlassPanel(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                description,
                color = Muted,
                fontSize = 15.sp,
                lineHeight = 24.sp,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Composable
fun BottomNavigation(selected: String, onSelect: (String) -> Unit) {
    val tabs = listOf(
        "Chats" to "◉",
        "Contactos" to "♙",
        "Conexiones" to "⌁",
        "Perfil" to "◎"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .background(
                Color(0xFF142B47).copy(alpha = 0.88f),
                RoundedCornerShape(25.dp)
            )
            .border(1.dp, GlassLine, RoundedCornerShape(25.dp))
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEach { (name, symbol) ->
            Column(
                modifier = Modifier
                    .clickable { onSelect(name) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(symbol, color = if (selected == name) Cyan else Muted, fontSize = 21.sp)
                Text(
                    name,
                    color = if (selected == name) Cyan else Muted,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(20.dp),
    tint: Color = Color(0xFF24496F),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    listOf(
                        tint.copy(alpha = 0.66f),
                        tint.copy(alpha = 0.30f),
                        Color(0xFF10233C).copy(alpha = 0.72f)
                    )
                ),
                shape
            )
            .border(1.dp, GlassLine, shape)
    ) {
        content()
    }
}
