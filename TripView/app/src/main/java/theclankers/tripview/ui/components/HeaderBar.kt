package theclankers.tripview.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// This component needs to somehow be fully integrated with the Navigation stack
// It needs to always show the name of the screen at the top
// It also will conditionally show a back arrow depending on
// 1: if there is a screen to go back to
// 2: if you are allowed to go back from the current screen

@Composable
fun Header(
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {}
) {
    TopAppBar(
        title = {"_" },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            } else null
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFF7F6F8)
        ),
    )
}