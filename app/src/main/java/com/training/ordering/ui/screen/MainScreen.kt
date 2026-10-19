package com.training.ordering.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.training.ordering.ui.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Products : Screen("products", "Menu", Icons.Default.ShoppingCart)
    object Orders : Screen("orders", "Orders", Icons.Default.List)
    object Menu : Screen("menu", "Menu", Icons.Default.Menu)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    var selectedScreen by rememberSaveable { mutableStateOf<Screen>(Screen.Products) }
    val items = listOf(Screen.Products, Screen.Orders, Screen.Menu, Screen.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = selectedScreen == screen,
                        onClick = { selectedScreen = screen }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedScreen) {
                is Screen.Products -> ProductsScreen(viewModel = viewModel)
                is Screen.Orders -> OrdersScreen(viewModel = viewModel)
                is Screen.Menu -> EmptyScreen(title = "Menu Screen (Empty)")
                is Screen.Settings -> EmptyScreen(title = "Settings Screen (Empty)")
            }
        }
    }
}

@Composable
fun EmptyScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text(text = title, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
    }
}