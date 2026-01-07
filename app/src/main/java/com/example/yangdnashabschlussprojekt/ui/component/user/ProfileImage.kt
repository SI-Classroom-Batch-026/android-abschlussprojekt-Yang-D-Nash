package com.example.yangdnashabschlussprojekt.ui.component.user

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.yangdnashabschlussprojekt.R

@Composable
fun ProfileImage(
    profileImageUri: Uri? = null,
    onImageSelected: (Uri) -> Unit = {},
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { onImageSelected(it) } }

    val painter = rememberAsyncImagePainter(model = profileImageUri)
    val accessibilityDesc = stringResource(R.string.profile_image_desc)

    Box(
        modifier = Modifier
            .size(110.dp)
            .border(
                BorderStroke(2.dp, Brush.linearGradient(listOf(Color.Cyan, Color.Magenta))),
                CircleShape
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        val imgMod = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .clickable { launcher.launch("image/*") }

        if (profileImageUri == null) {
            Box(
                modifier = imgMod.background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = accessibilityDesc,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
        } else {
            Image(
                painter = painter,
                contentDescription = accessibilityDesc,
                contentScale = ContentScale.Crop,
                modifier = imgMod
            )
        }
    }
}