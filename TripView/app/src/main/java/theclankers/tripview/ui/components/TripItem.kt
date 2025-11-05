package theclankers.tripview.ui.components

// Takes as input a single trip object, containing the trip name and description

// Trip Name
// Trip description


// Clicking this component ALWAYS opens up the trip screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.R.attr.description
import android.R.attr.name
import android.R.attr.onClick
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import theclankers.tripview.ui.theme.Purple4
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useTrip

/**
 * Displays a single trip item with a name and description.
 * Clicking this component always opens the trip screen.
 */
@Composable
fun TripItem(navController: NavController, tripId: Int
    //for homepage
    //tripName: String,
    //tripDescription: String,
    //onClick: () -> Unit
) {
    val appVM = useAppContext()
    val token = appVM.accessTokenState.value

    if (token == null) return

    val tripVM = useTrip(token, tripId)
    val name = tripVM.nameState.value
    val description = tripVM.descriptionState.value

    if (name == null || description == null) return

    Log.d("TripItem", "Rendering TripItem for trip: $tripId")

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Purple4),
        modifier = Modifier
            .fillMaxWidth()
            .padding( vertical = 8.dp)
            .clickable { navController.navigate("trip/${tripId}") }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            HelperText2(
                text = name,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            HelperText(
                text = description
            )
        }
    }
}

/**
 * Displays a single trip item that expands within itself when clicked.
 *
 * @param tripName The title of the trip.
 * @param tripDescription A short description or detail about the trip.
 * @param expandedContent Optional expanded section (e.g., stops, details).
 */
// @Composable
// fun TripItem2(
//     //for picking an itineary
//     tripName: String,
//     tripDescription: String,
//     expandedContent: @Composable (() -> Unit)? = null
// ) {
//     var expanded by remember { mutableStateOf(false) }
// 
//     Card(
//         shape = RoundedCornerShape(16.dp),
//         elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//         colors = CardDefaults.cardColors(containerColor = Purple4),
//         modifier = Modifier
//             .fillMaxWidth()
//             .padding(vertical = 8.dp)
//             .clickable { expanded = !expanded }
//     ) {
//         Column(
//             modifier = Modifier
//                 .fillMaxWidth()
//                 .padding(16.dp)
//         ) {
//             // Header Row (Name + Dropdown Icon)
//             Row(
//                 modifier = Modifier.fillMaxWidth(),
//                 verticalAlignment = Alignment.CenterVertically
//             ) {
//                 HelperText2(
//                     text = tripName,
//                     modifier = Modifier.weight(1f)
//                 )
// 
//                 Icon(
//                     imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//                     contentDescription = if (expanded) "Collapse" else "Expand"
//                 )
//             }
// 
//             Spacer(modifier = Modifier.height(4.dp))
// 
//             HelperText(text = tripDescription)
// 
//             // Expanded Section
//             AnimatedVisibility(
//                 visible = expanded,
//                 enter = expandVertically(),
//                 exit = shrinkVertically()
//             ) {
//                 expandedContent?.let {
//                     Column(modifier = Modifier.padding(top = 12.dp)) {
//                         it()
//                     }
//                 }
//             }
//         }
//     }
// }
