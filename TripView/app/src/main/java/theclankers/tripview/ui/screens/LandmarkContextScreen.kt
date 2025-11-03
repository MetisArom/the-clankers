package theclankers.tripview.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
fun LandmarkContextScreen (photoPath: String?,navController: NavHostController){
    if (photoPath == null) {
        Text("No photo found")
        return
    }

    val bitmap = remember(photoPath) {
        loadRotatedBitmap(photoPath)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row() {
            HeaderText1("Camera")
            Button(
                onClick = { navigateTo(navController, "camera") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D)),
                modifier= Modifier.padding(start=20.dp))


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
                modifier = Modifier
                    .width(400.dp)
                    .height(400.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("This is where the context of the landmark goes")

    }
}