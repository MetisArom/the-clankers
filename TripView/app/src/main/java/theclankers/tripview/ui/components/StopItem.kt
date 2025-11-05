package theclankers.tripview.ui.components

import android.R.attr.checked
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import theclankers.tripview.data.api.ApiClient
import theclankers.tripview.data.models.Stop
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import theclankers.tripview.ui.viewmodels.StopViewModel
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useStop

// For reference, look at the Trip 1 Screen, these are each of the stops


// Takes as input the stop object (https://lucid.app/lucidchart/4b3e1b22-e5ef-49e9-b20b-d5a6e5e591e9/edit?page=0_0&invitationId=inv_bb1cdee1-aef2-44e0-806a-2a3ccf4c65bf#)

// Uses the checkbox component to indicate whether or not this stop was marked completed
// Displays icon, stop name, and duration in hours


// Clicking this stop opens up the Stop in its own screen
@Composable
fun StopItem(navController: NavController, stopId: Int) {
    val appVM = useAppContext()
    val token = appVM.accessTokenState.value

    if (token == null) return

    val stopVM = useStop(token, stopId)
    val completed = stopVM.completedState.value
    val name = stopVM.nameState.value

    if (completed == null || name == null) return
    //stop: Stop,
    //onStopClick: (Stop) -> Unit,
    //onCompletedChange: (Stop, Boolean) -> Unit,
    //modifier: Modifier = Modifier,
    //onDeleteStop: suspend (Stop) -> Unit? = {},
    //editMode: Boolean = false,
    //trailingContent: @Composable (() -> Unit)? = null
    //val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                navController.navigate("stop/${stopId}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (completed) // editMode
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            //if (editMode) {
            //    Row(
            //        verticalAlignment = Alignment.CenterVertically,
            //        horizontalArrangement = Arrangement.spacedBy(4.dp)
            //    ) {
            //        IconButton(onClick = {
            //            scope.launch {
            //                onDeleteStop(stop)
            //            }
            //        }) {
            //            Icon(
            //                imageVector = Icons.Filled.Delete,
            //                contentDescription = "Delete stop"
            //            )
            //        }

            //        trailingContent?.invoke()
            //    }
            //} else {}
            Checkbox(
                checked = completed,
                onCheckedChange = { checked ->
                    stopVM.toggleCompleted(stopId, checked)
                }
            )
        }
    }
}
