package theclankers.tripview.ui.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import theclankers.tripview.R
import theclankers.tripview.ui.navigation.navigateToDetail
import theclankers.tripview.ui.viewmodels.AppViewModel
import theclankers.tripview.ui.viewmodels.useAppContext
import theclankers.tripview.ui.viewmodels.useUser

@Composable
fun ProfileComponent(navController: NavController, userId: Int){
    val appVM: AppViewModel = useAppContext()
    val loggedInUserId = appVM.userIdState.value
    val token = appVM.accessTokenState.value

    if (loggedInUserId == null || token == null) return

    val userViewModel = useUser(token, userId)
    val firstName = userViewModel.firstNameState.value
    val lastName = userViewModel.lastNameState.value
    val username = userViewModel.usernameState.value

    if (firstName == null || lastName == null || username == null) return

    Row(Modifier.padding(20.dp)) {
        SimpleImage(R.drawable.profile_picture, modifier= Modifier.size(150.dp))
        Column(Modifier.padding(15.dp) ){
            HeaderText("$firstName $lastName")
            Text( text= username,  fontSize=20.sp, modifier= Modifier.padding(bottom=10.dp))

            // Only show Edit Profile if this is the logged-in user's profile
            if (userId == loggedInUserId) {
                Button(
                    onClick = { navigateToDetail(navController, "editProfile") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D))
                ) {
                    Text("Edit Profile")
                }
            }
        }
    }
}