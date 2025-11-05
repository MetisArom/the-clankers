package theclankers.tripview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import theclankers.tripview.ui.theme.TripViewTheme

import theclankers.tripview.ui.screens.AppScaffold

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
