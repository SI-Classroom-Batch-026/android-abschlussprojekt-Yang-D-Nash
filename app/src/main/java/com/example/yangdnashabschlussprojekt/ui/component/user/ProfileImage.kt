package com.example.yangdnashabschlussprojekt.ui.component.user

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter

@Composable
fun ProfileImage(
    profileImageUri: Uri? = null,
    onImageSelected: (Uri) -> Unit = {},
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    val painter = rememberAsyncImagePainter(model = profileImageUri)

    Image(
        painter = painter,
        contentDescription = "Profilbild",
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(Color.Gray)
            .clickable { launcher.launch("image/*") }
    )
}
