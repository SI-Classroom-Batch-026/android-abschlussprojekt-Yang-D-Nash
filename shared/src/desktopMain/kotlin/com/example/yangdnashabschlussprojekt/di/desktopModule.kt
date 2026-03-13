package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.companion.DesktopCompanionServer
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionConfig
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionRepository
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionTransport
import com.example.yangdnashabschlussprojekt.data.repository.InMemoryLocalHistoryStore
import com.example.yangdnashabschlussprojekt.data.repository.LocalHistoryStore
import com.example.yangdnashabschlussprojekt.feature.repository.CaptureGateway
import com.example.yangdnashabschlussprojekt.feature.repository.HistoryGateway
import com.example.yangdnashabschlussprojekt.feature.repository.LocalCaptureGateway
import com.example.yangdnashabschlussprojekt.feature.repository.LocalHistoryGateway
import com.example.yangdnashabschlussprojekt.feature.repository.OnboardingGateway
import com.example.yangdnashabschlussprojekt.feature.repository.SessionGateway
import com.example.yangdnashabschlussprojekt.feature.repository.SettingsOnboardingGateway
import com.example.yangdnashabschlussprojekt.feature.repository.UnavailableSessionGateway
import com.example.yangdnashabschlussprojekt.ui.camera.DesktopCameraManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import com.russhwolf.settings.Settings
import org.koin.dsl.module

val desktopModule = module {
    single<Settings> { Settings() }
    single<LocalHistoryStore> { InMemoryLocalHistoryStore() }
    single<CloudVisionConfig> { DesktopCloudVisionConfig() }
    single<CloudVisionTransport> { DesktopCloudVisionTransport() }
    single { CloudVisionRepository(config = get(), transport = get()) }
    single { DesktopCompanionServer().apply { ensureStarted() } }
    single<SessionGateway> {
        UnavailableSessionGateway(
            unavailableMessage = "Desktop Companion meldet sich ueber ein verbundenes Handy an. Ein eigener Desktop-Login ist im Live-Flow noch nicht vorgesehen."
        )
    }
    single<OnboardingGateway> { SettingsOnboardingGateway(get()) }
    single<HistoryGateway> { LocalHistoryGateway(get()) }
    single<CaptureGateway> { LocalCaptureGateway(get()) }
    single<CameraManager> { DesktopCameraManager() }
}
