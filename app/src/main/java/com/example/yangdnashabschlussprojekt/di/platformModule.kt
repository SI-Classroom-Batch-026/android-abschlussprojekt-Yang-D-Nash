package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.feature.repository.RepositoryHistoryGateway
import com.example.yangdnashabschlussprojekt.feature.repository.RepositorySessionGateway
import com.example.yangdnashabschlussprojekt.feature.repository.RepositoryCaptureGateway
import com.example.yangdnashabschlussprojekt.feature.repository.CaptureGateway
import com.example.yangdnashabschlussprojekt.feature.repository.HistoryGateway
import com.example.yangdnashabschlussprojekt.feature.repository.OnboardingGateway
import com.example.yangdnashabschlussprojekt.feature.repository.SessionGateway
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionConfig
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionRepository
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionTransport
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.Executor
import java.util.concurrent.Executors

val platformModule = module {
    single<Executor> { Executors.newSingleThreadExecutor() }
    single { CameraXManager(context = androidContext(), executor = get()) }
    single<CameraManager> { get<CameraXManager>() }
    single<CloudVisionConfig> { AndroidCloudVisionConfig() }
    single<CloudVisionTransport> { AndroidCloudVisionTransport() }
    single { CloudVisionRepository(config = get(), transport = get()) }
    single<SessionGateway> { RepositorySessionGateway(get()) }
    single<OnboardingGateway> { AndroidOnboardingGateway(get()) }
    single<HistoryGateway> { RepositoryHistoryGateway(get()) }
    single<CaptureGateway> { RepositoryCaptureGateway(get()) }
}
