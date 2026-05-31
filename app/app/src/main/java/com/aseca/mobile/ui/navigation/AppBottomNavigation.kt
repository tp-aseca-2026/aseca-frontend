package com.aseca.mobile.ui.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.aseca.mobile.navigation.AuthScreen
import com.aseca.mobile.ui.AuthColors

private data class BottomNavItem(
    val screen: AuthScreen,
    val label: String,
    val marker: String,
    val automationId: String,
)

private val bottomNavItems = listOf(
    BottomNavItem(AuthScreen.Home, "Inicio", "I", "nav_home"),
    BottomNavItem(AuthScreen.Portfolio, "Portfolio", "P", "nav_portfolio"),
    BottomNavItem(AuthScreen.Watchlist, "Watchlist", "W", "nav_watchlist"),
    BottomNavItem(AuthScreen.Edgar, "EDGAR", "E", "nav_edgar"),
    BottomNavItem(AuthScreen.Transactions, "Ops", "O", "nav_transactions"),
)

@Composable
fun AppBottomNavigation(
    currentScreen: AuthScreen,
    onNavigate: (AuthScreen) -> Unit,
) {
    NavigationBar(
        containerColor = Color(0xFF0C1017),
        contentColor = AuthColors.PrimaryText,
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentScreen == item.screen

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.screen) },
                modifier = Modifier.semantics { contentDescription = item.automationId },
                icon = {
                    Text(
                        text = item.marker,
                        fontWeight = FontWeight.Bold,
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF06100B),
                    selectedTextColor = AuthColors.Accent,
                    indicatorColor = AuthColors.Accent,
                    unselectedIconColor = AuthColors.MutedText,
                    unselectedTextColor = AuthColors.MutedText,
                ),
            )
        }
    }
}
