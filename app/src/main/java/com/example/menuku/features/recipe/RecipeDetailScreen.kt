package com.example.menuku.features.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.menuku.core.navigation.Screen
import com.example.menuku.data.models.ShoppingItem
import com.example.menuku.data.models.UrgencyLevel
import com.example.menuku.data.repository.MenuKuRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    repository: MenuKuRepository,
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val recipes by repository.recipes.collectAsState()
    val recipe = recipes.find { it.id == recipeId } ?: recipes.first()

    var showCookingModeDialog by remember { mutableStateOf(false) }
    var activeCookingStep by remember { mutableStateOf(1) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val readyIngredientsCount = recipe.ingredients.count { it.isReadyInPantry }
    val totalIngredientsCount = recipe.ingredients.size

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showCookingModeDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("recipe_detail_start_cooking_btn"),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.SoupKitchen, contentDescription = null)
                            Text(text = "Mulai Mode Masak", fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { onNavigate(Screen.AddSchedule.route) },
                        modifier = Modifier.height(50.dp),
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Jadwalkan")
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
                .testTag("recipe_detail_content"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Hero Image Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = recipe.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Top Gradient Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )

                    // Top Bar Floating Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.4f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = Color.White
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { onNavigate(Screen.CreateRecipe.createRoute(recipe.id)) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.4f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Resep",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = { repository.toggleFavoriteRecipe(recipe.id) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.4f))
                            ) {
                                Icon(
                                    imageVector = if (recipe.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favorit",
                                    tint = if (recipe.isFavorite) MenuKuError else Color.White
                                )
                            }
                        }
                    }

                    // Bottom info over hero
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MenuKuOrangeLight
                        ) {
                            Text(
                                text = recipe.category,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MenuKuOrange,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = recipe.title,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Recipe 4-column Bento Metrics
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = MenuKuGreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Waktu", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "${recipe.prepTimeMinutes} Mnt", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                    VerticalDivider(modifier = Modifier.height(30.dp), color = MaterialTheme.colorScheme.surfaceContainerHighest)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Groups, contentDescription = null, tint = MenuKuGreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Porsi", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "${recipe.portions} Orang", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                    VerticalDivider(modifier = Modifier.height(30.dp), color = MaterialTheme.colorScheme.surfaceContainerHighest)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = MenuKuOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Tingkat", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = recipe.difficulty, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Chef Note Card
            if (recipe.chefNote.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MenuKuOrangeLight.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FormatQuote, contentDescription = null, tint = MenuKuOrange)
                            Column {
                                Text(
                                    text = "Catatan Rahasia Dapur",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MenuKuOrangeOnContainer
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = recipe.chefNote,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MenuKuOrangeOnContainer
                                )
                            }
                        }
                    }
                }
            }

            // Pantry Readiness Status Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
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
                                Icon(imageVector = Icons.Default.Kitchen, contentDescription = null, tint = MenuKuGreenPrimary)
                                Text(
                                    text = "Status Kesiapan Bahan",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = if (readyIngredientsCount == totalIngredientsCount) MenuKuGreenLight else MenuKuOrangeLight
                            ) {
                                Text(
                                    text = if (readyIngredientsCount == totalIngredientsCount) "Siap Masak" else "Perlu Belanja",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (readyIngredientsCount == totalIngredientsCount) MenuKuGreenPrimary else MenuKuOrange,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { if (totalIngredientsCount > 0) readyIngredientsCount.toFloat() / totalIngredientsCount else 1f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = MenuKuGreenPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        )

                        Text(
                            text = "$readyIngredientsCount dari $totalIngredientsCount bahan tersedia di stok dapur Anda",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (readyIngredientsCount < totalIngredientsCount) {
                            Button(
                                onClick = {
                                    recipe.ingredients.filterNot { it.isReadyInPantry }.forEach { ing ->
                                        repository.addShoppingItem(
                                            ShoppingItem(
                                                id = "shp-${System.currentTimeMillis()}-${ing.name.hashCode()}",
                                                name = ing.name,
                                                quantity = ing.amount,
                                                unit = ing.unit,
                                                category = "bumbu",
                                                urgency = UrgencyLevel.URGENT,
                                                linkedRecipeTitle = recipe.title
                                            )
                                        )
                                    }
                                    snackbarMessage = "Bahan yang kurang telah ditambahkan ke Daftar Belanja!"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MenuKuOrangeContainer),
                                shape = CircleShape,
                                modifier = Modifier.fillMaxWidth().testTag("sync_to_shopping_list_btn")
                            ) {
                                Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Kirim Bahan Kurang ke Daftar Belanja")
                            }
                        }
                    }
                }
            }

            // Ingredients Section
            item {
                Text(
                    text = "Bahan-Bahan yang Diperlukan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(recipe.ingredients) { ingredient ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (ingredient.isReadyInPantry) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = if (ingredient.isReadyInPantry) MenuKuGreenPrimary else MaterialTheme.colorScheme.outline
                            )
                            Column {
                                Text(
                                    text = ingredient.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (ingredient.isReadyInPantry) "Tersedia di Kulkas" else "Belum Ada di Stok",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (ingredient.isReadyInPantry) MenuKuGreenPrimary else MenuKuOrange
                                )
                            }
                        }

                        val formattedAmount = if (ingredient.amount % 1.0 == 0.0) ingredient.amount.toInt().toString() else ingredient.amount.toString()
                        Text(
                            text = "$formattedAmount ${ingredient.unit}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MenuKuGreenPrimary
                        )
                    }
                }
            }

            // Cooking Steps Section
            item {
                Text(
                    text = "Langkah-Langkah Memasak",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            items(recipe.steps) { step ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MenuKuGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${step.stepNumber}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MenuKuGreenPrimary
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = step.instruction,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (step.tip.isNotBlank()) {
                                Text(
                                    text = "💡 Tips: ${step.tip}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MenuKuOrange,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Cooking Mode Dialog
    if (showCookingModeDialog) {
        AlertDialog(
            onDismissRequest = { showCookingModeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.SoupKitchen, contentDescription = null, tint = MenuKuGreenPrimary)
                    Text(text = "Mode Memasak Aktif", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                val currentStep = recipe.steps.getOrNull(activeCookingStep - 1)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Langkah $activeCookingStep dari ${recipe.steps.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MenuKuGreenPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentStep?.instruction ?: "Memasak hidangan lezat...",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!currentStep?.tip.isNullOrBlank()) {
                        Text(
                            text = "💡 ${currentStep?.tip}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MenuKuOrange
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (activeCookingStep < recipe.steps.size) {
                            activeCookingStep++
                        } else {
                            showCookingModeDialog = false
                            activeCookingStep = 1
                            onNavigate(Screen.WriteTasteStory.route)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary)
                ) {
                    Text(if (activeCookingStep < recipe.steps.size) "Langkah Selanjutnya" else "Selesai & Abadikan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCookingModeDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}
