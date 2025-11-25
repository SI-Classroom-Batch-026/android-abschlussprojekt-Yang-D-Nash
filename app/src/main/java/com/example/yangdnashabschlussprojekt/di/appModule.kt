package com.example.yangdnashabschlussprojekt.di

import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module

val appModule = module {

    single { FirebaseAuth.getInstance() }

}
