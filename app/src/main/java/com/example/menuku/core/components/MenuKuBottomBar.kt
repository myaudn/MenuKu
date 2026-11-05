package com.example.menuku.core.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.menuku.core.navigation.Screen
import com.example.ui.theme.MenuKuGreenPrimary

sealed class BottomNavItem(
    val screen: Screen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Hub : BottomNavItem(Screen.Hub, "Hub", Icons.Filled.Home, Icons.Outlined.Home, "bottom_nav_hub")
    object Recipes : BottomNavItem(Screen.Recipes, "Resep", Icons.Filled.RestaurantMenu, Icons.Outlined.RestaurantMenu, "bottom_nav_recipes")
    object Schedule : BottomNavItem(Screen.Schedule, "Jadwal", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "bottom_nav_schedule")
    object Inventory : BottomNavItem(Screen.Inventory, "Stok", Icons.Filled.Kitchen, Icons.Outlined.Kitchen, "bottom_nav_inventory")
    object Shopping : BottomNavItem(Screen.Shopping, "Belanja", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag, "bottom_nav_shopping")
}

@Composable
fun MenuKuBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Hub,
        BottomNavItem.Recipes,
        BottomNavItem.Schedule,
        BottomNavItem.Inventory,
        BottomNavItem.Shopping
    )

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp,
            windowInsets = NavigationBarDefaults.windowInsets
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.screen.route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            onNavigate(item.screen)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = false
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MenuKuGreenPrimary,
                        indicatorColor = MenuKuGreenPrimary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag(item.testTag)
                )
            }
        }
    }
}


