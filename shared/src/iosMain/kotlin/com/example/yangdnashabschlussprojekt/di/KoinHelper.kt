package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.di.sharedModul.commonModule
import org.koin.core.context.startKoin
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("startKoin")
fun initKoin() {
    startKoin {
        modules(commonModule, iosModule)
    }
}