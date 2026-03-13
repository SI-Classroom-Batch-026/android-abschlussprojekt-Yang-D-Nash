package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.di.sharedModul.commonModule
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import org.koin.core.context.startKoin
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("startKoin")
fun initKoin() {
    startKoin {
        modules(commonModule, iosLocalModule)
    }
}

@OptIn(ExperimentalObjCName::class)
@ObjCName("startKoinWithFirebase")
fun initKoinWithFirebase() {
    Firebase.initialize()
    startKoin {
        modules(commonModule, iosFirebaseModule)
    }
}
