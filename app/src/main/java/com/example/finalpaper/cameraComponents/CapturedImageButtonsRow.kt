package com.example.finalpaper.cameraComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.finalpaper.R

@Composable
fun CapturedImageButtonsRow(
    onSaveImage: () -> Unit,
    onReset: () -> Unit,
    onToggleFilterButtons: () -> Unit,
    showFilterButtons: Boolean
) {
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = onSaveImage,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .height(80.dp)
                .width(80.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_gallery),
                contentDescription = "Save to Gallery"
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(
            onClick = onReset,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .height(80.dp)
                .width(80.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                contentDescription = "Reset magnifier"
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(
            onClick = onToggleFilterButtons,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .height(80.dp)
                .width(80.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = if (!showFilterButtons) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                contentColor = if (!showFilterButtons) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_filter_list_24),
                contentDescription = "Filters"
            )
        }
    }
}