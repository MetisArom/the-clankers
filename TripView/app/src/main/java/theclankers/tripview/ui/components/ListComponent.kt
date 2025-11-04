package theclankers.tripview.ui.components
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

// Abstract container component that renders a list of items given a list of item IDs and a render function
@Composable
fun <T> ListComponent(
    itemIds: List<T>,
    renderItem: @Composable (T) -> Unit
) {
    Column {
        for (itemId in itemIds) {
            renderItem(itemId)
        }
    }
}