package com.genshinlauncher.app

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

data class AppInfo(
    val packageName: String,
    val label: String,
    val icon: Drawable?
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var currentAccentColor by remember { mutableStateOf(Color(0xFFD4AF37)) } // Default Gold

            GenshinTheme(accentColor = currentAccentColor) {
                GenshinHomeScreen(
                    onElementSelected = { newColor ->
                        currentAccentColor = newColor
                    }
                )
            }
        }
    }
}

@Composable
fun GenshinTheme(
    accentColor: Color,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = accentColor,
        onPrimary = Color.Black,
        background = Color(0xFF0D1B2A),
        surface = Color(0xFF1B2A4A),
        onBackground = Color.White,
        onSurface = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

@Composable
fun GenshinHomeScreen(onElementSelected: (Color) -> Unit) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    var apps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var showSetLauncherHint by remember { mutableStateOf(true) }

    // Load installed apps
    LaunchedEffect(Unit) {
        apps = loadInstalledApps(packageManager)
    }

    val filteredApps = remember(searchQuery, apps) {
        if (searchQuery.isBlank()) apps
        else apps.filter {
            it.label.contains(searchQuery, ignoreCase = true) ||
                    it.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // === HEADER ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GENSHIN IMPACT",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD4AF37)
                        )
                    )
                    Text(
                        text = "Welcome back, Traveler",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB8C4D4)
                    )
                }

                // Quick action: Set as default launcher
                if (showSetLauncherHint) {
                    TextButton(
                        onClick = {
                            val intent = Intent(android.provider.Settings.ACTION_HOME_SETTINGS)
                            context.startActivity(intent)
                            showSetLauncherHint = false
                        }
                    ) {
                        Text("Set as Default", color = Color(0xFFD4AF37))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // === WIDGETS ROW ===
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFD4AF37),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Widget 1: Clock
                ClockWidget(modifier = Modifier.weight(1f))

                // Widget 2: Elemental Theme Switcher
                ElementalThemeWidget(
                    onElementSelected = onElementSelected,
                    modifier = Modifier.weight(1f)
                )

                // Widget 3: Adventure Stats
                StatsWidget(
                    appCount = apps.size,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // === SEARCH ===
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search apps or packages...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD4AF37),
                    unfocusedBorderColor = Color(0xFF4A5A7A)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // === APP GRID ===
            Text(
                text = "Your Apps (${filteredApps.size})",
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFFD4AF37),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredApps, key = { it.packageName }) { app ->
                    AppCard(
                        app = app,
                        onClick = { launchApp(context, app.packageName) }
                    )
                }
            }
        }
    }
}

@Composable
fun ClockWidget(modifier: Modifier = Modifier) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Calendar.getInstance()
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now.time)
            currentDate = SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(now.time)
            delay(30_000L) // update every 30 seconds
        }
    }

    Card(
        modifier = modifier
            .fillMaxHeight()
            .border(1.5.dp, Color(0xFFD4AF37), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2A4A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentTime,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD4AF37)
                )
            )
            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFB8C4D4)
            )
            Text(
                text = "Teyvat Time",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ElementalThemeWidget(
    onElementSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val elements = listOf(
        "Anemo" to Color(0xFF7ED7C1),
        "Pyro" to Color(0xFFFF6B6B),
        "Hydro" to Color(0xFF4ECDC4),
        "Electro" to Color(0xFF9D4EDD),
        "Dendro" to Color(0xFF7CB342),
        "Cryo" to Color(0xFF00CED1),
        "Geo" to Color(0xFFD4A017)
    )

    Card(
        modifier = modifier
            .fillMaxHeight()
            .border(1.5.dp, Color(0xFFD4AF37), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2A4A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Elemental Resonance",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFD4AF37)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                elements.forEach { (name, color) ->
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                            .clickable { onElementSelected(color) }
                    )
                }
            }
            Text(
                text = "Tap to change theme",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun StatsWidget(appCount: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .border(1.5.dp, Color(0xFFD4AF37), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2A4A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = appCount.toString(),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD4AF37)
                )
            )
            Text(
                text = "Apps in Teyvat",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFB8C4D4)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Daily Commissions: 4/4 ✓",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF7ED7C1)
            )
        }
    }
}

@Composable
fun AppCard(app: AppInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clickable { onClick() }
            .border(1.dp, Color(0xFFD4AF37), RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2A4A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Icon using AndroidView (real icon from system)
            if (app.icon != null) {
                AndroidView(
                    factory = { ctx ->
                        ImageView(ctx).apply {
                            setImageDrawable(app.icon)
                            scaleType = ImageView.ScaleType.FIT_CENTER
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF4A5A7A), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.label.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = app.label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

fun loadInstalledApps(pm: PackageManager): List<AppInfo> {
    val apps = mutableListOf<AppInfo>()
    val intent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    val resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)

    for (ri in resolveInfos) {
        try {
            val packageName = ri.activityInfo.packageName
            val label = ri.loadLabel(pm).toString().ifBlank { packageName }
            val icon = try {
                pm.getApplicationIcon(packageName)
            } catch (e: Exception) {
                null
            }
            apps.add(AppInfo(packageName, label, icon))
        } catch (e: Exception) {
            // Skip problematic apps
        }
    }
    return apps.distinctBy { it.packageName }.sortedBy { it.label.lowercase() }
}

fun launchApp(context: Context, packageName: String) {
    try {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    } catch (e: Exception) {
        // Handle error silently or show toast in real app
    }
}