package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.data.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.data.repository.InMemoryLocalHistoryStore
import com.example.yangdnashabschlussprojekt.data.repository.LocalHistoryStore
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionConfig
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionRepository
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionTransport
import com.example.yangdnashabschlussprojekt.feature.repository.HistoryGateway
import com.example.yangdnashabschlussprojekt.feature.repository.OnboardingGateway
import com.example.yangdnashabschlussprojekt.feature.repository.CaptureGateway
import com.example.yangdnashabschlussprojekt.feature.repository.LocalCaptureGateway
import com.example.yangdnashabschlussprojekt.feature.repository.LocalHistoryGateway
import com.example.yangdnashabschlussprojekt.feature.repository.RepositoryCaptureGateway
import com.example.yangdnashabschlussprojekt.feature.repository.RepositoryHistoryGateway
import com.example.yangdnashabschlussprojekt.feature.repository.RepositorySessionGateway
import com.example.yangdnashabschlussprojekt.feature.repository.SessionGateway
import com.example.yangdnashabschlussprojekt.feature.repository.SettingsOnboardingGateway
import com.example.yangdnashabschlussprojekt.feature.repository.UnavailableSessionGateway
import com.example.yangdnashabschlussprojekt.ui.camera.IOSCameraManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import com.russhwolf.settings.Settings
import org.koin.dsl.module

val iosLocalModule = module {
    single<Settings> { Settings() }
    single<LocalHistoryStore> { InMemoryLocalHistoryStore() }
    single<CloudVisionConfig> { IOSCloudVisionConfig() }
    single<CloudVisionTransport> { IOSCloudVisionTransport() }
    single { CloudVisionRepository(config = get(), transport = get()) }
    single<SessionGateway> {
        UnavailableSessionGateway(
            unavailableMessage = "Firebase ist auf iOS noch nicht konfiguriert. Fuege GoogleService-Info.plist hinzu, damit Login und Cloud-Verlauf live verfuegbar sind."
        )
    }
    single<OnboardingGateway> { SettingsOnboardingGateway(get()) }
    single<HistoryGateway> { LocalHistoryGateway(get()) }
    single<CaptureGateway> { LocalCaptureGateway(get()) }
    single<CameraManager> { IOSCameraManager() }
}

val iosFirebaseModule = module {
    single<Settings> { Settings() }
    single<LocalHistoryStore> { InMemoryLocalHistoryStore() }
    single<CloudVisionConfig> { IOSCloudVisionConfig() }
    single<CloudVisionTransport> { IOSCloudVisionTransport() }
    single { CloudVisionRepository(config = get(), transport = get()) }
    single { UserRepository() }
    single { HistoryRepository(localHistoryStore = get(), userRepository = get()) }
    single<SessionGateway> { RepositorySessionGateway(get()) }
    single<OnboardingGateway> { SettingsOnboardingGateway(get()) }
    single<HistoryGateway> { RepositoryHistoryGateway(get()) }
    single<CaptureGateway> { RepositoryCaptureGateway(get()) }
    single<CameraManager> { IOSCameraManager() }
}
