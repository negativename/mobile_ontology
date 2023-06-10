import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.App
import app.AppViewModel


fun main() = application {
    val viewModel = AppViewModel()
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme{
            App(viewModel)
        }
    }

}
