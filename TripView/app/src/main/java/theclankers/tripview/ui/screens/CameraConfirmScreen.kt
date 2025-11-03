package theclankers.tripview.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.material.icons.filled.Search

fun loadRotatedBitmap(photoPath: String): Bitmap? {
    val bitmap = BitmapFactory.decodeFile(photoPath) ?: return null
    val exif = ExifInterface(photoPath)

    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
    }

    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

@Composable
fun CameraConfirmScreen(photoPath: String?, navController: NavHostController) {
    if (photoPath == null) {
        Text("No photo found")
        return
    }
    val encodedPath = Uri.encode(photoPath)

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
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navigateTo(navController, "landmarkContext/${encodedPath}") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D)),
            modifier= Modifier
                .padding(start=20.dp)
                .align(Alignment.CenterHorizontally))


        { Text("Identify Image")
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Back"
            )}
    }
}
