package com.example.menuku.features.shopping

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
import com.example.menuku.core.utils.AiPriceEstimator
import com.example.menuku.data.models.ShoppingItem
import com.example.menuku.data.models.UrgencyLevel
import com.example.menuku.data.repository.MenuKuRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShoppingItemScreen(
    repository: MenuKuRepository,
    onBackClick: () -> Unit
) {
    val recipes by repository.recipes.collectAsState()

    var itemName by remember { mutableStateOf("") }
    var quantityInput by remember { mutableStateOf("500") }
    var unit by remember { mutableStateOf("gram") }
    var priceInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("bumbu") }
    var linkedRecipeTitle by remember { mutableStateOf("Soto Ayam Lamongan Asli") }
    var urgency by remember { mutableStateOf(UrgencyLevel.URGENT) }
    var notes by remember { mutableStateOf("") }

    val quickRecommendations = listOf(
        Pair("Bawang Merah", "bumbu"),
        Pair("Daun Jeruk Purut", "bumbu"),
        Pair("Santan Kelapa Instan", "sembako"),
        Pair("Cabai Rawit Merah", "sayur"),
        Pair("Daging Dada Ayam", "protein")
    )

    val units = listOf("gram", "kg", "pcs", "ikat", "liter", "ml", "sdm")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Item Belanja", fontWeight = FontWeight.Bold) },
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
                .testTag("add_shopping_item_form"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
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
                            Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null, tint = MenuKuGreenPrimary)
                        }
                        Column {
                            Text("Tambah Item Belanja", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Lengkapi stok bahan agar sesi masak makin menyenangkan.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Field 1: Nama Bahan
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Nama Bahan Masakan *", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Text("Wajib diisi", style = MaterialTheme.typography.labelSmall, color = MenuKuGreenPrimary)
                        }

                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            placeholder = { Text("Contoh: Daging Dada Ayam, Bawang Merah...") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Restaurant, contentDescription = null, tint = MenuKuGreenPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth().testTag("shopping_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = MenuKuGreenPrimary
                            )
                        )

                        Text("Rekomendasi Cepat:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(quickRecommendations) { (name, cat) ->
                                FilterChip(
                                    selected = itemName == name,
                                    onClick = {
                                        itemName = name
                                        selectedCategory = cat
                                    },
                                    label = { Text("+ $name") },
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

            // Field 2: Jumlah & Satuan Takar (Supports decimal 0,... and manual typing)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Jumlah & Satuan Takar", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                    modifier = Modifier.weight(1f).testTag("shopping_quantity_input"),
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

                            LazyRow(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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

            // Field: Estimasi Harga (Opsional - AI Price Identification)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estimasi Harga (Opsional)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Surface(shape = CircleShape, color = MenuKuGreenLight) {
                                Text("💡 Identifikasi AI", style = MaterialTheme.typography.labelSmall, color = MenuKuGreenPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }

                        OutlinedTextField(
                            value = priceInput,
                            onValueChange = { priceInput = it },
                            placeholder = { Text("Kosongkan untuk estimasi cerdas AI (Cth: 25000)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = MenuKuGreenPrimary
                            )
                        )
                        Text("Jika dikosongkan, AI akan mengidentifikasi harga pasar bahan ini secara otomatis.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Field 3: Kategori Belanja
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Kategori Belanja", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)

                        val categoryItems = listOf(
                            Triple("sayur", "Sayur & Buah", Icons.Default.LocalDining),
                            Triple("bumbu", "Bumbu Rempah", Icons.Default.Spa),
                            Triple("protein", "Daging & Protein", Icons.Default.SetMeal),
                            Triple("sembako", "Sembako Pokok", Icons.Default.Grain)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            categoryItems.take(2).forEach { (catId, catLabel, icon) ->
                                val isSelected = selectedCategory == catId
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedCategory = catId },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MenuKuGreenLight else MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) MenuKuGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(catLabel, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            categoryItems.takeLast(2).forEach { (catId, catLabel, icon) ->
                                val isSelected = selectedCategory == catId
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedCategory = catId },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MenuKuGreenLight else MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) MenuKuGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(catLabel, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Field 4: Hubungkan ke Resep MenuKu
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Kaitkan ke Resep MenuKu", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Text("Opsional", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(recipes) { rec ->
                                val isSelected = linkedRecipeTitle == rec.title
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { linkedRecipeTitle = rec.title },
                                    label = { Text(rec.title) },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MenuKuGreenPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Field 5: Tingkat Urgensi
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Tingkat Urgensi", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            UrgencyLevel.values().forEach { level ->
                                val isSelected = urgency == level
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { urgency = level },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) {
                                            if (level == UrgencyLevel.URGENT) MenuKuOrangeContainer else MenuKuGreenPrimary
                                        } else Color.Transparent
                                    )
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = level.label,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Field 6: Catatan Tambahan
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Catatan Khusus", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Text("${notes.length}/120", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { if (it.length <= 120) notes = it },
                            placeholder = { Text("Contoh: Minta dipotong jadi 4 bagian untuk rebusan kaldu...") },
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
                        if (itemName.isNotBlank()) {
                            val qty = quantityInput.replace(',', '.').toDoubleOrNull() ?: 1.0
                            val userPrice = priceInput.toLongOrNull()
                            val finalPrice = if (userPrice != null && userPrice > 0L) {
                                userPrice
                            } else {
                                AiPriceEstimator.estimatePrice(itemName, qty, unit, selectedCategory)
                            }

                            repository.addShoppingItem(
                                ShoppingItem(
                                    id = "shp-${System.currentTimeMillis()}",
                                    name = itemName,
                                    quantity = qty,
                                    unit = unit,
                                    category = selectedCategory,
                                    urgency = urgency,
                                    linkedRecipeTitle = linkedRecipeTitle,
                                    estimatedPrice = finalPrice,
                                    notes = notes,
                                    isAutoGenerated = false
                                )
                            )
                            onBackClick()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_add_shopping_btn"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary)
                ) {
                    Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan ke Daftar Belanja", fontWeight = FontWeight.Bold)
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
}
