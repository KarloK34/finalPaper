package com.example.finalpaper.screens

sealed class Screen(val route: String) {
    data object Home: Screen("home")
    data object Magnifier: Screen("magnifier")
    data object Navigation: Screen("navigation")
}