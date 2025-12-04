package theclankers.tripview.ui.navigation

import ChatViewModel
import ChatViewModelFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import theclankers.tripview.ui.screens.*
import theclankers.tripview.ui.screens.auth.LoginScreen
import theclankers.tripview.ui.screens.auth.SignupScreen
import theclankers.tripview.ui.viewmodels.TripViewModel
import theclankers.tripview.ui.viewmodels.useAppContext

/**
 * Main Navigation Graph for the TripView app
 * All screens/routes should be declared here.
 * Any screen that needs a resource (Trip, Stop, User) will just receive its ID.
 */
@Composable
fun TripViewNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "trips") {

        // Camera screen (no arguments)
        composable("camera") { CameraScreen(navController) }

        // Friends list screen
        composable("friends") { FriendsScreen(navController) }

        // Search Friends Screen

        composable("searchFriends") { SearchFriendsScreen(navController)}

        // Profile screen
        composable("profile/{userId}", arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            if (userId != -1) ProfileScreen(navController, userId) else goBack(navController)
        }

        // Your profile screen
        composable("yourProfile"
        ) {
            val appVM = useAppContext()
            if (appVM.userIdState.value !== null) ProfileScreen(navController,
                appVM.userIdState.value!!
            ) else goBack(navController)
        }

        // Edit profile screen
        composable("editProfile") { EditProfileScreen(navController) }

        composable(
            route = "chat/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId")
            val appVM = useAppContext()
            val token = appVM.accessTokenState.value

            if (tripId == null || token == null) {
                LaunchedEffect(Unit) { goBack(navController) }
                return@composable
            }

            val vm: ChatViewModel = viewModel(
                factory = ChatViewModelFactory(tripId, token)
            )

            ChatScreen(vm, navController)
        }

        // BELOW HERE WAS NOT IN MAIN
        
        // Friend Profile Screen
        // composable("friendProfile") { FriendProfileScreen(navController) }
        
        // Navigation screen for a specific trip
        composable("navigation/{tripId}", arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            if (tripId != 0) NavigationScreen(navController, tripId) else goBack(navController)
        }

        // Main camera screen
        composable("camera") { CameraScreen(navController)}
        
        // Camera confirmation screem
        composable(
            "cameraConfirmScreen/{photoPath}",
            arguments = listOf(navArgument("photoPath") { type = NavType.StringType })
        ) { backStackEntry ->
            val photoPath = backStackEntry.arguments?.getString("photoPath")
            CameraConfirmScreen(photoPath,navController)
        }

        // Landmark Context Screen
        composable(
            "landmarkContext/{photoPath}",
            arguments = listOf(navArgument("photoPath") { type = NavType.StringType })
        ) { backStackEntry ->
            val photoPath = backStackEntry.arguments?.getString("photoPath")
            LandmarkContextScreen(photoPath,navController)
        }

        // unknown navigation screen
        // composable("navigation") { NavigationScreen(navController) }

        // Screen for a stop of some kind
//        composable(
//            "stops/{stop}",
//            arguments = listOf(navArgument("stop") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val stopJson = backStackEntry.arguments?.getString("stop")
//            if (stopJson != null) {
//                val stop = Json.decodeFromString<Stop>(stopJson)
//                StopScreen(navController, stop)
//            } else {
//                goBack(navController)
//            }
//        }
        composable("trips/{refreshKey}") { TripsScreen(navController) }
        composable("tripcreationform") {
            TripCreationForm(navController = navController)
        }
        composable(route="TripFormPt2") {
            TripFormPt2(navController = navController)
        }

        // ABOVE HERE WAS NOT IN MAIN


        // Trip screen for a specific trip
        composable(
            "trip/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            if (tripId != 0) TripScreen(navController, tripId) else goBack(navController)
        }

        // Add stop screen for a specific trip
        composable(
            "addStop/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            if (tripId != 0) AddStopScreen(navController, tripId) else goBack(navController)
        }

        // Invites screen for a specific trip
        composable(
            "invites/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            if (tripId != 0) InvitesScreen(navController, tripId) else goBack(navController)
        }

        // Invite friend screen for a specific trip
        composable(
            "inviteFriend/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            if (tripId != 0) InviteFriendScreen(navController, tripId) else goBack(navController)
        }

        // Stop screen for a specific stop
        composable(
            "stop/{stopId}",
            arguments = listOf(navArgument("stopId") { type = NavType.IntType })
        ) { backStackEntry ->
            val stopId = backStackEntry.arguments?.getInt("stopId") ?: 0
            if (stopId != 0) StopScreen(navController, stopId) else goBack(navController)
        }

        // Trips list screen
        composable("trips") { TripsScreen(navController) }

        // Trip creation form
        composable("tripcreationform") { TripCreationForm(navController) }

        // Debug screen
        composable("debug") { DebugScreen(navController) }
        // Sample trip screen
        composable("sampleTrip") { (navController) }

        // Edit itinerary screen
        composable(
            "editItinerary/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: 0
            if (tripId != 0) EditItinerary(navController, tripId) else goBack(navController)
        }

        composable(
            route = "ItineraryScreen/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId")

            if (tripId == null) {
                // tripId missing: safely go back
                LaunchedEffect(Unit) {
                    goBack(navController)
                }
            } else {
                val token = "user_jwt_token"
                ItineraryScreen(
                    navController = navController,
                    tripId = tripId,
                    token = token
                )
            }
        }

        // composable(
        //     route = "EditItinerary/{tripId}"
        // ) { backStackEntry ->
        //     val debugToken = "user_jwt_token"
        //     val tripId = backStackEntry.arguments?.getString("tripId")?.toInt()
        //     val tripViewModel = remember { TripViewModel(debugToken) }
        //     EditItinerary(navController, tripId, tripViewModel)
        // }
        //   composable("tripdetail/{tripId}", arguments = listOf(navArgument("tripId") { type = NavType.IntType })) { TripDetailsScreen(navController) }
        //   composable("ItineraryScreen"){ ItineraryScreen(navController, 1, viewModel)}
//         composable(
//             route = "ItineraryScreen/{tripId}",
//             arguments = listOf(navArgument("tripId") { type = NavType.IntType })
//         ) { backStackEntry ->
//             val tripId = backStackEntry.arguments?.getInt("tripId") ?: return@composable
//             val token = "user_jwt_token"
//
//             ItineraryScreen(
//                 navController = navController,
//                 tripId = tripId,
//                 token = token
//             )
        // }
        // composable("ItineraryScreen/1") {
        //     val debugToken = "user_jwt_token"
   
        //     val tripViewModel = remember { TripViewModel(debugToken) }
        //     ItineraryScreen(navController, 1, tripViewModel)
        // }
        //   composable("stop/{stopId}") { backStackEntry ->
        //       val stop = backStackEntry.arguments?.getInt("stopId") ?: 0
        //       StopScreen(navController, stop)
        //   }
    }
}

@Composable
fun AuthNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("signup") {
            SignupScreen(navController)
        }

        // Main camera screen
        composable("camera") { CameraScreen(navController)}

        // Camera confirmation screem
        composable(
            "cameraConfirmScreen/{photoPath}",
            arguments = listOf(navArgument("photoPath") { type = NavType.StringType })
        ) { backStackEntry ->
            val photoPath = backStackEntry.arguments?.getString("photoPath")
            CameraConfirmScreen(photoPath,navController)
        }

        // Landmark Context Screen
        composable(
            "landmarkContext/{photoPath}",
            arguments = listOf(navArgument("photoPath") { type = NavType.StringType })
        ) { backStackEntry ->
            val photoPath = backStackEntry.arguments?.getString("photoPath")
            LandmarkContextScreen(photoPath,navController)
        }
    }
}

/** Navigate to a specific detail route */
fun navigateToDetail(navController: NavController, route: String) {
    navController.navigate(route)
}

/** Navigate to a root route and clear back stack */
fun navigateToRoot(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Go back to the previous screen */
fun goBack(navController: NavController) {
    navController.popBackStack()
}
