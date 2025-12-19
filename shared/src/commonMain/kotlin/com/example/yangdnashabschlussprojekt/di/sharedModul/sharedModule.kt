package com.example.yangdnashabschlussprojekt.di.sharedModul

import com.example.yangdnashabschlussprojekt.data.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import com.example.yangdnashabschlussprojekt.domain.usecase.GetHistoryUseCase
import com.example.yangdnashabschlussprojekt.domain.usecase.ManageHistoryUseCase
import org.koin.dsl.module

val sharedModule = module {
    single { UserRepository() }
    single { HistoryRepository(get()) }
    factory { GetHistoryUseCase(get()) }
    factory { ManageHistoryUseCase(get()) }
}