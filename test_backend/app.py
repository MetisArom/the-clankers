from flask import Flask, request, jsonify

app = Flask(__name__)

# -------------------------------
# Hard-coded data
# -------------------------------
fake_users = [
    {
        "id": 1,
        "username": "alice",
        "firstname": "Alice",
        "lastname": "Anderson",
        "likes": "mountains, hiking",
        "dislikes": "crowds"
    },
    {
        "id": 2,
        "username": "bob",
        "firstname": "Bob",
        "lastname": "Brown",
        "likes": "beaches, surfing",
        "dislikes": "cold weather"
    }
]

fake_trips = [
    {
        "trip_id": 1,
        "owner_id": 1,
        "status": "planned",
        "destination": "Paris",
        "stops": [
            {
                "stop_id": 1,
                "coordinates": [48.8566, 2.3522],
                "name": "Eiffel Tower",
                "description": "Visit the Eiffel Tower",
                "order": 0
            },
            {
                "stop_id": 2,
                "coordinates": [48.8606, 2.3376],
                "name": "Louvre Museum",
                "description": "See the Mona Lisa",
                "order": 1
            }
        ]
    }
]

fake_friends = {
    1: {"friends": [2], "incoming_requests": [], "outgoing_requests": []},
    2: {"friends": [1], "incoming_requests": [], "outgoing_requests": []}
}

# -------------------------------
# USER ENDPOINTS
# -------------------------------

@app.route("/user/<int:user_id>", methods=["GET"])
def get_user(user_id):
    user = next((u for u in fake_users if u["user_id"] == user_id), None)
    if user:
        return jsonify(user)
    return jsonify({"message": "User not found"}), 404

@app.route("/create_user", methods=["POST"])
def create_user():
    data = request.get_json()
    new_user = {
        "user_id": len(fake_users) + 1,
        "username": data.get("username"),
        "firstname": data.get("fullname", "").split(" ")[0],
        "lastname": data.get("fullname", "").split(" ")[-1],
        "likes": data.get("likes", ""),
        "dislikes": data.get("dislikes", "")
    }
    fake_users.append(new_user)
    return jsonify({
        "message": "User created successfully",
        "access_token": "fake_jwt_token",
        "user": new_user
    })

@app.route("/login", methods=["POST"])
def login():
    data = request.get_json()
    user = next((u for u in fake_users if u["username"] == data.get("username")), None)
    if user:
        return jsonify({
            "message": "Login successful",
            "access_token": "fake_jwt_token",
            "user": user
        })
    return jsonify({"message": "User not found"}), 404

@app.route("/edit_user", methods=["POST"])
def edit_user():
    # JWT token ignored for MVP
    data = request.get_json()
    user = fake_users[0]  # just edit first user for simplicity
    user.update({
        "firstname": data.get("firstname", user["firstname"]),
        "lastname": data.get("lastname", user["lastname"]),
        "likes": data.get("likes", user["likes"]),
        "dislikes": data.get("dislikes", user["dislikes"])
    })
    return jsonify({"message": "User updated successfully", "user": user})

# -------------------------------
# FRIENDSHIP ENDPOINTS
# -------------------------------

@app.route("/friends", methods=["GET"])
def get_friends():
    # Always return for user_id=1
    user_id = 1
    friends_list = [u for u in fake_users if u["user_id"] in fake_friends[user_id]["friends"]]
    incoming = [u for u in fake_users if u["user_id"] in fake_friends[user_id]["incoming_requests"]]
    outgoing = [u for u in fake_users if u["user_id"] in fake_friends[user_id]["outgoing_requests"]]
    return jsonify({
        "friends": friends_list,
        "incoming_requests": incoming,
        "outgoing_requests": outgoing
    })

@app.route("/friends/request", methods=["POST"])
def send_friend_request():
    data = request.get_json()
    # For MVP, just return success
    return jsonify({"message": f"Friend request sent to user {data.get('user_id')}"})

@app.route("/friends/accept", methods=["POST"])
def accept_friend_request():
    data = request.get_json()
    return jsonify({"message": f"Friend request from user {data.get('user_id')} accepted"})

@app.route("/friends/decline", methods=["POST"])
def decline_friend_request():
    data = request.get_json()
    return jsonify({"message": f"Friend request from user {data.get('user_id')} declined"})

@app.route("/friends/search", methods=["GET"])
def search_users():
    query = request.args.get("q", "")
    results = [u for u in fake_users if query.lower() in u["username"].lower()]
    for u in results:
        u["status"] = "none"
    return jsonify(results)

# -------------------------------
# TRIP ENDPOINTS
# -------------------------------

@app.route("/trips/create", methods=["POST"])
def create_trip():
    data = request.get_json()
    new_trip = {
        "trip_id": len(fake_trips) + 1,
        "owner_id": 1,
        "status": "planned",
        "destination": data.get("destination"),
        "stops": []
    }
    fake_trips.append(new_trip)
    return jsonify(new_trip)

# -------------------------------
# PARTY MANAGEMENT
# -------------------------------

@app.route("/party/invite", methods=["POST"])
def invite_member():
    data = request.get_json()
    return jsonify({"message": f"User {data.get('friend_id')} invited to trip {data.get('trip_id')}"})

@app.route("/party/remove", methods=["POST"])
def remove_member():
    data = request.get_json()
    return jsonify({"message": f"User {data.get('friend_id')} removed from trip {data.get('trip_id')}", "removed_by": 1})

@app.route("/party/<int:trip_id>", methods=["GET"])
def view_party_members(trip_id):
    trip = next((t for t in fake_trips if t["trip_id"] == trip_id), None)
    if trip:
        members = [u for u in fake_users if u["user_id"] in [trip["owner_id"]]]
        return jsonify({
            "trip_id": trip_id,
            "owner_id": trip["owner_id"],
            "members": members
        })
    return jsonify({"message": "Trip not found"}), 404

# -------------------------------
# Run server
# -------------------------------
if __name__ == "__main__":
    app.run(debug=True)
