package theclankers.tripview.ui.screens

import android.R.attr.bottom
import android.R.attr.rating
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import theclankers.tripview.data.models.Stop
import theclankers.tripview.ui.navigation.goBack
import androidx.core.net.toUri
import theclankers.tripview.ui.components.HeaderText
import theclankers.tripview.ui.components.HelperText
import theclankers.tripview.ui.components.StarRating
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useStop
import theclankers.tripview.ui.viewmodels.useStopById

@Composable
fun StopScreen(navController: NavHostController, stopId: Int) {
    val context = LocalContext.current

    val appVM = useAppContext()
    val token = appVM.accessTokenState.value

    if (token == null) return

    val stopVM = useStopById(token, stopId)
    val latitude = stopVM.latitudeState.value
    val longitude = stopVM.longitudeState.value
    val name = stopVM.nameState.value
    val address = stopVM.addressState.value
    val hours = stopVM.hoursState.value
    val ratings = stopVM.ratingState.value
    val priceRange = stopVM.priceRangeState.value
    val googleMapsUri = stopVM.googleMapsUriState.value


    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        HeaderText(
            text = "$name",
            modifier = Modifier.padding(bottom = 4.dp)
        )
        if (!address.isNullOrBlank()) {
            HelperText(
                text = address,
            )
        } else {
            HelperText(
                text = "Address Unavailable",
            )
        }

        if (ratings !== null && ratings !== "") {
            var ratingNum: Double = 0.0
            var showStars = true
            try {
                ratingNum = ratings.toDouble()
            } catch(e: Exception) {
                showStars = false
            }
            if (showStars) {
                Row {
                    StarRating(rating = ratingNum)
                    HelperText(
                        text = ratings
                    )
                }
            } else {
                HelperText(
                    text = "Ratings Unavailable"
                )
            }
        } else {
            HelperText(
                text = "Ratings Unavailable"
            )
        }

        if (!priceRange.isNullOrBlank()) {
            HelperText(
                text = "Price: $priceRange",
            )
        } else {
            HelperText(
                text = "Price Range Unavailable"
            )
        }

        if (!hours.isNullOrBlank()) {
            HeaderText(
                text = "Hours:",
                modifier = Modifier.padding(top=8.dp, bottom = 4.dp)
            )
            HelperText(
                text = hours,
            )
        } else {
            HelperText(
                text = "Hours Unavailable",
            )
        }


        Row(
            modifier = Modifier.fillMaxHeight().fillMaxWidth().padding(bottom=16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End
        ) {
            // Button for opening Google Maps listing of stop to find more info
            if (!googleMapsUri.isNullOrBlank()) {
                Button(
                    modifier = Modifier
                        .padding(16.dp),
                    onClick = {
                        //Build the URI
                        val uri =
                            googleMapsUri.toUri()

                        //Create intent to open the Google Maps app explicitly if it exists
                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                            //Explicitly open Google Maps
                            setPackage("com.google.android.apps.maps")
                        }

                        // Try to start the activity
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        } else {
                            // Fallback: open in browser if Google Maps app is not installed
                            val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(browserIntent)
                        }
                    }) { Text("See in Google Maps") }
            }
            // Button for getting directions to stop with Google Maps
            Button(
                modifier = Modifier
                    .padding(16.dp),
                onClick = {
                    //Build the URI
                    val uri =
                        "https://www.google.com/maps/dir/?api=1&destination=${latitude},${longitude}&travelmode=driving".toUri()

                    //Create intent to open the Google Maps app explicitly if it exists
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        //Explicitly open Google Maps
                        setPackage("com.google.android.apps.maps")
                    }

                    // Try to start the activity
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        // Fallback: open in browser if Google Maps app is not installed
                        val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(browserIntent)
                    }
                }) { Text("Get Directions") }
        }
    }
}