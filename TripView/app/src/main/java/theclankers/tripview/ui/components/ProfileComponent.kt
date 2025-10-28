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
import theclankers.tripview.ui.navigation.navigateTo

// Input: user_id

// Displays the generic avatar, first name, last name, username stylishly

// for friends list
@Composable
fun ProfileComponent( user_id: Int, modifier: Modifier = Modifier){
    Row(Modifier.padding(20.dp)) {
        SimpleImage(R.drawable.profile_picture, modifier= Modifier.size(150.dp))
        Column(Modifier.padding(15.dp) ){
            // change to input based on the user ID from the backend
            HeaderText("First Name Last Name")
            Text( text="Username",  fontSize=20.sp)
        }
    }
}

//for the profile page
@Composable
fun ProfilePageComponent( user_id: Int, navController: NavController, modifier: Modifier = Modifier){
    Row(Modifier.padding(20.dp)) {
        SimpleImage(R.drawable.profile_picture, modifier= Modifier.size(150.dp))
        Column(Modifier.padding(15.dp) ){
            // change to input based on the user ID from the backend
            HeaderText("First Name Last Name")
            Text( text="Username",  fontSize=20.sp, modifier= Modifier.padding(bottom=10.dp))

            //Edit profile button
            Button(
                onClick = { navigateTo(navController, "editProfile") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D)))
            { Text("Edit Profile")  }
        }
    }
}