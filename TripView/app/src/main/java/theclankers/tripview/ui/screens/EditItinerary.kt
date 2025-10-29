package theclankers.tripview.ui.screens

import android.R.attr.onClick
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import theclankers.tripview.classes.Stop
import theclankers.tripview.ui.components.StopItem

@Composable
fun EditItinerary(navController: NavHostController) {
    val stops = remember {
        mutableStateListOf(
            Stop(1, 37.8199, -122.4783, "Morning walk across the bridge", completed = true),
            Stop(2, 37.8080, -122.4177, "Seafood lunch by the bay", completed = false),
            Stop(3, 37.8267, -122.4230, "Afternoon tour of the historic prison", completed = false),
            Stop(4, 37.7544, -122.4477, "Sunset view over San Francisco", completed = true),
        )
    }
    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }

    val itemHeightDp = 80.dp
    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeightDp.toPx() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(stops, key = { _, stop -> stop.id }) { index, stop ->
            val isDragging = index == draggingIndex

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeightDp)
                    .then(
                        if (isDragging) Modifier.offset { IntOffset(0, dragOffset.toInt()) } else Modifier
                    )
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { draggingIndex = index },
                            onDrag = { change, dragAmount ->
                                dragOffset += dragAmount.y
                                change.consume()

                                val targetIndex = ((dragOffset + index * itemHeightPx) / itemHeightPx)
                                    .toInt()
                                    .coerceIn(0, stops.size - 1)

                                if (targetIndex != index && targetIndex in stops.indices) {
                                    stops.removeAt(index)
                                    stops.add(targetIndex, stop)
                                    draggingIndex = targetIndex
                                    dragOffset = 0f
                                }
                            },
                            onDragEnd = {
                                draggingIndex = null
                                dragOffset = 0f
                            },
                            onDragCancel = {
                                draggingIndex = null
                                dragOffset = 0f
                            }
                        )
                    },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stop.name)
                }
            }
        }
    }
}