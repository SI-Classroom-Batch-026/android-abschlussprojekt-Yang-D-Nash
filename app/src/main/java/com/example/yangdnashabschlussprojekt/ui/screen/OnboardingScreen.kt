package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.data.local.onBoarding.OnboardingData
import com.example.yangdnashabschlussprojekt.ui.component.onBoarding.OnboardingContent
import com.example.yangdnashabschlussprojekt.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pages = listOf(
        OnboardingData("Live-Erkennung", "Objekte werden in Echtzeit erkannt.", "Live-Modus", Icons.Default.AutoAwesome),
        OnboardingData("Fokus halten", "Halte die Kamera für Text-Scans absolut ruhig.", "Nicht wackeln!", Icons.AutoMirrored.Filled.TextSnippet),
        OnboardingData("Präziser Scan", "Tippe den Button für Cloud-KI Übersetzungen.", "Scan & Translate", Icons.Default.CameraAlt),
        OnboardingData("Cloud-Power", "Maximale Präzision durch modernste Server.", "Online", Icons.Default.CloudUpload)
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    val bgGradient = Brush.verticalGradient(
        if (isDark) listOf(DeepSpaceCyan, DeepSpaceBlack)
        else listOf(LightBgStart, LightBgEnd)
    )

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        if (pagerState.currentPage < pages.size - 1) {
            TextButton(
                onClick = onFinished,
                modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(12.dp)
            ) {
                Text("Überspringen", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(24.dp)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { index ->
                OnboardingContent(pages[index], index)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    repeat(pages.size) { i ->
                        val isSelected = pagerState.currentPage == i
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(if (isSelected) 10.dp else 6.dp)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                                    CircleShape
                                )
                        )
                    }
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onFinished()
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.size - 1) "Starten" else "Weiter",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}