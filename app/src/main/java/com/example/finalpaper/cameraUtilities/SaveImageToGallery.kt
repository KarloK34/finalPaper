package com.example.finalpaper.cameraUtilities

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.OutputStream

fun saveImageToGallery(bitmap: Bitmap, context: Context) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val resolver: ContentResolver = context.contentResolver
    val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        val outputStream: OutputStream? = resolver.openOutputStream(it)
        outputStream.use { stream ->
            if (stream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
        }
    }
}