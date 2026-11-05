package com.example.menuku.features.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
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
import com.example.menuku.data.models.TasteStory
import com.example.menuku.data.repository.MenuKuRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteTasteStoryScreen(
    repository: MenuKuRepository,
    onBackClick: () -> Unit
) {
    val recipes by repository.recipes.collectAsState()

    var dishName by remember { mutableStateOf("Soto Ayam Lamongan Istimewa") }
    var selectedRecipeTitle by remember { mutableStateOf("Soto Ayam Lamongan Asli") }
    var rating by remember { mutableFloatStateOf(5.0f) }
    var notes by remember { mutableStateOf("Kuah gurih kaya rempah, koya udang renyah maksimal. Sukses besar dan seluruh keluarga nambah!") }
    var uploadProgress by remember { mutableFloatStateOf(1.0f) }

    val photoUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCZ2gN_rh20QTAw5CtXJUnIOwuI40kNf7mN1EU4ptctpvYySApefhH8WpyB3XkR92ByNGd56d1b62AmQiYN0CiSU8_bJIZHn69N2wQmiURblAhxcQmJz6zmm3vW2i8Ab98DF151w6jEcdwzjYy0ZUzwyIyBHJpQ3aV0Rs8u1ZYbnIqwm22a8cwsJNsLNjU0MpwzqIIzwiYwjKNLRw4PafL_q-a-QKKxCV3haxp5eGQd"

    val ratingLabels = mapOf(
        1.0f to "1.0 - Kurang pas, perlu banyak latihan",
        2.0f to "2.0 - Cukup baik, bumbu agak kurang meresap",
        3.0f to "3.0 - Lumayan enak, porsi pas",
        4.0f to "4.0 - Mantap! Rasa mendekati resto idaman",
        5.0f to "5.0 - Sempurna! Seluruh keluarga nambah"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tulis Cerita Masakan", fontWeight = FontWeight.Bold) },
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
                .testTag("write_taste_story_form"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(shape = CircleShape, color = MenuKuOrangeLight) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, tint = MenuKuOrange, modifier = Modifier.size(14.dp))
                            Text("Dapur Berbintang Hari Ini", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MenuKuOrange)
                        }
                    }
                    Text("Dokumentasikan Kreasi Dapur Anda", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Simpan memori rasa, bumbu rahasia, serta foto hidangan istimewa buatan tangan sendiri.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Image Preview & Dropzone
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Foto Masakan",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, tint = MenuKuGreenPrimary, modifier = Modifier.size(14.dp))
                                    Text("Foto Terpilih (HD)", style = MaterialTheme.typography.labelSmall, color = MenuKuGreenPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Tray Options
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null, tint = MenuKuGreenPrimary, modifier = Modifier.size(18.dp))
                                Text("Kamera Langsung", style = MaterialTheme.typography.labelSmall)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, tint = MenuKuOrange, modifier = Modifier.size(18.dp))
                                Text("Buka Galeri HP", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            // Input Nama Masakan
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Nama Masakan *", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            Text("${dishName.length}/50", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        OutlinedTextField(
                            value = dishName,
                            onValueChange = { if (it.length <= 50) dishName = it },
                            singleLine = true,
                            leadingIcon = { Icon(imageVector = Icons.Default.Restaurant, contentDescription = null, tint = MenuKuGreenPrimary) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth().testTag("dish_name_input"),
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

            // Tautkan ke Buku Resep
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = MenuKuGreenPrimary)
                            Text("Tautkan ke Buku Resep", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(recipes) { rec ->
                                val isSelected = selectedRecipeTitle == rec.title
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedRecipeTitle = rec.title },
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

            // Star Rating Kepuasan Masak
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Tingkat Kepuasan Hasil Masakan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            (1..5).forEach { star ->
                                IconButton(
                                    onClick = { rating = star.toFloat() },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = "Star $star",
                                        tint = StarYellow,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }

                        Surface(shape = CircleShape, color = MenuKuOrangeLight) {
                            Text(
                                text = ratingLabels[rating] ?: "Sempurna!",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MenuKuOrange,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Catatan & Kesan Rasa
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Catatan & Kesan Rasa", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = { Text("Ceritakan rasa masakan Anda...") },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth().testTag("taste_notes_input"),
                            minLines = 3,
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

            // Upload Progress Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Foto Siap & Resolusi Asli", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MenuKuGreenPrimary)
                            Text("100%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MenuKuGreenPrimary)
                        }
                        LinearProgressIndicator(
                            progress = { uploadProgress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                            color = MenuKuGreenPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                    }
                }
            }

            // Submit Buttons
            item {
                Button(
                    onClick = {
                        if (dishName.isNotBlank()) {
                            val newStory = TasteStory(
                                id = "tst-${System.currentTimeMillis()}",
                                title = dishName,
                                recipeTitle = selectedRecipeTitle,
                                dateFormatted = "Hari ini, 21 Nov 2024",
                                timeFormatted = "12:30 WIB",
                                rating = rating,
                                photoUrl = photoUrl,
                                notes = notes,
                                tags = listOf("KreasiDapur", "Lezat", "MenuKu"),
                                cookTimeMinutes = 45,
                                portionCount = 4
                            )
                            repository.addTasteStory(newStory)
                            onBackClick()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_taste_story_btn"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MenuKuGreenPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Cerita Rasa", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))

                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Batal & Buang Perubahan", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
