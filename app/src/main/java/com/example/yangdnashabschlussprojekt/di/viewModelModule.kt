package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.AndroidWelcomeViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.HistoryViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.shared.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::AndroidWelcomeViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ARViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::TextViewModel)
}