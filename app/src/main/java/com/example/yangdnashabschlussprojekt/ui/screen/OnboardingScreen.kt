package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.R
import com.example.yangdnashabschlussprojekt.data.local.onBoarding.OnboardingData
import com.example.yangdnashabschlussprojekt.ui.component.onBoarding.OnboardingContent
import com.example.yangdnashabschlussprojekt.ui.theme.DeepSpaceBlack
import com.example.yangdnashabschlussprojekt.ui.theme.DeepSpaceCyan
import com.example.yangdnashabschlussprojekt.ui.theme.LightBgEnd
import com.example.yangdnashabschlussprojekt.ui.theme.LightBgStart
import kotlinx.coroutines.launch
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pages = listOf(
        OnboardingData(
            title = stringResource(R.string.onboarding_live_title),
            desc = stringResource(R.string.onboarding_live_desc),
            hint = "Live-Modus",
            icon = Icons.Default.AutoAwesome
        ),
        OnboardingData(
            title = stringResource(R.string.onboarding_focus_title),
            desc = stringResource(R.string.onboarding_focus_desc),
            hint = "Nicht wackeln!",
            icon = Icons.AutoMirrored.Filled.TextSnippet
        ),
        OnboardingData(
            title = stringResource(R.string.onboarding_scan_title),
            desc = stringResource(R.string.onboarding_scan_desc),
            hint = "Scan & Translate",
            icon = Icons.Default.CameraAlt
        ),
        OnboardingData(
            title = stringResource(R.string.onboarding_cloud_title),
            desc = stringResource(R.string.onboarding_cloud_desc),
            hint = "Online",
            icon = Icons.Default.CloudUpload
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isDark = isSystemInDarkTheme()

    val bgGradient = Brush.verticalGradient(
        if (isDark) listOf(DeepSpaceCyan, DeepSpaceBlack)
        else listOf(LightBgStart, LightBgEnd)
    )

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { index ->
                OnboardingContent(pages[index], index)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
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
                        text = if (pagerState.currentPage == pages.size - 1)
                            stringResource(R.string.btn_start)
                        else
                            stringResource(R.string.btn_next),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = pagerState.currentPage < pages.size - 1,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(12.dp)
        ) {
            TextButton(onClick = onFinished) {
                Text(
                    text = stringResource(R.string.btn_skip),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}