package theclankers.tripview.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import theclankers.tripview.ui.components.HeaderText1
import theclankers.tripview.ui.navigation.navigateTo

@Composable
fun CameraConfirmScreen(photoPath: String?, navController: NavHostController) {
    if (photoPath == null) {
        Text("No photo found")
        return
    }

    val bitmap = remember(photoPath) {
        BitmapFactory.decodeFile(photoPath)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            HeaderText1("Camera")
            Button(
                onClick = { navigateTo(navController, "camera3") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D)))

            { Text("Take New Image")
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )}
        }


        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Captured photo",
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("This is your captured photo")
    }
}
