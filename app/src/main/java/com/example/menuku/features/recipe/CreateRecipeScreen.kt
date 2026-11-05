package com.example.menuku.features.recipe

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.menuku.data.models.CookingStepItem
import com.example.menuku.data.models.IngredientItem
import com.example.menuku.data.models.Recipe
import com.example.menuku.data.repository.MenuKuRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(
    recipeId: String? = null,
    repository: MenuKuRepository,
    onBackClick: () -> Unit
) {
    val recipes by repository.recipes.collectAsState()
    val collections by repository.collections.collectAsState()
    val existingRecipe = remember(recipeId, recipes) { recipes.find { it.id == recipeId } }

    val catalogCategories = remember(collections) {
        val staticCats = listOf(
            "Sarapan",
            "Makan Siang",
            "Makan Malam",
            "Camilan & Dessert",
            "Diet Sehat",
            "Tradisional Nusantara"
        )
        val collectionTitles = collections.map { it.title }
        (staticCats + collectionTitles).distinct()
    }

    var title by remember { mutableStateOf(existingRecipe?.title ?: "") }
    var subtitle by remember { mutableStateOf(existingRecipe?.subtitle ?: "") }
    var category by remember { mutableStateOf(existingRecipe?.category ?: catalogCategories.firstOrNull() ?: "Menu Diet Sehat") }
    var difficulty by remember { mutableStateOf(existingRecipe?.difficulty ?: "Sedang") }
    var portions by remember { mutableStateOf(existingRecipe?.portions?.toString() ?: "4") }
    var prepTimeMinutes by remember { mutableStateOf(existingRecipe?.prepTimeMinutes?.toString() ?: "30") }
    var chefNote by remember { mutableStateOf(existingRecipe?.chefNote ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(existingRecipe?.imageUrl?.let { if (it.startsWith("content://") || it.startsWith("file://")) it.toUri() else null }) }
    var imageUrlString by remember { mutableStateOf(existingRecipe?.imageUrl ?: "") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var showCreateCollectionDialog by remember { mutableStateOf(false) }
    var newCollectionNameInput by remember { mutableStateOf("") }
    val difficultiesList = listOf("Mudah", "Sedang", "Ahli")

    // Ingredients dynamic list
    var ingredientName by remember { mutableStateOf("") }
    var ingredientAmount by remember { mutableStateOf("") }
    var ingredientUnit by remember { mutableStateOf("gram") }
    val ingredientsList = remember { mutableStateOf(existingRecipe?.ingredients ?: emptyList()) }

    // Steps dynamic list
    var stepInstruction by remember { mutableStateOf("") }
    val stepsList = remember { mutableStateOf(existingRecipe?.steps ?: emptyList()) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Gallery Image Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            imageUrlString = uri.toString()
        }
    }

    val isEditing = existingRecipe != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Resep" else "Tambah Resep Baru", fontWeight = FontWeight.Bold) },
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
                .testTag("create_recipe_form"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Image Picker from Android Gallery
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clickable { galleryLauncher.launch("image/*") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (imageUri != null || imageUrlString.isNotBlank()) {
                            AsyncImage(
                                model = imageUri ?: imageUrlString,
                                contentDescription = "Foto Resep",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(MenuKuGreenLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = MenuKuGreenPrimary)
                                }
                                Text("Pilih Foto Resep dari Galeri", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MenuKuGreenPrimary)
                                Text("Ketuk untuk membuka galeri perangkat Android", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // 2. Informasi Utama (Judul, Subtitle, Koleksi Dropdown, Tingkatan, Porsi, Waktu)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Informasi Standar Resep", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Judul Resep (Contoh: Soto Ayam Lamongan)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )

                        OutlinedTextField(
                            value = subtitle,
                            onValueChange = { subtitle = it },
                            label = { Text("Deskripsi Singkat / Subtitle") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )

                        // Pilih Koleksi / Kategori (Integrated with Catalog Filter)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Pilih Koleksi / Kategori Resep *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                TextButton(onClick = { showCreateCollectionDialog = true }) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Tambah Koleksi", style = MaterialTheme.typography.labelSmall, color = MenuKuGreenPrimary, fontWeight = FontWeight.Bold)
                                }
                            }

                            ExposedDropdownMenuBox(
                                expanded = categoryExpanded,
                                onExpandedChange = { categoryExpanded = !categoryExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = category,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Pilihan Nama Koleksi / Kategori") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = categoryExpanded,
                                    onDismissRequest = { categoryExpanded = false }
                                ) {
                                    catalogCategories.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat) },
                                            onClick = {
                                                category = cat
                                                categoryExpanded = false
                                            }
                                        )
                                    }
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = MenuKuGreenPrimary, modifier = Modifier.size(18.dp))
                                                Text("+ Buat Nama Koleksi Baru", color = MenuKuGreenPrimary, fontWeight = FontWeight.Bold)
                                            }
                                        },
                                        onClick = {
                                            categoryExpanded = false
                                            showCreateCollectionDialog = true
                                        }
                                    )
                                }
                            }
                        }

                        // Tingkatan Kesulitan
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Tingkatan Kesulitan", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                difficultiesList.forEach { diff ->
                                    val isSelected = difficulty == diff
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { difficulty = diff },
                                        label = { Text(diff) }
                                    )
                                }
                            }
                        }

                        // Waktu & Porsi
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = portions,
                                onValueChange = { portions = it },
                                label = { Text("Porsi (Org)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            OutlinedTextField(
                                value = prepTimeMinutes,
                                onValueChange = { prepTimeMinutes = it },
                                label = { Text("Waktu (Mnt)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            )
                        }
                    }
                }
            }

            // 3. Daftar Bahan (Ingredients)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Daftar Bahan & Takaran", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = ingredientName,
                                onValueChange = { ingredientName = it },
                                label = { Text("Nama Bahan") },
                                singleLine = true,
                                modifier = Modifier.weight(2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = ingredientAmount,
                                onValueChange = { ingredientAmount = it },
                                label = { Text("Jumlah") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = ingredientUnit,
                                onValueChange = { ingredientUnit = it },
                                label = { Text("Satuan") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Button(
                            onClick = {
                                if (ingredientName.isNotBlank()) {
                                    val amountVal = ingredientAmount.replace(',', '.').toDoubleOrNull() ?: 1.0
                                    ingredientsList.value = ingredientsList.value + IngredientItem(ingredientName.trim(), amountVal, ingredientUnit.trim(), true)
                                    ingredientName = ""
                                    ingredientAmount = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenLight, contentColor = MenuKuGreenPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tambah Bahan")
                        }

                        if (ingredientsList.value.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                ingredientsList.value.forEachIndexed { index, item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("• ${item.name} (${item.amount} ${item.unit})", style = MaterialTheme.typography.bodyMedium)
                                        IconButton(onClick = {
                                            ingredientsList.value = ingredientsList.value.filterIndexed { i, _ -> i != index }
                                        }) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Langkah Memasak (Steps)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Langkah-Langkah Memasak", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = stepInstruction,
                            onValueChange = { stepInstruction = it },
                            label = { Text("Instruksi Langkah Memasak") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 2
                        )

                        Button(
                            onClick = {
                                if (stepInstruction.isNotBlank()) {
                                    val nextNum = stepsList.value.size + 1
                                    stepsList.value = stepsList.value + CookingStepItem(nextNum, stepInstruction.trim(), 300)
                                    stepInstruction = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenLight, contentColor = MenuKuGreenPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tambah Langkah Masak")
                        }

                        if (stepsList.value.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                stepsList.value.forEachIndexed { index, step ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${step.stepNumber}. ${step.instruction}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                                        IconButton(onClick = {
                                            stepsList.value = stepsList.value.filterIndexed { i, _ -> i != index }
                                        }) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Catatan Koki
            item {
                OutlinedTextField(
                    value = chefNote,
                    onValueChange = { chefNote = it },
                    label = { Text("Catatan Koki / Tips Khusus (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    minLines = 2
                )
            }

            if (errorMessage != null) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // 6. Submit Button
            item {
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            errorMessage = "Judul resep wajib diisi."
                        } else if (ingredientsList.value.isEmpty()) {
                            errorMessage = "Tambahkan minimal 1 bahan resep."
                        } else if (stepsList.value.isEmpty()) {
                            errorMessage = "Tambahkan minimal 1 langkah memasak."
                        } else {
                            val finalImgUrl = imageUrlString.ifBlank {
                                existingRecipe?.imageUrl ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuCzuNB4kexqxRqRAFXcsFJZvMZkYRN7yFJ7MUCoFWHvxSBIL4WshwRTA7dPFqiAUMmhW3s5tj7GQwPoydeB9mFPkNbf6-npvEBYtb53eUh3FRN-Z5XsE0U_Ky9VvJuXyEOJCzFTkjMueH0ASHqZNfE5On3G92PHfzZZZprS-TIc4MkcDEn5fit0XatIRvE9zcJoucylrfSqyWk_KZqOFhIEd9J2wTACpsjvdxB1ZNnX"
                            }

                            if (isEditing && existingRecipe != null) {
                                val updatedRecipe = existingRecipe.copy(
                                    title = title.trim(),
                                    subtitle = subtitle.trim().ifBlank { existingRecipe.subtitle },
                                    category = category,
                                    difficulty = difficulty,
                                    portions = portions.toIntOrNull() ?: existingRecipe.portions,
                                    prepTimeMinutes = prepTimeMinutes.toIntOrNull() ?: existingRecipe.prepTimeMinutes,
                                    imageUrl = finalImgUrl,
                                    ingredients = ingredientsList.value,
                                    steps = stepsList.value,
                                    chefNote = chefNote.ifBlank { existingRecipe.chefNote }
                                )
                                repository.updateRecipe(updatedRecipe)
                            } else {
                                val newRecipe = Recipe(
                                    id = "recipe-${System.currentTimeMillis()}",
                                    title = title.trim(),
                                    subtitle = subtitle.trim().ifBlank { "Resep lezat buatan sendiri" },
                                    imageUrl = finalImgUrl,
                                    rating = 5.0,
                                    reviewCount = 1,
                                    prepTimeMinutes = prepTimeMinutes.toIntOrNull() ?: 30,
                                    portions = portions.toIntOrNull() ?: 4,
                                    difficulty = difficulty,
                                    category = category,
                                    isAvailableInKitchen = true,
                                    missingIngredientsCount = 0,
                                    isFavorite = true,
                                    tags = listOf("Buatan Sendiri", category),
                                    ingredients = ingredientsList.value,
                                    steps = stepsList.value,
                                    chefNote = chefNote.ifBlank { "Nikmati selagi hangat bersama keluarga tercinta." }
                                )
                                repository.addRecipe(newRecipe)
                            }
                            onBackClick()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_recipe_btn"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary)
                ) {
                    Icon(imageVector = Icons.Default.RestaurantMenu, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEditing) "Simpan Perubahan Resep" else "Simpan & Tambahkan ke Katalog", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Dialog Tambah Koleksi Baru
    if (showCreateCollectionDialog) {
        AlertDialog(
            onDismissRequest = { showCreateCollectionDialog = false },
            title = { Text("Tambah Nama Koleksi Baru", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Masukkan nama koleksi baru pengguna untuk menyusun resep:", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = newCollectionNameInput,
                        onValueChange = { newCollectionNameInput = it },
                        label = { Text("Nama Koleksi Baru") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCollectionNameInput.isNotBlank()) {
                            val newTitle = newCollectionNameInput.trim()
                            repository.createCollection(
                                title = newTitle,
                                desc = "Koleksi khusus resep $newTitle",
                                icon = "eco",
                                color = "hijau",
                                recipeIds = emptyList()
                            )
                            category = newTitle
                            newCollectionNameInput = ""
                            showCreateCollectionDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary)
                ) {
                    Text("Tambah Koleksi")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateCollectionDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
