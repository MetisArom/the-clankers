package theclankers.tripview.ui.components

// Component representing a single User (row) in a list of users


// Input:
// user_id
// type = X, check, both, nullable, request, sent
// onClick = () => {} (click the entire row, perhaps to entire a profile?)
// onClickButton1 = () => {} (click the first button, depends on the relationship)
// onClickButton2 = () => {} (click the second button, only exists if type == both)


// Takes as input a Relationship object
// User 1's relationship to User2


// If the object exists, that means the two users are either already friends or there is a pending friend request
// The boolean flag "isFriends" differentiates between these two cases
// If "isFriends" == false, then that means there is a pending friend request from user1 to user2


// Get the user_ids from the relationship object, query the database for:
// First Name, Last Name, username

// Also shows generic profile picture avatar

// Depending on the type displays the corresponding buttons, but their functionality is implemented outside this component
