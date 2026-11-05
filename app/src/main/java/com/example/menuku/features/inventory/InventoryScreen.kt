package com.example.menuku.features.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.menuku.core.components.EmptyStateView
import com.example.menuku.core.components.MenuKuTopBar
import com.example.menuku.core.components.StockItemCard
import com.example.menuku.core.navigation.Screen
import com.example.menuku.data.models.StockItem
import com.example.menuku.data.models.StorageLocation
import com.example.menuku.data.repository.MenuKuRepository
import com.example.ui.theme.*

@Composable
fun InventoryScreen(
    repository: MenuKuRepository,
    onNavigate: (String) -> Unit
) {
    val stockItems by repository.stockItems.collectAsState()
    val storageLocations by repository.storageLocations.collectAsState()

    var selectedTab by remember { mutableStateOf("Semua") }
    val tabs = listOf("Semua") + storageLocations.map { it.label }

    val filteredItems = stockItems.filter { item ->
        if (selectedTab == "Semua") true else {
            val matchingLoc = storageLocations.find { it.label == selectedTab }
            item.location == matchingLoc
        }
    }

    val urgentCount = stockItems.count { it.isUrgent }

    // Edit Stock Item Dialog State
    var editingItem by remember { mutableStateOf<StockItem?>(null) }
    var editName by remember { mutableStateOf("") }
    var editQtyInput by remember { mutableStateOf("") }
    var editUnit by remember { mutableStateOf("") }
    var editLocation by remember { mutableStateOf(StorageLocation.KULKAS) }
    var editRemainingDaysInput by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf("") }
    var editNote by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            MenuKuTopBar(
                title = "Stok Bahan Dapur",
                subtitle = "${stockItems.size} Bahan Tercatat",
                onNotificationClick = { onNavigate(Screen.NotificationCenter.route) },
                onProfileClick = { onNavigate(Screen.Profile.route) }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigate(Screen.AddIngredient.route) },
                containerColor = MenuKuOrangeContainer,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("inventory_add_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tambah Bahan", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("inventory_screen_list"),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Urgent Expiry Overview Banner
            if (urgentCount > 0) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MenuKuOrangeLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MenuKuOrangeContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "$urgentCount Bahan Perlu Segera Diolah!",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MenuKuOrangeOnContainer
                                )
                                Text(
                                    text = "Masa simpan tersisa <= 2 hari. Cek rekomendasi resepnya.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MenuKuOrangeOnContainer
                                )
                            }
                        }
                    }
                }
            }

            // Storage Location Filter Tabs
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tabs) { tab ->
                        val isSelected = selectedTab == tab
                        val count = if (tab == "Semua") stockItems.size else {
                            val loc = storageLocations.find { it.label == tab }
                            stockItems.count { it.location == loc }
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            label = { Text("$tab ($count)") },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MenuKuGreenPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = null
                        )
                    }
                }
            }

            // Empty state if filtered list is empty
            if (filteredItems.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.Kitchen,
                        title = "Zona Simpan Ini Kosong",
                        description = "Tambahkan bahan makanan segar dapur Anda agar dapat dipantau masa simpannya.",
                        actionButtonText = "+ Tambah Bahan Sekarang",
                        onActionClick = { onNavigate(Screen.AddIngredient.route) }
                    )
                }
            } else {
                items(filteredItems) { stockItem ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        StockItemCard(
                            stockItem = stockItem,
                            onClick = { onNavigate(Screen.IngredientDetail.createRoute(stockItem.id)) },
                            onEditClick = {
                                editingItem = stockItem
                                editName = stockItem.name
                                editQtyInput = stockItem.quantity.toString()
                                editUnit = stockItem.unit
                                editLocation = stockItem.location
                                editRemainingDaysInput = stockItem.remainingDays.toString()
                                editCategory = stockItem.category
                                editNote = stockItem.note
                            }
                        )
                    }
                }
            }
        }
    }

    // Edit Stock Item Dialog
    if (editingItem != null) {
        val currentItem = editingItem!!
        AlertDialog(
            onDismissRequest = { editingItem = null },
            title = { Text("Edit Stok Bahan", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nama Bahan") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editQtyInput,
                            onValueChange = { editQtyInput = it },
                            label = { Text("Jumlah") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editUnit,
                            onValueChange = { editUnit = it },
                            label = { Text("Satuan") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = editRemainingDaysInput,
                        onValueChange = { editRemainingDaysInput = it },
                        label = { Text("Sisa Hari Kedaluwarsa") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Tempat Penyimpanan:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(storageLocations) { loc ->
                            FilterChip(
                                selected = editLocation == loc,
                                onClick = { editLocation = loc },
                                label = { Text(loc.label) },
                                shape = CircleShape
                            )
                        }
                    }
                    OutlinedTextField(
                        value = editNote,
                        onValueChange = { editNote = it },
                        label = { Text("Catatan") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newQty = editQtyInput.replace(',', '.').toDoubleOrNull() ?: currentItem.quantity
                        val newDays = editRemainingDaysInput.toIntOrNull() ?: currentItem.remainingDays
                        val updated = currentItem.copy(
                            name = editName.ifBlank { currentItem.name },
                            quantity = newQty,
                            unit = editUnit.ifBlank { currentItem.unit },
                            location = editLocation,
                            remainingDays = newDays,
                            expiryDate = "$newDays Hari Lagi",
                            note = editNote
                        )
                        repository.updateStockItem(updated)
                        editingItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = {
                            repository.deleteStockItem(currentItem.id)
                            editingItem = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Hapus")
                    }
                    TextButton(onClick = { editingItem = null }) {
                        Text("Batal")
                    }
                }
            }
        )
    }
}
