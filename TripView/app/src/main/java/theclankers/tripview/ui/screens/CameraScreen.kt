package theclankers.tripview.ui.screens


import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import theclankers.tripview.R
import theclankers.tripview.ui.components.HeaderText1
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun CameraPreviewContent(
    navController: NavHostController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

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

        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp) // roughly height of header+text
        )
        Box(modifier= Modifier
            .align(alignment =BottomCenter)
            .size(80.dp)){
            CameraCaptureButton(
                imageCapture = imageCapture,
                navController = navController
            )
        }

    }
}

@Composable
fun CameraScreen(
    navController: NavHostController
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        CameraPreviewContent(navController = navController)
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Camera permission required to use this feature.")
        }
    }
}



@Composable
fun CameraCaptureButton(
    imageCapture: ImageCapture?,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Button(
        onClick = {
            val capture = imageCapture ?: run {
                Log.e("CameraPreview", "imageCapture is null - not bound")
                return@Button
            }

            val photoFile = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            capture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        //for debugging:
                        Log.e("CameraPreview", "she worked")
                        val encodedPath = URLEncoder.encode(photoFile.absolutePath, StandardCharsets.UTF_8.toString())
                        navController.navigate("cameraConfirmScreen/${encodedPath}")
                        /*val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                        if (bitmap != null) {
                            onPhotoCaptured(bitmap)
                        } else {
                            Log.e("CameraPreview", "failed to decode saved image")
                        }*/
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


