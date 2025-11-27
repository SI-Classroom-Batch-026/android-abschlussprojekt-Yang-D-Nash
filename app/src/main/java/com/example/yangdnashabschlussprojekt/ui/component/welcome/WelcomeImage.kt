package com.example.yangdnashabschlussprojekt.ui.component.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.R

@Composable
fun WelcomeImage(
    modifier: Modifier = Modifier,
    imageRes: Int = R.drawable.smartvision
) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Willkommens Icon",
        modifier = modifier
            .clip(CircleShape)
            .fillMaxWidth()
            .height(160.dp)
            .padding(16.dp)
    )
}

