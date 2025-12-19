package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pages = listOf(
        OnboardingData("Live-Erkennung", "Objekte werden automatisch in deiner Handysprache erkannt, während du dich bewegst.", "Live-Modus"),
        OnboardingData("Texte scannen", "Halte die Kamera ca. 1 Sekunde absolut ruhig auf den Text für optimale Ergebnisse.", "Fokus halten"),
        OnboardingData("Cloud-Power", "Nutze die Cloud, um erkannte Texte präzise übersetzen zu lassen.", "Online-Übersetzung")
    )
    
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
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
                Text(data.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Text(data.desc, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(24.dp))
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(16.dp)) {
                    Text(data.hint, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
        }

        // Steuerung unten
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            // Punkte
            Row {
                repeat(pages.size) { i ->
                    val color = if (pagerState.currentPage == i) MaterialTheme.colorScheme.primary else Color.LightGray
                    Box(modifier = Modifier.padding(4.dp).size(10.dp).background(color, CircleShape))
                }
            }
            
            Button(onClick = {
                if (pagerState.currentPage < pages.size - 1) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    onFinished()
                }
            }) {
                Text(if (pagerState.currentPage == pages.size - 1) "Verstanden" else "Weiter")
            }
        }
    }
}

data class OnboardingData(val title: String, val desc: String, val hint: String)