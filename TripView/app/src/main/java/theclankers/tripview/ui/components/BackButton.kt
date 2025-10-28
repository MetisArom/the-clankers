package theclankers.tripview.ui.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import theclankers.tripview.ui.navigation.goBack
import theclankers.tripview.ui.theme.PurpleGrey40

@Composable
fun BackButton(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { goBack(navController) },
        colors=ButtonDefaults.buttonColors(containerColor = PurpleGrey40)
    // can get in a funky state with the edit profile screen, may need more logic here
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White // or your theme color
        )
    }
}