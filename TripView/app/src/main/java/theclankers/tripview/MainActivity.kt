package theclankers.tripview

import TripViewViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import theclankers.tripview.ui.components.SampleButton
import theclankers.tripview.ui.navigation.TripViewNavGraph
import theclankers.tripview.ui.navigation.TripViewNavigationBar
import theclankers.tripview.ui.theme.TripViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripViewTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen()
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val activityVM: TripViewViewModel = viewModel(LocalActivity.current as ComponentActivity)

    Scaffold(
        bottomBar = {
            if (activityVM.showNavbar) {
                TripViewNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        // Apply padding to the NavHost
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
            // DON'T MODIFY THIS!! If you're looking to add UI elements to the screen,
            // go edit the screen definition in ui/screens!
            // Basically, the way this works is that the NavGraph handles rendering
            // the composable for each screen. Then we do all UI work in that composable.
            // To add a new screen, go to TripViewNavGraph in ui/navigation/NavDefinitions.kt
            TripViewNavGraph(navController)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TripViewTheme {
        Greeting("Android")
    }
}