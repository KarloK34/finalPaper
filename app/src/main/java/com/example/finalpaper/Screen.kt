package com.example.finalpaper

sealed class Screen(val route: String) {
    data object Home: Screen("home")
    data object Magnifier: Screen("magnifier")
}