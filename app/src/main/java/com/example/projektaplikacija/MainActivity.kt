package com.example.projektaplikacija

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.projektaplikacija.ui.theme.AppNav
import com.example.projektaplikacija.ui.theme.BottomNavigation
import com.example.projektaplikacija.ui.theme.PotRoutes
import com.example.projektaplikacija.ui.theme.ProjektAplikacijaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjektAplikacijaTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                fun navigateTo(route: String) {
                    navController.navigate(route) {
                        popUpTo(BottomNavigation.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {

                            NavigationBarItem(
                                selected = currentRoute == BottomNavigation.HOME,
                                onClick = { navigateTo(BottomNavigation.HOME) },
                                icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                                label = { Text("Home") }
                            )

                            NavigationBarItem(
                                selected = currentRoute == BottomNavigation.OPEN_BOX,
                                onClick = { navigateTo(BottomNavigation.OPEN_BOX) },
                                icon = { Icon(Icons.Filled.Inventory2, contentDescription = "Open Box") },
                                label = { Text("Open Box") }
                            )

                            NavigationBarItem(
                                selected = currentRoute == BottomNavigation.POT || currentRoute in setOf(PotRoutes.SELECT, PotRoutes.SETTINGS, PotRoutes.RESULT),
                                onClick = { navigateTo(BottomNavigation.POT) },
                                icon = { Icon(Icons.Filled.Map, contentDescription = "Pot") },
                                label = { Text("Pot") }
                            )

                            NavigationBarItem(
                                selected = currentRoute == BottomNavigation.PROFILE,
                                onClick = { navigateTo(BottomNavigation.PROFILE) },
                                icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                                label = { Text("Profile") }
                            )
                        }
                    }
                ) { innerPadding ->
                    AppNav(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}