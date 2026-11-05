package com.example.menuku.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
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
import com.example.menuku.data.repository.MenuKuRepository
import com.example.ui.theme.*
import com.example.MenuKuApplication
import com.onesignal.OneSignal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    repository: MenuKuRepository,
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val profile by repository.userProfile.collectAsState()

    var syncShopping by remember { mutableStateOf(profile.syncShoppingToPantry) }
    var cookAlarm by remember { mutableStateOf(profile.cookReminderAlarm) }
    var expiryAlert by remember { mutableStateOf(profile.earlyExpiryAlert) }

    // Edit Profile State
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(profile.name) }
    var editRole by remember { mutableStateOf(profile.roleTitle) }
    var editEmail by remember { mutableStateOf(profile.email) }
    var editAvatar by remember { mutableStateOf(profile.avatarUrl) }
    var editPortions by remember { mutableStateOf(profile.standardPortions.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Pengguna", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
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
                .testTag("profile_content_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(70.dp)) {
                                AsyncImage(
                                    model = profile.avatarUrl,
                                    contentDescription = profile.name,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(MenuKuGreenPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.LocalDining, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                            }

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(profile.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Surface(shape = CircleShape, color = MenuKuGreenLight) {
                                        Text(profile.roleTitle, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MenuKuGreenPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Text(profile.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Anggota sejak ${profile.joinDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }

                        // Edit Profile Button
                        Button(
                            onClick = {
                                editName = profile.name
                                editRole = profile.roleTitle
                                editEmail = profile.email
                                editAvatar = profile.avatarUrl
                                editPortions = profile.standardPortions.toString()
                                showEditProfileDialog = true
                            },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow, contentColor = MaterialTheme.colorScheme.onSurface),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profil Pengguna", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }

                        // 3 Column Quick Stats
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${profile.recipesCookedCount}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MenuKuGreenPrimary)
                                    Text("Resep Masak", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${profile.fridgeItemsCount}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MenuKuOrange)
                                    Text("Bahan Kulkas", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${profile.favoriteCollectionsCount}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MenuKuTertiary)
                                    Text("Koleksi", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // Preferensi Dapur Cerdas
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Preferensi Dapur Cerdas", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                        // Porsi Makan
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(imageVector = Icons.Default.Groups, contentDescription = null, tint = MenuKuGreenPrimary)
                                Column {
                                    Text("Standar Porsi Masak", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                    Text("Disesuaikan otomatis tiap resep", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerLow) {
                                Text("${profile.standardPortions} Porsi Keluarga", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerLow)

                        // Pola Makan & Pantangan
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(imageVector = Icons.Default.LocalDining, contentDescription = null, tint = MenuKuGreenPrimary)
                                Text("Pola Makan & Pantangan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(start = 34.dp)) {
                                profile.dietaryHabits.forEach { habit ->
                                    Surface(shape = CircleShape, color = MenuKuGreenLight) {
                                        Text("✓ $habit", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MenuKuGreenPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerLow)

                        // Anggaran Mingguan
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MenuKuTertiary)
                                    Text("Anggaran Belanja Mingguan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                }
                                Text("Rp 500.000", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            }

                            LinearProgressIndicator(
                                progress = { 0.3f },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                color = MenuKuGreenPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Terpakai Rp 148.500 (30%)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Sisa Rp 351.500", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MenuKuGreenPrimary)
                            }
                        }
                    }
                }
            }

            // Otomasi & Notifikasi Toggles
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Otomasi & Notifikasi", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                        // Toggle 1: Sinkronisasi Belanja ke Kulkas
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                                Icon(imageVector = Icons.Default.Sync, contentDescription = null, tint = MenuKuGreenPrimary)
                                Column {
                                    Text("Sinkronisasi Belanja ke Kulkas", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                    Text("Bahan terbeli otomatis masuk stok inventaris", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = syncShopping,
                                onCheckedChange = {
                                    syncShopping = it
                                    repository.updateProfile { p -> p.copy(syncShoppingToPantry = it) }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MenuKuGreenPrimary)
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerLow)

                        // Toggle 2: Alarm Pengingat Masak
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                                Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = MenuKuGreenPrimary)
                                Column {
                                    Text("Alarm Pengingat Masak", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                    Text("Bunyikan pengingat 30 menit sebelum jadwal makan", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = cookAlarm,
                                onCheckedChange = {
                                    cookAlarm = it
                                    repository.updateProfile { p -> p.copy(cookReminderAlarm = it) }
                                    try {
                                        OneSignal.User.addTag("alarm_masak", it.toString())
                                        if (it) {
                                            MenuKuApplication.requestNotificationPermission(fallbackToSettings = true)
                                        }
                                    } catch (_: Exception) {}
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MenuKuGreenPrimary)
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerLow)

                        // Toggle 3: Peringatan Kadaluwarsa Dini
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                                Icon(imageVector = Icons.Default.Event, contentDescription = null, tint = MenuKuOrange)
                                Column {
                                    Text("Peringatan Kadaluwarsa Dini", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                    Text("Pemberitahuan 2 hari sebelum bahan membusuk", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = expiryAlert,
                                onCheckedChange = {
                                    expiryAlert = it
                                    repository.updateProfile { p -> p.copy(earlyExpiryAlert = it) }
                                    try {
                                        OneSignal.User.addTag("peringatan_kadaluwarsa", it.toString())
                                        if (it) {
                                            MenuKuApplication.requestNotificationPermission(fallbackToSettings = true)
                                        }
                                    } catch (_: Exception) {}
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MenuKuGreenPrimary)
                            )
                        }
                    }
                }
            }

            // Tombol Keluar Akun
            item {
                Button(
                    onClick = { onNavigate(Screen.Login.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("profile_logout_btn"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MenuKuErrorContainer, contentColor = MenuKuOnErrorContainer)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar Akun", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "MenuKu v2.4.1 — Sahabat Dapur Rumah Tangga Anda",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profil Pengguna", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nama Lengkap") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editRole,
                        onValueChange = { editRole = it },
                        label = { Text("Peran Dapur (contoh: Chef Keluarga)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Alamat Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAvatar,
                        onValueChange = { editAvatar = it },
                        label = { Text("URL Foto Profil / Avatar") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPortions,
                        onValueChange = { editPortions = it },
                        label = { Text("Standar Porsi Masak") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val portionsInt = editPortions.toIntOrNull() ?: profile.standardPortions
                        repository.updateProfile { p ->
                            p.copy(
                                name = editName.ifBlank { p.name },
                                roleTitle = editRole.ifBlank { p.roleTitle },
                                email = editEmail.ifBlank { p.email },
                                avatarUrl = editAvatar.ifBlank { p.avatarUrl },
                                standardPortions = portionsInt
                            )
                        }
                        // Update Firestore if user is authenticated
                        try {
                            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                            if (uid != null) {
                                val userMap = mapOf(
                                    "name" to editName,
                                    "email" to editEmail,
                                    "roleTitle" to editRole,
                                    "avatarUrl" to editAvatar,
                                    "standardPortions" to portionsInt
                                )
                                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                    .collection("users").document(uid)
                                    .set(userMap, com.google.firebase.firestore.SetOptions.merge())
                            }
                        } catch (_: Exception) {}

                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary)
                ) {
                    Text("Simpan Perubahan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
