package com.example.yangdnashabschlussprojekt.feature.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val hint: String,
    val title: String,
    val description: String
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
            description = "SmartVision blendet erkannte Objekte direkt im Live-Bild ein und macht die Umgebung schneller lesbar."
        ),
        OnboardingPage(
            hint = "Text-Fokus",
            title = "Texte stabil erfassen und verstehen",
            description = "Schilder, Dokumente und Hinweise koennen lokal oder ueber Cloud-OCR erkannt und anschliessend uebersetzt werden."
        ),
        OnboardingPage(
            hint = "Audio & Feedback",
            title = "Ergebnisse hoeren und spueren",
            description = "Die Android-App kombiniert Uebersetzung, Vorlesen und haptisches Feedback zu einer echten Assistenz-Erfahrung."
        ),
        OnboardingPage(
            hint = "AR & Companion",
            title = "Dieselbe Produktidee auf jeder Plattform",
            description = "iOS soll denselben SmartVision-Flow erhalten. Desktop bleibt als Companion fuer Spiegelung, Verlauf und spaetere Erweiterungen gedacht."
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF071B22), Color.Black)))
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
                val page = pages[pageIndex]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF7DEBFF).copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = page.hint,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            color = Color(0xFF7DEBFF),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    ) {
                        Text(
                            text = page.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = page.description,
                            modifier = Modifier.padding(top = 16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.76f)
                        )
                    }
                }
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
                                .size(if (isSelected) 10.dp else 7.dp)
                                .background(
                                    color = if (isSelected) Color(0xFF7DEBFF) else Color.White.copy(alpha = 0.3f),
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
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DEBFF))
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.lastIndex) "Starten" else "Weiter",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        if (pagerState.currentPage < pages.lastIndex) {
            TextButton(
                onClick = onFinished,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .systemBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Ueberspringen",
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}
