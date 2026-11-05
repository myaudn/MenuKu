package com.example.menuku.core.navigation

sealed class Screen(val route: String) {
    // Top-level Bottom Navigation Screens
    object Hub : Screen("hub")
    object Recipes : Screen("recipes")
    object Schedule : Screen("schedule")
    object Inventory : Screen("inventory")
    object Shopping : Screen("shopping")

    // Sub-screens & Detail Flows
    object RecipeDetail : Screen("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_detail/$recipeId"
    }
    object CreateRecipe : Screen("create_recipe?recipeId={recipeId}") {
        fun createRoute(recipeId: String? = null) = if (recipeId != null) "create_recipe?recipeId=$recipeId" else "create_recipe"
    }
    object RecipeCollection : Screen("recipe_collection/{collectionId}") {
        fun createRoute(collectionId: String) = "recipe_collection/$collectionId"
    }
    object CreateCollection : Screen("create_collection")

    object IngredientDetail : Screen("ingredient_detail/{stockId}") {
        fun createRoute(stockId: String) = "ingredient_detail/$stockId"
    }
    object AddIngredient : Screen("add_ingredient")

    object ScheduleDetail : Screen("schedule_detail/{scheduleId}") {
        fun createRoute(scheduleId: String) = "schedule_detail/$scheduleId"
    }
    object AddSchedule : Screen("add_schedule")

    object ShoppingItemDetail : Screen("shopping_detail/{itemId}") {
        fun createRoute(itemId: String) = "shopping_detail/$itemId"
    }
    object AddShoppingItem : Screen("add_shopping_item")

    object TasteStoryGallery : Screen("taste_story_gallery")
    object TasteStoryDetail : Screen("taste_story_detail/{storyId}") {
        fun createRoute(storyId: String) = "taste_story_detail/$storyId"
    }
    object WriteTasteStory : Screen("write_taste_story")

    object Profile : Screen("profile")
    object NotificationCenter : Screen("notifications")
    object Auth : Screen("auth")
    object Login : Screen("login")
    object Register : Screen("register")
}
