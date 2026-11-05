package com.example.menuku.features.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.menuku.core.components.MenuKuTopBar
import com.example.menuku.core.navigation.Screen
import com.example.menuku.data.repository.MenuKuRepository
import com.example.ui.theme.*

@Composable
fun RecipeCollectionScreen(
    collectionId: String,
    repository: MenuKuRepository,
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val collections by repository.collections.collectAsState()
    val recipes by repository.recipes.collectAsState()

    val collection = collections.find { it.id == collectionId }
    if (collection == null) {
        LaunchedEffect(Unit) {
            onBackClick()
        }
        return
    }

    val collectionRecipes = recipes.filter { collection.recipeIds.contains(it.id) || it.category.equals(collection.title, ignoreCase = true) }

    // Edit Collection State
    var showEditDialog by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf(collection.title) }
    var editDesc by remember { mutableStateOf(collection.description) }
    var editRecipeIds by remember { mutableStateOf(collection.recipeIds.toSet()) }

    Scaffold(
        topBar = {
            MenuKuTopBar(
                title = collection.title,
                subtitle = "Detail Koleksi & Kategori",
                showBackButton = true,
                onBackClick = onBackClick,
                onNotificationClick = { onNavigate(Screen.NotificationCenter.route) },
                onProfileClick = { onNavigate(Screen.Profile.route) },
                actions = {
                    IconButton(onClick = {
                        editTitle = collection.title
                        editDesc = collection.description
                        editRecipeIds = collection.recipeIds.toSet()
                        showEditDialog = true
                    }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Koleksi", tint = MenuKuGreenPrimary)
                    }
                    IconButton(onClick = {
                        repository.deleteCollection(collection.id)
                        onBackClick()
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus Koleksi", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("recipe_collection_list"),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Collection Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            AsyncImage(
                                model = collection.coverUrl,
                                contentDescription = collection.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.9f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = MenuKuGreenPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Koleksi & Kategori Utama",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = collection.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = collection.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // 2 Metric Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("${collection.recipeCount} Resep", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                        Text("Tersimpan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(collection.difficulty, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = MenuKuTertiary)
                                        Text("Tingkat", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }

                            // Author Sticky Note
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MenuKuOrangeLight.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.StickyNote2, contentDescription = null, tint = MenuKuOrange)
                                    Column {
                                        Text(
                                            text = "Catatan Koleksi",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MenuKuOrangeOnContainer
                                        )
                                        Text(
                                            text = collection.authorNote,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MenuKuOrangeOnContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Recipe List Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Resep dalam Koleksi ini",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${collectionRecipes.size} Hidangan Tersedia",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { onNavigate(Screen.CreateCollection.route) },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah Koleksi", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Recipes in Collection
            items(collectionRecipes) { recipe ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onNavigate(Screen.RecipeDetail.createRoute(recipe.id)) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = recipe.imageUrl,
                                    contentDescription = recipe.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(4.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.Black.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = "${recipe.prepTimeMinutes} mnt",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = recipe.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = recipe.tags.firstOrNull() ?: "Sehat", style = MaterialTheme.typography.labelSmall, color = MenuKuGreenPrimary)
                                }

                                Button(
                                    onClick = { onNavigate(Screen.RecipeDetail.createRoute(recipe.id)) },
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(containerColor = MenuKuOrangeContainer),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.SoupKitchen, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Masak Sekarang", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        // Specific recipe note
                        if (recipe.chefNote.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.EditNote, contentDescription = null, tint = MenuKuOrange, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = recipe.chefNote,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Collection Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Koleksi", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Nama Koleksi") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Deskripsi Koleksi") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Text("Kelola Resep dalam Koleksi:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    recipes.forEach { recipe ->
                        val isChecked = editRecipeIds.contains(recipe.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    editRecipeIds = if (isChecked) editRecipeIds - recipe.id else editRecipeIds + recipe.id
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(recipe.title, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    editRecipeIds = if (checked) editRecipeIds + recipe.id else editRecipeIds - recipe.id
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedCol = collection.copy(
                            title = editTitle,
                            description = editDesc,
                            recipeIds = editRecipeIds.toList(),
                            recipeCount = editRecipeIds.size
                        )
                        repository.updateCollection(updatedCol)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary)
                ) {
                    Text("Simpan Perubahan")
                }
            },
            dismissButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            repository.deleteCollection(collection.id)
                            showEditDialog = false
                            onBackClick()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hapus")
                    }
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Batal")
                    }
                }
            }
        )
    }
}
