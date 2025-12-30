package com.example.projektaplikacija.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projektaplikacija.ui.theme.screens.AlgoSettingsScreen
import com.example.projektaplikacija.ui.theme.screens.HomeScreen
import com.example.projektaplikacija.ui.theme.screens.MapScreen
import com.example.projektaplikacija.ui.theme.screens.OpenBoxScreen
import com.example.projektaplikacija.ui.theme.screens.ProfileScreen
import com.example.projektaplikacija.ui.theme.screens.SelectLocationsScreen

object BottomNavigation {
    const val HOME = "home"
    const val OPEN_BOX = "open_box"
    const val POT = "pot"
    const val PROFILE = "profile"
}

object PotRoutes {
    const val SELECT = "pot_select"
    const val SETTINGS = "pot_settings"
    const val RESULT = "pot_result"
}

@Composable
fun AppNav(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavigation.HOME,
        modifier = modifier
    ) {
        composable(BottomNavigation.HOME) { HomeScreen() }
        composable(BottomNavigation.OPEN_BOX) { OpenBoxScreen() }
        composable(BottomNavigation.PROFILE) { ProfileScreen() }

        composable(BottomNavigation.POT) {
            SelectLocationsScreen(onNext = { navController.navigate(PotRoutes.SETTINGS) })
        }

        composable(PotRoutes.SELECT) {
            SelectLocationsScreen(onNext = { navController.navigate(PotRoutes.SETTINGS) })
        }
        composable(PotRoutes.SETTINGS) {
            AlgoSettingsScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(PotRoutes.RESULT) }
            )
        }
        composable(PotRoutes.RESULT) {
            MapScreen(onBack = { navController.popBackStack() })
        }
    }
}
