package com.example.finalpaper.permissions

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}