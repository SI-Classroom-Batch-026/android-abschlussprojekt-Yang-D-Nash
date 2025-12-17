// shared/src/commonMain/kotlin/com/example/project/di/Koin.kt

package com.example.yangdnashabschlussprojekt.di.sharedModul // Achte auf das Paket!

import org.koin.dsl.module

// Es MUSS 'val' sein und darf NICHT 'internal' oder 'private' sein, 
// wenn der Helper in einem anderen Paket liegt.
val commonModule = module {
    // Deine Definitionen (ViewModel, Repositories, etc.)
}