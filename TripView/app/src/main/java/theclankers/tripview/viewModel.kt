import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TripViewViewModel : ViewModel() {
    var showNavbar by mutableStateOf(true)
        private set

    fun toggleNavbar() {
        showNavbar = !showNavbar
    }
}