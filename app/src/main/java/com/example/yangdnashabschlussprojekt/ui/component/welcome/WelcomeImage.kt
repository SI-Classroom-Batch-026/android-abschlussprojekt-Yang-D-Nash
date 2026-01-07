package com.example.yangdnashabschlussprojekt.ui.component.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.R

@Composable
fun WelcomeImage(
    modifier: Modifier = Modifier,
    imageRes: Int = R.drawable.smartvisionmain
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.size(180.dp).blur(40.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            shape = CircleShape
        ) {}

        Image(
            painter = painterResource(id = imageRes),
            // Nutzt stringResource für Barrierefreiheit
            contentDescription = stringResource(R.string.welcome_image_desc),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(220.dp)
                .padding(16.dp)
        )
    }
}