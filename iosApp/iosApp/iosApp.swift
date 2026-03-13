import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        _ = IOSMLKitRuntimeHost.shared
        if Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") != nil {
            KoinHelperKt.startKoinWithFirebase()
        } else {
            KoinHelperKt.startKoin()
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
