package com.example.yangdnashabschlussprojekt.di

import android.annotation.SuppressLint
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.HistoryViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.WelcomeViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.shared.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@Suppress("DEPRECATION")
@SuppressLint("RestrictedApi")
val viewModelModule = module {
    viewModel { WelcomeViewModel(get(), get()) }
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ARViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::TextViewModel)
}