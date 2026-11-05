package com.example.menuku.features.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.menuku.core.components.MenuKuTopBar
import com.example.menuku.core.components.RecipeCard
import com.example.menuku.core.navigation.Screen
import com.example.menuku.data.repository.MenuKuRepository
import com.example.ui.theme.*

@Composable
fun RecipeCatalogScreen(

    repository: MenuKuRepository,
    onNavigate: (String) -> Unit
) {
    val recipes by repository.recipes.collectAsState()
    val collections by repository.collections.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua Resep") }
    var filterDropdownExpanded by remember { mutableStateOf(false) }

    val categories = remember(collections) {
        val staticCats = listOf(
            "Semua Resep",
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

    val filteredRecipes = recipes.filter { recipe ->
        val matchesSearch = recipe.title.contains(searchQuery, ignoreCase = true) ||
                recipe.tags.any { it.contains(searchQuery, ignoreCase = true) }
        val matchesCategory = if (selectedCategory == "Semua Resep") {
            true
        } else {
            val matchingCol = collections.find { it.title.equals(selectedCategory, ignoreCase = true) }
            if (matchingCol != null) {
                matchingCol.recipeIds.contains(recipe.id) || recipe.category.equals(selectedCategory, ignoreCase = true)
            } else {
                recipe.category.equals(selectedCategory, ignoreCase = true) ||
                        recipe.tags.any { it.equals(selectedCategory, ignoreCase = true) }
            }
        }
        matchesSearch && matchesCategory
    }

    // Matching collection based on selected category grouping
    val matchingCollection = if (selectedCategory == "Semua Resep") {
        null
    } else {
        collections.find { it.title.equals(selectedCategory, ignoreCase = true) }
            ?: when (selectedCategory) {
                "Diet Sehat" -> collections.find { it.id == "diet-sehat" }
                "Sarapan" -> collections.find { it.id == "favorit-si-kecil" }
                "Camilan & Dessert" -> collections.find { it.id == "kilat-20" }
                else -> null
            }
    }

    Scaffold(
        topBar = {
            MenuKuTopBar(
                title = "Katalog Resep",
                subtitle = "Eksplorasi Masakan Lezat",
                onNotificationClick = { onNavigate(Screen.NotificationCenter.route) },
                onProfileClick = { onNavigate(Screen.Profile.route) }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigate(Screen.CreateRecipe.route) },
                containerColor = MenuKuOrangeContainer,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("recipe_catalog_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Tambah Resep", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("recipe_grid"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search Bar with Filter Dropdown on Right
            item(span = { GridItemSpan(2) }) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari resep, bahan, atau masakan...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline
                        )
                    },
                    trailingIcon = {
                        Box {
                            IconButton(onClick = { filterDropdownExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Filter Kategori",
                                    tint = if (selectedCategory != "Semua Resep") MenuKuGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            DropdownMenu(
                                expanded = filterDropdownExpanded,
                                onDismissRequest = { filterDropdownExpanded = false }
                            ) {
                                Text(
                                    text = "Pilih Kategori",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = MenuKuGreenPrimary
                                )
                                HorizontalDivider()
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        trailingIcon = {
                                            if (selectedCategory == cat) {
                                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = MenuKuGreenPrimary, modifier = Modifier.size(16.dp))
                                            }
                                        },
                                        onClick = {
                                            selectedCategory = cat
                                            filterDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    singleLine = true,
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recipe_search_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MenuKuGreenPrimary
                    )
                )
            }

            // Category-Grouped Collection Banner (Routes to matching collection)
            if (matchingCollection != null) {
                item(span = { GridItemSpan(2) }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onNavigate(Screen.RecipeCollection.createRoute(matchingCollection.id))
                            }
                            .testTag("recipe_collection_banner"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
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
                                Icon(
                                    imageVector = Icons.Default.FolderSpecial,
                                    contentDescription = null,
                                    tint = MenuKuGreenPrimary
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Koleksi Terkait: ${matchingCollection.title}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Eksplorasi resep unggulan dalam kategori $selectedCategory",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Seksi Daftar Koleksi Resep & Tombol Tambah Koleksi
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = MenuKuGreenPrimary, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Daftar Koleksi Resep",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
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
                            Text("Tambah Koleksi", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(collections) { col ->
                            Card(
                                modifier = Modifier
                                    .width(160.dp)
                                    .clickable { onNavigate(Screen.RecipeCollection.createRoute(col.id)) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(MenuKuGreenLight),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FolderSpecial,
                                                contentDescription = null,
                                                tint = MenuKuGreenPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                                        ) {
                                            Text(
                                                text = "${col.recipeCount} Resep",
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = col.title,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        item {
                            OutlinedButton(
                                onClick = { onNavigate(Screen.CreateCollection.route) },
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(78.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MenuKuGreenPrimary)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Tambah Koleksi", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Catalog Header with Count & Sorter
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Katalog Masakan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        ) {
                            Text(
                                text = "${filteredRecipes.size}",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = null,
                            tint = MenuKuGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Terpopuler",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MenuKuGreenPrimary
                        )
                    }
                }
            }

            // 2-Column Recipe Grid Items
            items(filteredRecipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onClick = { onNavigate(Screen.RecipeDetail.createRoute(recipe.id)) },
                    onFavoriteToggle = { repository.toggleFavoriteRecipe(recipe.id) }
                )
            }

            // Bottom Spacing for FAB
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}
