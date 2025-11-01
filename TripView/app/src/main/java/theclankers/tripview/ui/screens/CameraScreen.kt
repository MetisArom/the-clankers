package theclankers.tripview.ui.screens

import android.R.attr.bottom
import android.R.attr.text
import android.R.id.bold
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import theclankers.tripview.R
import theclankers.tripview.ui.components.HeaderText1
import java.io.File


@Composable
fun Camera3Screen(
    onPhotoCaptured: (Bitmap) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Remember these across recompositions
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    // This is where LaunchedEffect goes 👇
    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = androidx.camera.core.Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            Log.d("CameraPreview", "Camera bound successfully")
        } catch (exc: Exception) {
            Log.e("CameraPreview", "Failed to bind camera", exc)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
        ) {
            HeaderText1("Camera")
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Take a picture of a landmark to get more information.",
                fontWeight = FontWeight.Bold
            )
        }

        // --- Camera preview fills space below the text ---
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp) // roughly height of header+text
        )

        // --- Button floats ABOVE the preview, at bottom center ---
        Box(modifier= Modifier
            .align(alignment =BottomCenter)
            .size(80.dp)){
            CameraCaptureButton(
                imageCapture = imageCapture,
                onPhotoCaptured = onPhotoCaptured
            )
        }

    }




}


@Composable
fun CameraCaptureButton(
    imageCapture: ImageCapture?,
    onPhotoCaptured: (Bitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Button(
        onClick = {
            val capture = imageCapture ?: run {
                Log.e("CameraPreview", "imageCapture is null - not bound")
                return@Button
            }

            // create ONE file and reuse the same reference everywhere
            val photoFile = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            capture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        //for debugging
                        //Log.e("CameraPreview", "she worked")

                        // decode the saved file
                        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                        if (bitmap != null) {
                            onPhotoCaptured(bitmap)
                        } else {
                            Log.e("CameraPreview", "failed to decode saved image")
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraPreview", "Capture failed", exception)
                    }
                }
            )
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D))
    ) {
        Icon(
            painter = painterResource(R.drawable.camera_icon),
            contentDescription = "camera"
        )
    }
}


