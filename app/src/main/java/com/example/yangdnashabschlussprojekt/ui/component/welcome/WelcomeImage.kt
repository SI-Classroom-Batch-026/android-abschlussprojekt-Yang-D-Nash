package com.example.yangdnashabschlussprojekt.ui.component.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.R

@Composable
fun WelcomeImage(
    modifier: Modifier = Modifier,
    imageRes: Int = R.drawable.smartvisionmain
) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Willkommens Icon",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}