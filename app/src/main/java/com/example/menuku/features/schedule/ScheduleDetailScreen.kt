package com.example.menuku.features.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.menuku.core.navigation.Screen
import com.example.menuku.data.repository.MenuKuRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailScreen(
    scheduleId: String,
    repository: MenuKuRepository,
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val schedules by repository.schedules.collectAsState()
    val stockItems by repository.stockItems.collectAsState()
    val schedule = schedules.find { it.id == scheduleId } ?: schedules.first()
    val recipe = schedule.recipe

    val ingredientsStatus = remember(recipe, stockItems) {
        recipe?.ingredients?.map { ing ->
            val stock = stockItems.find { s -> s.name.contains(ing.name, ignoreCase = true) || ing.name.contains(s.name, ignoreCase = true) }
            val available = stock?.quantity ?: 0.0
            val isSufficient = available >= ing.amount
            Triple(ing, available, isSufficient)
        } ?: emptyList()
    }

    val readyCount = ingredientsStatus.count { it.third }
    val totalCount = ingredientsStatus.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Jadwal Menu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate(Screen.AddSchedule.route) }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (recipe != null) {
                                onNavigate(Screen.RecipeDetail.createRoute(recipe.id))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("schedule_start_cooking_btn"),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.SoupKitchen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mulai Masak Sekarang", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onNavigate(Screen.AddSchedule.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ganti Resep Lain")
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("schedule_detail_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Tanggal & Waktu
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(shape = CircleShape, color = MenuKuGreenLight) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Restaurant, contentDescription = null, tint = MenuKuGreenPrimary, modifier = Modifier.size(14.dp))
                                    Text("${schedule.slot.label} • ${schedule.slot.defaultTime}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MenuKuGreenPrimary)
                                }
                            }
                            Text("Tepat Waktu", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Text("Kamis, 21 November 2024", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Recipe Hero Card
            if (recipe != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ) {
                                AsyncImage(
                                    model = recipe.imageUrl,
                                    contentDescription = recipe.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                            )
                                        )
                                )

                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                ) {
                                    Surface(shape = CircleShape, color = MenuKuOrangeLight) {
                                        Text(
                                            text = recipe.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MenuKuOrange,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = recipe.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // 4 Metrics Grid
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Waktu", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${recipe.prepTimeMinutes} Mnt", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Porsi", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${recipe.portions} Orang", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Tingkat", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(recipe.difficulty, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }

            // Catatan Rencana Makan
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MenuKuOrangeLight.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.StickyNote2, contentDescription = null, tint = MenuKuOrange)
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Catatan Rencana Makan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = MenuKuOrangeOnContainer)
                                Text("Sarah", style = MaterialTheme.typography.labelSmall, color = MenuKuOrange)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = schedule.note.ifBlank { "Masak porsi lebih untuk makan malam jika ada sisa, koya digoreng pagi hari bersama sarapan." },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MenuKuOrangeOnContainer
                            )
                        }
                    }
                }
            }

            // Status Bahan Dapur & Mini Checklist
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.Kitchen, contentDescription = null, tint = MenuKuGreenPrimary)
                                Text("Status Ketersediaan Stok Dapur", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            }
                            Surface(
                                shape = CircleShape,
                                color = if (readyCount == totalCount) MenuKuGreenLight else MenuKuOrangeLight
                            ) {
                                Text(
                                    text = if (readyCount == totalCount) "Siap Masak" else "Stok Kurang ($readyCount/$totalCount)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (readyCount == totalCount) MenuKuGreenPrimary else MenuKuOrange,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { if (totalCount > 0) readyCount.toFloat() / totalCount else 1.0f },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = MenuKuGreenPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        )

                        Text(
                            text = "$readyCount dari $totalCount bahan lengkap di stok kulkas/pantri Anda.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            ingredientsStatus.forEach { (ing, available, isSufficient) ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSufficient) MaterialTheme.colorScheme.surfaceContainerLow else MenuKuOrangeLight.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isSufficient) Icons.Default.CheckCircle else Icons.Default.AddShoppingCart,
                                                contentDescription = null,
                                                tint = if (isSufficient) MenuKuGreenPrimary else MenuKuOrange,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(ing.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        }

                                        val formattedReq = if (ing.amount % 1.0 == 0.0) ing.amount.toInt().toString() else ing.amount.toString()
                                        val formattedAvail = if (available % 1.0 == 0.0) available.toInt().toString() else available.toString()

                                        Text(
                                            text = if (isSufficient) "Tersedia $formattedAvail ${ing.unit}" else "Stok: $formattedAvail ${ing.unit} (Butuh $formattedReq ${ing.unit})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSufficient) MenuKuGreenPrimary else MenuKuOrange,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        if (readyCount < totalCount) {
                            Button(
                                onClick = { onNavigate(Screen.Shopping.route) },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = MenuKuOrangeContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Bahan Kurang Sudah Masuk Daftar Belanja")
                            }
                        }
                    }
                }
            }
        }
    }
}
