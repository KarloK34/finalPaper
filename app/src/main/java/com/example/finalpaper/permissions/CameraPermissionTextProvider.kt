package com.example.finalpaper.permissions

class CameraPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you've permanently declined camera permission. " +
                    "You can go to the app settings to grant it."
        } else {
            "This app needs access to your camera."
        }
    }
}