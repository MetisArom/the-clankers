package theclankers.tripview.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import theclankers.tripview.core.Constants.PASSWORD
import theclankers.tripview.core.Constants.USERNAME
import theclankers.tripview.ui.components.FormInput
import theclankers.tripview.ui.navigation.navigateToDetail
import theclankers.tripview.ui.navigation.navigateToRoot
import theclankers.tripview.ui.viewmodels.AppViewModel
import theclankers.tripview.ui.viewmodels.useAppContext

@Composable
fun LoginScreen(navController: NavHostController) {
    // TODO: Implement LoginScreen
    // appVM & LaunchedEffect, hide the navbar
    // username/email and password state (the username field should also accept email address)
    // login button
    // switch to sign up button
    // ApiService.login(username/email, password)

    val appVM: AppViewModel = useAppContext()

    val username = mutableStateOf("")
    val password = mutableStateOf("")

    Scaffold(
        containerColor = Color(0xFFF7F6F8)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Log In",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(24.dp))

                FormInput(
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = "Username",
                    placeholder = "",
                    imeAction = ImeAction.Next,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FormInput(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = "Password",
                    placeholder = "",
                    imeAction = ImeAction.Next,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (appVM.authErrorMessageState.value != null) {
                    Text("Failed to login. Please check your credentials and try again.", color=Color.Red)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        //redirect to demo camera page
                        navigateToRoot(navController, "camera")
                        appVM.demoFlowState.value = true
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    enabled = !appVM.isAuthingState.value
                ) {
                    Text("Continue as Demo")
                }

                Button(
                    onClick = {
                        //log in
                        appVM.login(username.value, password.value)
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF56308D)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    enabled = !appVM.isAuthingState.value
                ) {
                    if (appVM.isAuthingState.value) {
                        Text("Logging In...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Log In",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}