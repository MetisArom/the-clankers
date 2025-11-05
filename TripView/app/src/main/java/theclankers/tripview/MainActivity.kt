package theclankers.tripview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import theclankers.tripview.ui.theme.TripViewTheme
import theclankers.tripview.ui.viewmodels.AppViewModel
import theclankers.tripview.core.Constants.USERNAME
import theclankers.tripview.core.Constants.PASSWORD
import theclankers.tripview.ui.screens.AppScaffold

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripViewTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val activityVM: AppViewModel = viewModel(LocalActivity.current as ComponentActivity)
                    activityVM.login(USERNAME, PASSWORD)
                    AppScaffold(innerPadding = innerPadding)
                }
            }
        }
    }
}
