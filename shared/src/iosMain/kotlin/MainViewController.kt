import androidx.compose.ui.window.ComposeUIViewController
import com.example.yangdnashabschlussprojekt.di.sharedModul.initKoin
import com.example.yangdnashabschlussprojekt.shared.App

fun mainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}