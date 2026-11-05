package com.example.menuku.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.menuku.core.navigation.Screen
import com.example.menuku.data.models.NotificationCategory
import com.example.menuku.data.repository.MenuKuRepository
import com.example.ui.theme.*
import com.example.MenuKuApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterScreen(
    repository: MenuKuRepository,
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val notifications by repository.notifications.collectAsState()

    var selectedFilter by remember { mutableStateOf(NotificationCategory.ALL) }
    val categories = NotificationCategory.values().toList()

    val filteredNotifications = notifications.filter {
        if (selectedFilter == NotificationCategory.ALL) true else it.category == selectedFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pusat Notifikasi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    TextButton(onClick = { repository.markAllNotificationsRead() }) {
                        Text("Tandai Dibaca", color = MenuKuGreenPrimary, style = MaterialTheme.typography.labelMedium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("notification_center_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Filter Pills
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSelected = selectedFilter == cat
                        val count = if (cat == NotificationCategory.ALL) notifications.size else notifications.count { it.category == cat }
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = cat },
                            label = { Text("${cat.label} ($count)") },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MenuKuGreenPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            border = null
                        )
                    }
                }
            }

            // OneSignal Push Notification Status Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onesignal_push_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MenuKuGreenLight.copy(alpha = 0.6f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MenuKuGreenPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        "OneSignal Push Notification",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MenuKuGreenPrimary
                                    )
                                    Text(
                                        "ID: ${MenuKuApplication.ONESIGNAL_APP_ID.take(8)}... Terhubung",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Surface(
                                shape = CircleShape,
                                color = MenuKuGreenPrimary
                            ) {
                                Text(
                                    "Aktif",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Text(
                            "Notifikasi instan untuk pengingat memasak, peringatan bahan kedaluwarsa, dan rekomendasi resep harian.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = {
                                MenuKuApplication.requestNotificationPermission(fallbackToSettings = true)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("btn_request_push_permission"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Izinkan & Uji Push Notifikasi",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Featured Card: Waktunya Persiapan Soto Lamongan Asli
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MenuKuGreenLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = MenuKuGreenPrimary, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text("PENGINGAT JADWAL MASAK", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MenuKuGreenPrimary)
                                    Text("11:30 WIB", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MenuKuGreenPrimary))
                        }

                        Text("Waktunya Persiapan Soto Lamongan Asli", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        // Food Preview Box
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp))) {
                                    AsyncImage(
                                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCkIuMsVcmHt38NZIild8EDB7JjV32DpOjY791yAunrN390Ucjf-wWb0YwmFgfCVt7s7CN8qjAUhKs7Tp8RNkIteckvDI-MOlsb1uJUMkHzzfhHxQ_Ypxn1-QjcaYkoZe-nokFK1vTjq1H6YNG5svYcVYTIVw1CSUyspehYBiYjj96CGhZ-Fm5g1o9v50Fsg3McUpxbV6wZpkE6FPPGcF_yABbkKf0LNE5qAp_FxmGu",
                                        contentDescription = "Soto Lamongan",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("Target Santap Siang: 12:30 WIB", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                                            Text("45 mnt", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                        Surface(shape = CircleShape, color = MenuKuGreenLight) {
                                            Text("Bahan Lengkap (6/6)", style = MaterialTheme.typography.labelSmall, color = MenuKuGreenPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        }

                        // Action Buttons
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onNavigate(Screen.RecipeDetail.createRoute("soto-ayam")) },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = MenuKuOrangeContainer)
                            ) {
                                Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mulai Masak Sekarang", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { /* Tunda */ },
                                modifier = Modifier.height(46.dp),
                                shape = CircleShape
                            ) {
                                Text("Tunda 15 Menit", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }

            // Featured Card 2: Expiry Alert
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MenuKuOrangeLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MenuKuOrangeContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text("PERLU TINDAKAN SEGERA", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MenuKuOrangeOnContainer)
                                    Text("09:15 WIB", style = MaterialTheme.typography.bodySmall, color = MenuKuOrangeOnContainer.copy(alpha = 0.8f))
                                }
                            }
                        }

                        Text("Dada Ayam Fillet & Susu UHT Mendekati Kedaluwarsa", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MenuKuOrangeOnContainer)
                        Text("2 bahan dapur bernilai tinggi tersisa 1-2 hari sebelum masa simpan habis. Olah untuk cegah mubazir!", style = MaterialTheme.typography.bodySmall, color = MenuKuOrangeOnContainer)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onNavigate(Screen.RecipeDetail.createRoute("soto-ayam")) },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = MenuKuOrange)
                            ) {
                                Text("Olah Sekarang", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { onNavigate(Screen.Inventory.route) },
                                modifier = Modifier.height(44.dp),
                                shape = CircleShape
                            ) {
                                Text("Lihat Stok Kulkas", color = MenuKuOrangeOnContainer)
                            }
                        }
                    }
                }
            }

            // General Notification List
            items(filteredNotifications.drop(2)) { notif ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerLow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (notif.category == NotificationCategory.PANTRY) Icons.Default.ShoppingCartCheckout else Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = MenuKuGreenPrimary
                            )
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(notif.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            }
                            Text(notif.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(notif.timeLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}
