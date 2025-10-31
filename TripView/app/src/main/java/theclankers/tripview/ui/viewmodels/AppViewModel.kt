package theclankers.tripview.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// This file stores the global context that will be used throughout the entire app.
// It's not implemented yet...
class AppViewModel : ViewModel() {
    var showNavbar by mutableStateOf(true)
        private set

    fun toggleNavbar() {
        showNavbar = !showNavbar
    }
}