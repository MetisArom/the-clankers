package theclankers.tripview

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import theclankers.tripview.ui.theme.TripViewTheme

import theclankers.tripview.ui.screens.AppScaffold
import theclankers.tripview.ui.screens.ChatScreen


class ChattViewModel(app: Application): AndroidViewModel(app) {
    val model = app.getString(R.string.model)
    val username = app.getString(R.string.model)
    val instruction = app.getString(R.string.instruction)

    val message = TextFieldState(app.getString(R.string.message))
    val errMsg = mutableStateOf("")
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TripViewTheme {
               AppScaffold()
            }
        }
    }
}

