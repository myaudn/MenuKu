package com.example.menuku.features.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun ShoppingItemDetailScreen(
    itemId: String,
    repository: MenuKuRepository,
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val items by repository.shoppingItems.collectAsState()
    val item = items.find { it.id == itemId } ?: items.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Belanja", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Edit */ }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(
                        onClick = {
                            repository.deleteShoppingItem(item.id)
                            onBackClick()
                        },
                        modifier = Modifier.testTag("delete_shopping_item_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = MenuKuError)
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
                        onClick = { repository.toggleShoppingItemPurchased(item.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("toggle_purchase_status_btn"),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (item.isPurchased) MaterialTheme.colorScheme.surfaceContainerHighest else MenuKuGreenPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (item.isPurchased) Icons.Default.Undo else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (item.isPurchased) MaterialTheme.colorScheme.onSurface else Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (item.isPurchased) "Batalkan Status Dibeli" else "Tandai Sudah Dibeli",
                            fontWeight = FontWeight.Bold,
                            color = if (item.isPurchased) MaterialTheme.colorScheme.onSurface else Color.White
                        )
                    }

                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = CircleShape
                    ) {
                        Text("Kembali ke Daftar")
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
                .testTag("shopping_item_detail_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Context Subheader
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, tint = MenuKuGreenPrimary, modifier = Modifier.size(18.dp))
                        Text("Item #S-204", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Main Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Badges Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (item.isPurchased) MenuKuGreenLight else MenuKuOrangeLight
                            ) {
                                Text(
                                    text = if (item.isPurchased) "Sudah Dibeli" else "Belum Dibeli",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.isPurchased) MenuKuGreenPrimary else MenuKuOrange,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerLow) {
                                Text(
                                    text = "Daging & Protein",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Product Title & Image Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                                AsyncImage(
                                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBFD1m38ZfkRQHPJ3J-NknUdFBfUO80qkD7svXEwk0nOD2bRVDWRSQjEldV7FQpCOfn9edHMIpSiW3c8LfI5hbJHG5OFhfj4W6sJV43f6odDX_tJ50CRCdHlq73TcmmdVQF75XamqayF0ZtdcZK5hJ2NXKd0qvhtOn8-Q0kLExJQ9_wXQY_ihjDpFXu6H4Ds8uw7HSaoSZy0sIREluXOIM62a28nsqmtppBItGXo9DL",
                                    contentDescription = item.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Bahan Dapur Utama", style = MaterialTheme.typography.labelSmall, color = MenuKuGreenPrimary, fontWeight = FontWeight.Bold)
                                Text(item.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("Kualitas segar fillet tanpa lemak berlebih", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Quantity Stepper Module
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Jumlah Belanja", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    val formattedQty = if (item.quantity % 1.0 == 0.0) item.quantity.toInt().toString() else item.quantity.toString()
                                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(formattedQty, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MenuKuGreenPrimary)
                                        Text(item.unit, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(
                                        onClick = { repository.updateShoppingItemQty(item.id, -100.0) },
                                        modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    ) {
                                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Kurang")
                                    }
                                    IconButton(
                                        onClick = { repository.updateShoppingItemQty(item.id, 100.0) },
                                        modifier = Modifier.size(40.dp).clip(CircleShape).background(MenuKuGreenPrimary)
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah", tint = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Recipe Source Link Card
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Sumber Kebutuhan Resep", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(Screen.RecipeDetail.createRoute("soto-ayam")) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAV4lqPu0HkdJnZmG2PUHsbIILh4zjm25E4QrQfagxNxlcW_uoPHpiyWyKs5R7REMZ8G5s-XGFwx-J3WBAW1J6zF4KXTgAS3Gtm1cyt5A4NaimkzDUzI-XKkDcp2CpsWafN83l2QvDSRhmY_KGrgeATbWIZcncqx3UtKh_hkIRHs2fgmjwA2Vv8jVZb_tIzbUj3n8uVU7eKqHKkr0jqZ_rFqIN1kLFWQtDUcXIbeAmv",
                                    contentDescription = "Soto Ayam",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Menu Makan Siang", style = MaterialTheme.typography.labelSmall, color = MenuKuOrange, fontWeight = FontWeight.Bold)
                                Text(item.linkedRecipeTitle.ifBlank { "Soto Ayam Lamongan Asli" }, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Porsi untuk 4–5 orang keluarga", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Special Notes Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MenuKuOrangeLight.copy(alpha = 0.5f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(imageVector = Icons.Default.EditNote, contentDescription = null, tint = MenuKuOrange)
                        Column {
                            Text("Catatan Khusus Belanja", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = MenuKuOrangeOnContainer)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.notes.ifBlank { "Beli yang tanpa kulit, minta dipotong jadi 4 bagian untuk rebusan kaldu soto siang ini." },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MenuKuOrangeOnContainer
                            )
                        }
                    }
                }
            }

            // Split Bento: Estimasi Anggaran & Tempat Beli
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Estimasi Harga", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = MenuKuGreenPrimary, modifier = Modifier.size(16.dp))
                            }
                            Text("Rp ${String.format("%,d", item.estimatedPrice).replace(',', '.')}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("s/d Rp 40.000", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Tempat Beli", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = MenuKuOrange, modifier = Modifier.size(16.dp))
                            }
                            Text(item.storeRecommendation, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text("Kios Unggas Pak Budi", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
