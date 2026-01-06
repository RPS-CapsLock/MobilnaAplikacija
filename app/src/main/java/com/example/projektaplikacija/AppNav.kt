package com.example.projektaplikacija

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projektaplikacija.screens.AlgoSettingsScreen
import com.example.projektaplikacija.screens.HomeScreen
import com.example.projektaplikacija.screens.MapScreen
import com.example.projektaplikacija.screens.OpenBoxScreen
import com.example.projektaplikacija.screens.ProfileScreen
import com.example.projektaplikacija.screens.SelectLocationsScreen

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
    var sharedViewModel: SharedViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = BottomNavigation.HOME,
        modifier = modifier
    ) {
        composable(BottomNavigation.HOME) { HomeScreen() }
        composable(BottomNavigation.OPEN_BOX) { OpenBoxScreen() }
        composable(BottomNavigation.PROFILE) { ProfileScreen() }

        composable(BottomNavigation.POT) {
            SelectLocationsScreen(onNext = { navController.navigate(PotRoutes.SETTINGS) }, sharedVm = sharedViewModel)
        }

        composable(PotRoutes.SELECT) {
            SelectLocationsScreen(onNext = { navController.navigate(PotRoutes.SETTINGS) }, sharedVm = sharedViewModel)
        }
        composable(PotRoutes.SETTINGS) {
            AlgoSettingsScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(PotRoutes.RESULT) },
                sharedVm = sharedViewModel
            )
        }
        composable(PotRoutes.RESULT) {
            MapScreen(onBack = { navController.popBackStack()}, sharedVm = sharedViewModel)
        }
    }
}
