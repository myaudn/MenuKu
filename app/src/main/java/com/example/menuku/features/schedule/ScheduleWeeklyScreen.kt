package com.example.menuku.features.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.menuku.core.components.MenuKuTopBar
import com.example.menuku.core.navigation.Screen
import com.example.menuku.data.models.MealSlot
import com.example.menuku.data.repository.MenuKuRepository
import com.example.ui.theme.*
import java.util.Calendar

@Composable
fun ScheduleWeeklyScreen(
    repository: MenuKuRepository,
    onNavigate: (String) -> Unit
) {
    val schedules by repository.schedules.collectAsState()

    var selectedYear by remember { mutableIntStateOf(2024) }
    var selectedMonthIndex by remember { mutableIntStateOf(10) } // 10 = November
    var selectedDayNumber by remember { mutableIntStateOf(21) }
    var showMonthPicker by remember { mutableStateOf(false) }

    val monthsList = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
    val currentMonthName = monthsList[selectedMonthIndex]

    val daysInMonth = when (selectedMonthIndex) {
        1 -> if (selectedYear % 4 == 0) 29 else 28
        3, 5, 8, 10 -> 30
        else -> 31
    }
    val allDays = (1..daysInMonth).toList()

    val dateRowState = rememberLazyListState()

    // Scroll date row to selected day on initial load or month change
    LaunchedEffect(selectedDayNumber, selectedMonthIndex) {
        if (selectedDayNumber in 1..daysInMonth) {
            dateRowState.animateScrollToItem((selectedDayNumber - 1).coerceAtLeast(0))
        }
    }

    // Filter schedules for the active day
    val daySchedules = remember(schedules, selectedDayNumber) {
        schedules.filter { it.dayNumber == selectedDayNumber || selectedDayNumber == 21 }
    }

    Scaffold(
        topBar = {
            MenuKuTopBar(
                title = "Jadwal & Kalender Menu",
                subtitle = "Pola Makan Sehat & Teratur",
                onNotificationClick = { onNavigate(Screen.NotificationCenter.route) },
                onProfileClick = { onNavigate(Screen.Profile.route) }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigate(Screen.AddSchedule.route) },
                containerColor = MenuKuOrangeContainer,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("schedule_add_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tambah Jadwal", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("weekly_schedule_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header Kalender Bulanan
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.clickable { showMonthPicker = true }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(MenuKuGreenLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = MenuKuGreenPrimary
                                    )
                                }
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "$currentMonthName $selectedYear",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = MenuKuGreenPrimary)
                                    }
                                    Text(
                                        text = "Ketuk untuk ganti bulan/tahun",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        if (selectedMonthIndex > 0) {
                                            selectedMonthIndex--
                                        } else {
                                            selectedMonthIndex = 11
                                            selectedYear--
                                        }
                                        if (selectedDayNumber > daysInMonth) selectedDayNumber = daysInMonth
                                    },
                                    modifier = Modifier.size(34.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceContainerLow)
                                ) {
                                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Bulan Sebelumnya", tint = MenuKuGreenPrimary)
                                }
                                IconButton(
                                    onClick = {
                                        if (selectedMonthIndex < 11) {
                                            selectedMonthIndex++
                                        } else {
                                            selectedMonthIndex = 0
                                            selectedYear++
                                        }
                                        if (selectedDayNumber > daysInMonth) selectedDayNumber = daysInMonth
                                    },
                                    modifier = Modifier.size(34.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceContainerLow)
                                ) {
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Bulan Berikutnya", tint = MenuKuGreenPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // 2. Horizontal Date Strip (Days in Month with Day Names)

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Pilih Tanggal Hari Ini",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "$selectedDayNumber $currentMonthName $selectedYear",
                            style = MaterialTheme.typography.labelSmall,
                            color = MenuKuGreenPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LazyRow(
                        state = dateRowState,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(allDays) { dayNum ->

                            val isSelected = selectedDayNumber == dayNum
                            val dayOfWeekName = getDayOfWeekName(
                                selectedYear,
                                selectedMonthIndex,
                                dayNum
                            )

                            Column(
                                modifier = Modifier
                                    .width(52.dp)
                                    .padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(68.dp)
                                        .clickable {
                                            selectedDayNumber = dayNum
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) {
                                            MenuKuGreenPrimary
                                        } else {
                                            MaterialTheme.colorScheme.surfaceContainerLow
                                        }
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = if (isSelected) 3.dp else 0.dp
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = dayOfWeekName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) {
                                                Color.White.copy(alpha = 0.8f)
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                            fontSize = 10.sp
                                        )

                                        Text(
                                            text = "$dayNum",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) {
                                                Color.White
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) {
                                                MenuKuGreenPrimary
                                            } else {
                                                MenuKuGreenPrimary.copy(alpha = 0.5f)
                                            }
                                        )
                                )
                            }
                        }
                    }
                }
            }


            // 3. Section Header Hari Aktif
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Rencana Menu Tanggal $selectedDayNumber",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(shape = CircleShape, color = MenuKuGreenLight) {
                            Text(
                                text = "4 Slot Waktu",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MenuKuGreenPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    TextButton(onClick = { onNavigate(Screen.AddSchedule.route) }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Atur Slot", style = MaterialTheme.typography.labelSmall, color = MenuKuGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 4. Meal Slots List (Multiple menus per slot supported)
            MealSlot.entries.forEach { slot ->
                val matchingSchedule = daySchedules.find { it.slot == slot }
                val slotRecipes = matchingSchedule?.allRecipes ?: emptyList()

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (matchingSchedule != null) {
                                    onNavigate(Screen.ScheduleDetail.createRoute(matchingSchedule.id))
                                } else {
                                    onNavigate(Screen.AddSchedule.route)
                                }
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (slotRecipes.isNotEmpty()) MaterialTheme.colorScheme.surfaceContainerLowest else MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (slotRecipes.isNotEmpty()) 1.dp else 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Slot Top Header Row
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
                                            .background(
                                                when (slot) {
                                                    MealSlot.SARAPAN -> Color(0xFFFFF3E0)
                                                    MealSlot.SIANG -> Color(0xFFFFE0B2)
                                                    MealSlot.CAMILAN -> Color(0xFFE8F5E9)
                                                    MealSlot.MALAM -> Color(0xFFE8EAF6)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (slot) {
                                                MealSlot.SARAPAN -> Icons.Outlined.WbTwilight
                                                MealSlot.SIANG -> Icons.Outlined.WbSunny
                                                MealSlot.CAMILAN -> Icons.Outlined.BakeryDining
                                                MealSlot.MALAM -> Icons.Outlined.Bedtime
                                            },
                                            contentDescription = null,
                                            tint = when (slot) {
                                                MealSlot.SARAPAN, MealSlot.SIANG -> MenuKuOrangeContainer
                                                MealSlot.CAMILAN -> MenuKuGreenPrimary
                                                MealSlot.MALAM -> Color(0xFF3F51B5)
                                            },
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = slot.label,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = matchingSchedule?.reminderTime?.ifBlank { slot.defaultTime } ?: slot.defaultTime,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (slotRecipes.isNotEmpty()) {
                                    if (matchingSchedule?.isReadyInFridge == true) {
                                        Surface(shape = CircleShape, color = MenuKuGreenLight) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = MenuKuGreenPrimary, modifier = Modifier.size(12.dp))
                                                Text("Stok Lengkap", style = MaterialTheme.typography.labelSmall, color = MenuKuGreenPrimary, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    } else {
                                        Surface(
                                            shape = CircleShape,
                                            color = MenuKuOrangeLight
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MenuKuOrange, modifier = Modifier.size(12.dp))
                                                Text("Stok Kurang", style = MaterialTheme.typography.labelSmall, color = MenuKuOrange, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            // Slot Body Content (Multiple menus)
                            if (slotRecipes.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    slotRecipes.forEach { recipe ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                            ) {
                                                AsyncImage(
                                                    model = recipe.imageUrl,
                                                    contentDescription = recipe.title,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }

                                            Column(
                                                modifier = Modifier.weight(1f),
                                                verticalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Text(
                                                    text = recipe.title,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )

                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
                                                        Text("${recipe.prepTimeMinutes} mnt", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                    }
                                                    Surface(shape = RoundedCornerShape(6.dp), color = MenuKuGreenLight) {
                                                        Text(recipe.category, style = MaterialTheme.typography.labelSmall, color = MenuKuGreenPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Empty slot call to action
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Belum ada rencana menu ${slot.label.lowercase()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Button(
                                        onClick = { onNavigate(Screen.AddSchedule.route) },
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = MenuKuOrangeContainer),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Pilih Menu", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

    // Dialog Pemilihan Bulan & Tahun
    if (showMonthPicker) {
        AlertDialog(
            onDismissRequest = { showMonthPicker = false },
            title = { Text("Pilih Bulan & Tahun", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tahun: $selectedYear", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = { selectedYear-- }) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Kurang")
                            }
                            IconButton(onClick = { selectedYear++ }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah")
                            }
                        }
                    }
                    HorizontalDivider()
                    Text("Pilih Bulan:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    monthsList.forEachIndexed { index, mName ->
                        TextButton(
                            onClick = {
                                selectedMonthIndex = index
                                if (selectedDayNumber > daysInMonth) selectedDayNumber = daysInMonth
                                showMonthPicker = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = mName,
                                fontWeight = if (selectedMonthIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedMonthIndex == index) MenuKuGreenPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMonthPicker = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

private fun getDayOfWeekName(year: Int, monthIndex: Int, dayNumber: Int): String {
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, monthIndex)
        set(Calendar.DAY_OF_MONTH, dayNumber)
    }
    return when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> "Min"
        Calendar.MONDAY -> "Sen"
        Calendar.TUESDAY -> "Sel"
        Calendar.WEDNESDAY -> "Rab"
        Calendar.THURSDAY -> "Kam"
        Calendar.FRIDAY -> "Jum"
        Calendar.SATURDAY -> "Sab"
        else -> "Sen"
    }
}
