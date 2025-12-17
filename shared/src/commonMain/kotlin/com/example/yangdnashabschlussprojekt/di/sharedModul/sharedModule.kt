package com.example.yangdnashabschlussprojekt.di.sharedModul

import com.example.yangdnashabschlussprojekt.data.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.domain.usecase.GetHistoryUseCase
import com.example.yangdnashabschlussprojekt.domain.usecase.ManageHistoryUseCase
import org.koin.core.context.startKoin
import org.koin.dsl.module

val sharedModule = module {
    single { HistoryRepository(get()) }
    factory { GetHistoryUseCase(get()) }
    factory { ManageHistoryUseCase(get()) }
}

fun initKoin() {
    startKoin {
        modules(sharedModule)
    }
}