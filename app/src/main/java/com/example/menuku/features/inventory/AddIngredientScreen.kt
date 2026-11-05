package com.example.menuku.features.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.menuku.data.models.StockItem
import com.example.menuku.data.models.StorageLocation
import com.example.menuku.data.repository.MenuKuRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientScreen(
    repository: MenuKuRepository,
    onBackClick: () -> Unit
) {
    val storageLocations by repository.storageLocations.collectAsState()

    var name by remember { mutableStateOf("") }
    var quantityInput by remember { mutableStateOf("500") }
    var unit by remember { mutableStateOf("gram") }
    var location by remember { mutableStateOf(StorageLocation.KULKAS) }
    var shelfLifeValueInput by remember { mutableStateOf("4") }
    var shelfLifeUnit by remember { mutableStateOf("Hari") }
    var category by remember { mutableStateOf("Daging & Telur") }
    var note by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    // Add Storage Location Dialog State
    var showAddLocationDialog by remember { mutableStateOf(false) }
    var newLocName by remember { mutableStateOf("") }
    var newLocTemp by remember { mutableStateOf("") }

    val quickSuggestions = listOf("Dada Ayam", "Telur Ayam", "Wortel Segar", "Tahu Sutra", "Bawang Merah", "Brokoli")
    val units = listOf("gram", "kg", "liter", "butir", "bungkus", "ikat")
    val categories = listOf("Sayur & Buah", "Daging & Telur", "Bumbu Rempah", "Susu & Olahan", "Sembako")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Bahan Dapur", fontWeight = FontWeight.Bold) },
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
                .testTag("add_ingredient_form"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome / Guide Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MenuKuGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Eco, contentDescription = null, tint = MenuKuGreenPrimary)
                        }
                        Column {
                            Text(
                                text = "Kelola Stok Bahan Makanan",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pantau kesegaran bahan dapur agar tetap bernutrisi dan tidak ada yang terbuang sia-sia.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Section 1: Nama Bahan
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Nama Bahan Makanan *", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Text("Wajib diisi", style = MaterialTheme.typography.labelSmall, color = MenuKuGreenPrimary)
                        }

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("Contoh: Dada Ayam, Susu Segar, Brokoli") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Restaurant, contentDescription = null, tint = MenuKuGreenPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth().testTag("add_ingredient_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = MenuKuGreenPrimary
                            )
                        )

                        Text("Saran Cepat:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(quickSuggestions) { suggestion ->
                                FilterChip(
                                    selected = false,
                                    onClick = { name = suggestion },
                                    label = { Text("+ $suggestion") },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Section 2: Jumlah & Satuan (Supports decimal 0,... and manual typing)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Jumlah & Satuan *", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Stepper with Manual Decimal TextField
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        val current = quantityInput.replace(',', '.').toDoubleOrNull() ?: 0.0
                                        val step = if (current <= 1.0) 0.1 else if (current <= 5.0) 0.5 else 50.0
                                        val nextVal = maxOf(0.0, current - step)
                                        quantityInput = if (nextVal % 1.0 == 0.0) nextVal.toInt().toString() else nextVal.toString()
                                    },
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Kurang")
                                }

                                OutlinedTextField(
                                    value = quantityInput,
                                    onValueChange = { quantityInput = it },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f).testTag("quantity_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedBorderColor = MenuKuGreenPrimary
                                    )
                                )

                                IconButton(
                                    onClick = {
                                        val current = quantityInput.replace(',', '.').toDoubleOrNull() ?: 0.0
                                        val step = if (current < 1.0) 0.1 else if (current < 5.0) 0.5 else 50.0
                                        val nextVal = current + step
                                        quantityInput = if (nextVal % 1.0 == 0.0) nextVal.toInt().toString() else nextVal.toString()
                                    },
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(MenuKuGreenPrimary)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah", tint = Color.White)
                                }
                            }

                            // Unit dropdown selector
                            Column(modifier = Modifier.weight(1f)) {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(units) { u ->
                                        FilterChip(
                                            selected = unit == u,
                                            onClick = { unit = u },
                                            label = { Text(u) },
                                            shape = CircleShape,
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MenuKuGreenPrimary,
                                                selectedLabelColor = Color.White,
                                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Lokasi Simpan (Supports dynamic storage locations)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Lokasi Simpan *", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            TextButton(onClick = { showAddLocationDialog = true }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tambah Tempat", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(storageLocations) { loc ->
                                val isSelected = location == loc
                                Card(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .clickable { location = loc },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MenuKuGreenLight else MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = when (loc.id) {
                                                "kulkas" -> Icons.Default.Kitchen
                                                "freezer" -> Icons.Default.AcUnit
                                                "pantri" -> Icons.Default.Inbox
                                                else -> Icons.Default.FolderSpecial
                                            },
                                            contentDescription = null,
                                            tint = if (isSelected) MenuKuGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(loc.label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, maxLines = 1)
                                        Text(loc.tempDesc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 4: Daya Tahan & Kedaluwarsa (Manual input in Hari or Jam)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Perkiraan Daya Tahan Simpan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Surface(shape = CircleShape, color = MenuKuOrangeLight) {
                                Text(
                                    text = "$shelfLifeValueInput $shelfLifeUnit",
                                    fontWeight = FontWeight.Bold,
                                    color = MenuKuOrange,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = shelfLifeValueInput,
                                onValueChange = { shelfLifeValueInput = it },
                                label = { Text("Nilai Daya Tahan") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).testTag("shelf_life_input"),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = MenuKuOrange
                                )
                            )

                            // Unit Selector: Hari vs Jam
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("Hari", "Jam").forEach { u ->
                                    val isSelected = shelfLifeUnit == u
                                    Card(
                                        modifier = Modifier
                                            .clickable { shelfLifeUnit = u },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MenuKuOrangeContainer else Color.Transparent
                                        )
                                    ) {
                                        Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), contentAlignment = Alignment.Center) {
                                            Text(
                                                text = u,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("2" to "2 Hari", "7" to "1 Minggu", "12" to "12 Jam", "24" to "24 Jam").forEach { (valStr, label) ->
                                FilterChip(
                                    selected = shelfLifeValueInput == valStr && shelfLifeUnit == (if (valStr.contains("Jam")) "Jam" else "Hari"),
                                    onClick = {
                                        shelfLifeValueInput = valStr
                                        shelfLifeUnit = if (valStr.contains("Jam")) "Jam" else "Hari"
                                    },
                                    label = { Text(label) },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MenuKuOrangeContainer,
                                        selectedLabelColor = Color.White,
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Section 5: Kategori Bahan
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Kategori Bahan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(categories) { cat ->
                                val isSelected = category == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { category = cat },
                                    label = { Text(cat) },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MenuKuGreenPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Section 6: Catatan Tambahan
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Catatan Tambahan (Opsional)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            placeholder = { Text("Contoh: Beli di pasar tradisional, sudah dicuci dan dipotong.") },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = MenuKuGreenPrimary
                            )
                        )
                    }
                }
            }

            // Bottom Buttons
            item {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            isSaving = true
                            val shelfVal = shelfLifeValueInput.replace(',', '.').toDoubleOrNull() ?: 1.0
                            val remainingDays = if (shelfLifeUnit == "Jam") {
                                maxOf(0, (shelfVal / 24.0).toInt())
                            } else {
                                shelfVal.toInt()
                            }
                            val expiryStr = "$shelfLifeValueInput $shelfLifeUnit Lagi"

                            repository.addStockItem(
                                StockItem(
                                    id = "stk-${System.currentTimeMillis()}",
                                    name = name,
                                    quantity = quantityInput.replace(',', '.').toDoubleOrNull() ?: 0.0,
                                    unit = unit,
                                    location = location,
                                    category = category,
                                    purchaseDate = "Hari ini",
                                    expiryDate = expiryStr,
                                    remainingDays = remainingDays,
                                    note = note
                                )
                            )
                            onBackClick()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_add_ingredient_btn"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan ke Stok Dapur", fontWeight = FontWeight.Bold)
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

    // Add Storage Location Dialog
    if (showAddLocationDialog) {
        AlertDialog(
            onDismissRequest = { showAddLocationDialog = false },
            title = { Text("Tambah Tempat Penyimpanan", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newLocName,
                        onValueChange = { newLocName = it },
                        label = { Text("Nama Tempat (Cth: Rak Rempah)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newLocTemp,
                        onValueChange = { newLocTemp = it },
                        label = { Text("Keterangan Suhu (Cth: Suhu Ruang)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newLocName.isNotBlank()) {
                            repository.addStorageLocation(newLocName, newLocTemp.ifBlank { "Suhu Ruang" })
                            val added = repository.storageLocations.value.lastOrNull()
                            if (added != null) location = added
                            newLocName = ""
                            newLocTemp = ""
                            showAddLocationDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary)
                ) {
                    Text("Tambah")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLocationDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
