package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.SettingsViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.WelcomeViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private const val VISION_BASE = "https://vision.googleapis.com/"
private const val API_KEY = "AIzaSyCeptnqf5FVyWnYkMzb4tRaXI8L8RY9ZcY"
val appModule = module {

    single { FirebaseAuth.getInstance() }

    single { UserRepository(get()) }

    viewModelOf(::WelcomeViewModel)

    viewModelOf(::SettingsViewModel)

    viewModelOf(::ARViewModel)

    viewModelOf(::TextViewModel)

}

