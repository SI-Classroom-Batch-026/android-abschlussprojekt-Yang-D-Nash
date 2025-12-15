package com.example.yangdnashabschlussprojekt.ui.component.user

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person // NEU: Standard-Person-Icon
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale // NEU: Import
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    profileImageUri: Uri? = null,
    onImageSelected: (Uri) -> Unit = {},
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    val painter = rememberAsyncImagePainter(model = profileImageUri)

    val imageModifier = modifier
        .size(100.dp)
        .clip(CircleShape)
        .clickable { launcher.launch("image/*") }

    if (profileImageUri == null) {
        Box(
            modifier = imageModifier
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Profilbild auswählen oder ändern",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(60.dp)
            )
        }
    } else {
        Image(
            painter = painter,
            contentDescription = "Aktuelles Profilbild. Zum Ändern tippen.",
            contentScale = ContentScale.Crop,
            modifier = imageModifier
                .background(Color.Gray)
        )
    }
}