package theclankers.tripview.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import theclankers.tripview.data.models.Stop

@Composable
fun EditableStopItem(
    navController: NavHostController,
    stop: Stop,
    onDeleteStop: (Int) -> Unit,
    trailingContent: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        StopItem(
            navController = navController,
            stop = stop,
            trailingContent = trailingContent,
            onDeleteStop = onDeleteStop,
            editMode = true
        )
    }
}
