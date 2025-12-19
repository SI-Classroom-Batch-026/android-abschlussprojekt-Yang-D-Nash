package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.data.local.onBoarding.OnboardingData
import com.example.yangdnashabschlussprojekt.ui.component.FocusIllustration
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pages = listOf(
        OnboardingData(
            "Live-Erkennung",
            "Objekte werden automatisch in deiner Sprache erkannt, während du dich bewegst.",
            "Live-Modus",
            Icons.Default.AutoAwesome
        ),
        OnboardingData(
            "Fokus halten",
            "Halte die Kamera ca. 1 Sekunde absolut ruhig auf den Text für optimale Ergebnisse.",
            "Wichtig: Nicht wackeln!",
            Icons.AutoMirrored.Filled.TextSnippet
        ),
        OnboardingData(
            "Cloud-Power",
            "Nutze die Cloud, um erkannte Texte präzise und schnell übersetzen zu lassen.",
            "Online-Übersetzung",
            Icons.Default.CloudUpload
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { index ->
            val data = pages[index]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (index == 1) {
                    FocusIllustration(modifier = Modifier.padding(bottom = 40.dp))
                } else {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = null,
                        modifier = Modifier.size(130.dp).padding(bottom = 40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = data.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = data.desc,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(32.dp))

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = data.hint,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                repeat(pages.size) { i ->
                    val isSelected = pagerState.currentPage == i
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (isSelected) 12.dp else 8.dp)
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                shape = CircleShape
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
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage == pages.size - 1) "Los geht's" else "Weiter",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

