package com.example

import com.example.menuku.data.models.MealSchedule
import com.example.menuku.data.models.MealSlot
import com.example.menuku.data.models.ShoppingItem
import com.example.menuku.data.models.StockItem
import com.example.menuku.data.models.StorageLocation
import com.example.menuku.data.models.UrgencyLevel
import com.example.menuku.data.repository.MenuKuRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MenuKuRepositoryTest {

    private lateinit var repository: MenuKuRepository

    @Before
    fun setUp() {
        repository = MenuKuRepository()
    }

    @Test
    fun testInitialRecipesLoaded() {
        val recipes = repository.recipes.value
        assertTrue("Recipes should not be empty", recipes.isNotEmpty())
        val soto = recipes.find { it.id == "soto-ayam" }
        assertNotNull("Soto Ayam Lamongan should be present", soto)
        assertEquals("Soto Ayam Lamongan Asli", soto?.title)
    }

    @Test
    fun testToggleFavoriteRecipe() {
        val recipeId = "soto-ayam"
        val initialFav = repository.recipes.value.find { it.id == recipeId }?.isFavorite ?: false
        repository.toggleFavoriteRecipe(recipeId)
        val afterFav = repository.recipes.value.find { it.id == recipeId }?.isFavorite
        assertEquals(!initialFav, afterFav)
    }

    @Test
    fun testStockItemUrgencyCalculation() {
        val urgentItem = StockItem(
            id = "test-1",
            name = "Ayam Fillet",
            quantity = 500.0,
            unit = "gram",
            location = StorageLocation.KULKAS,
            category = "Daging & Telur",
            purchaseDate = "19 Nov 2024",
            expiryDate = "21 Nov 2024",
            remainingDays = 2
        )
        assertTrue("Item with remaining days <= 2 must be urgent", urgentItem.isUrgent)
        assertEquals("CRITICAL", urgentItem.statusColorType)
    }

    @Test
    fun testShoppingItemPurchasedSyncToStock() {
        val initialStockCount = repository.stockItems.value.size
        val shoppingItem = ShoppingItem(
            id = "test-shp-1",
            name = "Wortel Manis",
            quantity = 250.0,
            unit = "gram",
            category = "sayur",
            urgency = UrgencyLevel.NORMAL,
            isPurchased = false
        )
        repository.addShoppingItem(shoppingItem)
        repository.toggleShoppingItemPurchased(shoppingItem.id)

        // When auto-sync is enabled in user profile, purchased items sync into pantry stock
        val afterStockCount = repository.stockItems.value.size
        assertTrue(afterStockCount > initialStockCount)
        val addedItem = repository.stockItems.value.find { it.name == "Wortel Manis" }
        assertNotNull(addedItem)
    }

    @Test
    fun testScheduleAutoSyncMissingIngredients() {
        val initialShoppingCount = repository.shoppingItems.value.size
        val rendang = repository.recipes.value.find { it.id == "rendang-sapi" }
        assertNotNull(rendang)

        val schedule = MealSchedule(
            id = "test-sch-rendang",
            dateStr = "2024-11-23",
            dayName = "Sabtu",
            dayNumber = 23,
            slot = MealSlot.SIANG,
            recipe = rendang,
            isReadyInFridge = false,
            autoSyncGrocery = true
        )

        repository.addOrUpdateSchedule(schedule)

        // Missing ingredients from Rendang (e.g. Santan, Ambual) should auto-add to shopping list
        val afterShoppingCount = repository.shoppingItems.value.size
        assertTrue("Shopping items should increase with missing recipe ingredients", afterShoppingCount > initialShoppingCount)
    }
}
