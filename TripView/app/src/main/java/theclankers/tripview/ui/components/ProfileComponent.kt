package theclankers.tripview.ui.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theclankers.tripview.R

// Input: user_id

// Displays the generic avatar, first name, last name, username stylishly

@Composable
fun ProfileComponent( user_id: Int){
    Row {
        SimpleImage(R.drawable.profile_picture, modifier= Modifier.size(150.dp))
        Column(){
            // change to input based on the user ID from the backend
            HeaderText("First Name Last Name")
            Text( text="Username",  fontSize=20.sp)
        }
    }
}