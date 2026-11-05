package com.example.menuku.features.schedule

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BakeryDining
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.MenuKuApplication
import com.example.menuku.data.models.MealSchedule
import com.example.menuku.data.models.MealSlot
import com.example.menuku.data.models.Recipe
import com.example.menuku.data.repository.MenuKuRepository
import com.example.ui.theme.*
import com.onesignal.OneSignal
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAddScreen(
    repository: MenuKuRepository,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val recipes by repository.recipes.collectAsState()

    var selectedSlot by remember { mutableStateOf(MealSlot.SIANG) }
    var selectedRecipes by remember { mutableStateOf(setOf<Recipe>()) }
    var reminderEnabled by remember { mutableStateOf(true) }
    var prepTime by remember { mutableStateOf("11:15") }
    var notes by remember { mutableStateOf("Kurangi garam untuk ayah, siapkan potongan bumbu dan suwiran ayam dari jam 11...") }
    var autoSyncGrocery by remember { mutableStateOf(true) }
    var showRecipeBottomSheet by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Pre-select first recipe if empty initially
    LaunchedEffect(recipes) {
        if (selectedRecipes.isEmpty() && recipes.isNotEmpty()) {
            recipes.firstOrNull()?.let { selectedRecipes = setOf(it) }
        }
    }

    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        DisposableEffect(Unit) {
            val picker = TimePickerDialog(
                context,
                { _, h, m ->
                    prepTime = String.format("%02d:%02d", h, m)
                    showTimePicker = false
                },
                hour,
                minute,
                true
            )
            picker.setOnCancelListener { showTimePicker = false }
            picker.show()
            onDispose { picker.dismiss() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Atur Jadwal Menu", fontWeight = FontWeight.Bold) },
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
                .testTag("schedule_add_form"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Pemilihan Tanggal
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MenuKuGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = MenuKuGreenPrimary)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tanggal Rencana", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Kamis, 21 November 2024", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        Surface(shape = CircleShape, color = MenuKuGreenLight) {
                            Text(
                                text = "Hari ini",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MenuKuGreenPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // 2. Pemilihan Slot Makan
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Waktu Makan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val slots = listOf(
                            MealSlot.SARAPAN to Icons.Outlined.WbTwilight,
                            MealSlot.SIANG to Icons.Outlined.WbSunny,
                            MealSlot.CAMILAN to Icons.Outlined.BakeryDining,
                            MealSlot.MALAM to Icons.Outlined.Bedtime
                        )

                        slots.forEach { (slot, icon) ->
                            val isSelected = selectedSlot == slot
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedSlot = slot },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MenuKuGreenPrimary else Color.Transparent
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = slot.label.split(" ").last(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Pemilihan Beberapa Resep Masakan (Multiple menus)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Pilihan Hidangan (${selectedRecipes.size} Menu Dipilih)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        TextButton(onClick = { showRecipeBottomSheet = true }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah / Pilih Menu", color = MenuKuGreenPrimary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (selectedRecipes.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            selectedRecipes.forEach { recipe ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                        ) {
                                            AsyncImage(
                                                model = recipe.imageUrl,
                                                contentDescription = recipe.title,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(recipe.category, style = MaterialTheme.typography.labelSmall, color = MenuKuGreenPrimary, fontWeight = FontWeight.Bold)
                                            Text(recipe.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                            Text("${recipe.prepTimeMinutes} mnt • ${recipe.portions} Porsi", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }

                                        IconButton(
                                            onClick = { selectedRecipes = selectedRecipes - recipe }
                                        ) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showRecipeBottomSheet = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Box(modifier = Modifier.padding(20.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("Ketuk untuk memilih menu makanan...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // 4. Input Waktu Masak & Pengingat
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(imageVector = Icons.Default.Alarm, contentDescription = null, tint = MenuKuOrange)
                                Column {
                                    Text("Pengingat Masak", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                    Text("Kirim alarm persiapan sesuai jam Android Anda", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = reminderEnabled,
                                onCheckedChange = { reminderEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MenuKuGreenPrimary)
                            )
                        }

                        if (reminderEnabled) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showTimePicker = true },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLowest
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = MenuKuGreenPrimary, modifier = Modifier.size(18.dp))
                                        Text("Pukul Persiapan (Ketuk untuk Ubah)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    }
                                    Surface(shape = CircleShape, color = MenuKuGreenLight) {
                                        Text("$prepTime WIB", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MenuKuGreenPrimary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Catatan Masak
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Catatan Masak (Opsional)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text("${notes.length}/200", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { if (it.length <= 200) notes = it },
                        modifier = Modifier.fillMaxWidth().testTag("schedule_notes_input"),
                        shape = RoundedCornerShape(16.dp),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = MenuKuGreenPrimary
                        )
                    )
                }
            }

            // 6. Sinkronisasi Belanja Switch
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                            Icon(imageVector = Icons.Default.PlaylistAddCheck, contentDescription = null, tint = MenuKuGreenPrimary)
                            Column {
                                Text("Sinkronisasi Daftar Belanja", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Text("Otomatis tambahkan bahan resep jika stok dapur tidak mencukupi.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = autoSyncGrocery,
                            onCheckedChange = { autoSyncGrocery = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MenuKuGreenPrimary)
                        )
                    }
                }
            }

            // Bottom Save & Cancel Buttons
            item {
                Button(
                    onClick = {
                        if (selectedRecipes.isNotEmpty()) {
                            val newSchedule = MealSchedule(
                                id = "sch-${System.currentTimeMillis()}",
                                dateStr = "2024-11-21",
                                dayName = "Kamis",
                                dayNumber = 21,
                                slot = selectedSlot,
                                recipe = selectedRecipes.firstOrNull(),
                                recipes = selectedRecipes.toList(),
                                isReadyInFridge = true,
                                reminderTime = "$prepTime WIB",
                                reminderEnabled = reminderEnabled,
                                note = notes,
                                autoSyncGrocery = autoSyncGrocery
                            )
                            repository.addOrUpdateSchedule(newSchedule)

                            try {
                                OneSignal.User.addTag("reminder_${selectedSlot.name.lowercase()}", prepTime)
                                if (reminderEnabled) {
                                    MenuKuApplication.requestNotificationPermission(true)
                                }
                            } catch (_: Exception) {}

                            onBackClick()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_schedule_btn"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary)
                ) {
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan ke Jadwal Menu", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))

                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }

    // Recipe Selector Bottom Sheet Modal (Multi-select)
    if (showRecipeBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRecipeBottomSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pilih Menu Resep (${selectedRecipes.size} Dipilih)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { showRecipeBottomSheet = false }) {
                        Text("Selesai", fontWeight = FontWeight.Bold, color = MenuKuGreenPrimary)
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recipes) { rec ->
                        val isSelected = selectedRecipes.contains(rec)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedRecipes = if (isSelected) {
                                        selectedRecipes - rec
                                    } else {
                                        selectedRecipes + rec
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MenuKuGreenLight else MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    AsyncImage(
                                        model = rec.imageUrl,
                                        contentDescription = rec.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(rec.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("${rec.prepTimeMinutes} mnt • ${rec.category}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        selectedRecipes = if (checked) {
                                            selectedRecipes + rec
                                        } else {
                                            selectedRecipes - rec
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = MenuKuGreenPrimary)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
