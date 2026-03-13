package com.example.yangdnashabschlussprojekt.feature.ui

import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val hint: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SharedOnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = listOf(
        OnboardingPage(
            hint = "Live-Modus",
            title = "Objekte im Kamerabild erkennen",
            description = "SmartVision blendet erkannte Objekte direkt im Live-Bild ein und macht die Umgebung schneller lesbar.",
            icon = Icons.Default.AutoAwesome
        ),
        OnboardingPage(
            hint = "Nicht wackeln!",
            title = "Texte stabil erfassen und verstehen",
            description = "Schilder, Dokumente und Hinweise koennen lokal oder ueber Cloud-OCR erkannt und anschliessend uebersetzt werden.",
            icon = Icons.AutoMirrored.Filled.TextSnippet
        ),
        OnboardingPage(
            hint = "Scan & Translate",
            title = "Ergebnisse hoeren und spueren",
            description = "Die Android-App kombiniert Uebersetzung, Vorlesen und haptisches Feedback zu einer echten Assistenz-Erfahrung.",
            icon = Icons.Default.CameraAlt
        ),
        OnboardingPage(
            hint = "Online",
            title = "Dieselbe Produktidee auf jeder Plattform",
            description = "iOS folgt demselben SmartVision-Flow. Desktop bleibt der Companion fuer Spiegelung, Verlauf und spaetere Erweiterungen.",
            icon = Icons.Default.CloudUpload
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()
    val backgroundGradient = Brush.verticalGradient(
        colors = if (isDark) {
            listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                MaterialTheme.colorScheme.background
            )
        } else {
            listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                MaterialTheme.colorScheme.background
            )
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                SharedOnboardingContent(
                    data = pages[pageIndex],
                    index = pageIndex
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(if (isSelected) 10.dp else 6.dp)
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.28f),
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage == pages.lastIndex) {
                            onFinished()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.lastIndex) "Starten" else "Weiter",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (pagerState.currentPage < pages.lastIndex) {
            TextButton(
                onClick = onFinished,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Ueberspringen",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.68f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SharedOnboardingContent(
    data: OnboardingPage,
    index: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp)
    ) {
        when (index) {
            0 -> SharedLiveDetectionIllustration(data.icon)
            1 -> SharedFocusIllustration()
            2 -> SharedAnimatedScanIllustration()
            3 -> SharedLiveDetectionIllustration(data.icon)
        }
        Text(
            text = data.title,
            style = MaterialTheme.typography.headlineLarge.copy(
                letterSpacing = (-1.5).sp,
                fontWeight = FontWeight.ExtraBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = data.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(48.dp))
        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = data.hint,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SharedLiveDetectionIllustration(icon: ImageVector) {
    val infiniteTransition = rememberInfiniteTransition(label = "shared-radar")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(4000, easing = LinearEasing)
        ),
        label = "shared-radar-rotation"
    )
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
                .background(
                    brush = Brush.sweepGradient(
                        0.0f to androidx.compose.ui.graphics.Color.Transparent,
                        0.5f to MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        1.0f to androidx.compose.ui.graphics.Color.Transparent
                    ),
                    shape = CircleShape
                )
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SharedFocusIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "shared-focus")
    val lineMove by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = EaseInOutQuart),
            RepeatMode.Reverse
        ),
        label = "shared-focus-line-move"
    )

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.TextSnippet,
            contentDescription = null,
            modifier = Modifier.size(70.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
        val color = MaterialTheme.colorScheme.primary
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            val stroke = 5.dp.toPx()
            val length = 35.dp.toPx()
            val offset = lineMove.dp.toPx()

            drawLine(color, Offset(offset, offset), Offset(offset + length, offset), stroke, StrokeCap.Round)
            drawLine(color, Offset(offset, offset), Offset(offset, offset + length), stroke, StrokeCap.Round)
            drawLine(color, Offset(size.width - offset, offset), Offset(size.width - offset - length, offset), stroke, StrokeCap.Round)
            drawLine(color, Offset(size.width - offset, offset), Offset(size.width - offset, offset + length), stroke, StrokeCap.Round)
            drawLine(color, Offset(offset, size.height - offset), Offset(offset + length, size.height - offset), stroke, StrokeCap.Round)
            drawLine(color, Offset(offset, size.height - offset), Offset(offset, size.height - offset - length), stroke, StrokeCap.Round)
            drawLine(color, Offset(size.width - offset, size.height - offset), Offset(size.width - offset - length, size.height - offset), stroke, StrokeCap.Round)
            drawLine(color, Offset(size.width - offset, size.height - offset), Offset(size.width - offset, size.height - offset - length), stroke, StrokeCap.Round)
        }
    }
}

@Composable
private fun SharedAnimatedScanIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "shared-scan-glow")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = EaseInOutSine),
            RepeatMode.Reverse
        ),
        label = "shared-scan-scale"
    )
    val blurRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = EaseInOutSine),
            RepeatMode.Reverse
        ),
        label = "shared-scan-blur"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(200.dp)
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .scale(scale)
                .blur(blurRadius.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    CircleShape
                )
        )
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            tonalElevation = 8.dp
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxSize(),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
