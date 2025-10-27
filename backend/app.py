from flask import Flask, jsonify, request
from flask_cors import CORS
from flask_jwt_extended import JWTManager, create_access_token, jwt_required, get_jwt_identity
from werkzeug.security import generate_password_hash, check_password_hash
from sqlalchemy.exc import IntegrityError
from database import db, User, Trip, IsFriends, PartOf
from datetime import timedelta
import google.generativeai as genai
import os
import json
from polyline import regenerate_driving_polyline
from dotenv import load_dotenv

# -------------------------
# Flask App Config
# -------------------------
load_dotenv()
DB_PASSWORD = os.getenv("DB_PASSWORD")
genai.configure(api_key=os.getenv("GEMINI_API_KEY"))
model = genai.GenerativeModel("models/gemini-2.5-flash")

if not DB_PASSWORD:
    raise ValueError("❌ Missing DB_PASSWORD in environment variables")

app = Flask(__name__)
CORS(app)  # allow mobile frontend access

app.config['SQLALCHEMY_DATABASE_URI'] = f"postgresql://postgres:{DB_PASSWORD}@localhost:5432/tripview"
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['JWT_SECRET_KEY'] = os.getenv("JWT_SECRET_KEY", "super-secret-key")
app.config['JWT_ACCESS_TOKEN_EXPIRES'] = timedelta(days=3)  # Token valid for 3 days
jwt = JWTManager(app)
db.init_app(app)

# -------------------------
# Error Handling
# -------------------------
@app.errorhandler(Exception)
def handle_exception(e):
    return jsonify({"error": str(e)}), 500


# -------------------------
# ROUTES
# -------------------------
@app.route('/')
def home():
    return jsonify({"message": "TripView API is running and connected to PostgreSQL!"})

# ============================================================
# USER ENDPOINTS
# ============================================================

# Create new user (with password hashing & unique check). Generates the JWT access token as well.
@app.route('/create_user', methods=['POST'])
def user_create():
    data = request.get_json()
    required_fields = ['username', 'password', "fullname"]
    if not data or not all(field in data for field in required_fields):
        return jsonify({"error": "Missing required fields"}), 400

    username = data['username'].strip().lower()
    password = data['password']
    fullname = data.get('fullname')
    likes = data.get('likes')
    dislikes = data.get('dislikes')

    if User.query.filter_by(username=username).first():
        return jsonify({"error": "Username already taken"}), 409

    hashed_password = generate_password_hash(password, method='pbkdf2:sha256', salt_length=16)
    print("Hashed Password:", hashed_password)
    new_user = User(
        username=username,
        password=hashed_password,
        fullname=fullname,
        likes=likes,
        dislikes=dislikes
    )
    try:
        db.session.add(new_user)
        db.session.commit()
    except IntegrityError:
        db.session.rollback()
        return jsonify({"error": "Database integrity error"}), 500

    # ✅ Create JWT access token for this new user
    access_token = create_access_token(identity=str(new_user.user_id))

    return jsonify({
        "message": "User created successfully",
        "access_token": access_token,
        "user": {
            "id": new_user.user_id,
            "username": new_user.username,
            "firstname": new_user.firstname,
            "lastname": new_user.lastname,
            "likes": new_user.likes,
            "dislikes": new_user.dislikes
        }
    }), 201

# Login Route. Checks username and password and generates JWT access token.
@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')

    if not username or not password:
        return jsonify({"error": "Missing username or password"}), 400

    user = User.query.filter_by(username=username.lower()).first()
    if not user or not check_password_hash(user.password, password):
        return jsonify({"error": "Invalid username or password"}), 401

    access_token = create_access_token(identity=str(user.user_id))
    return jsonify({
        "message": "Login successful",
        "access_token": access_token,
        "user": {
            "id": user.user_id,
            "username": user.username,
            "firstname": user.firstname,
            "lastname": user.lastname,
            "likes": user.likes,
            "dislikes": user.dislikes
        }
    }), 200

# Edit user info. Allowed fields are firstname, lastname, user likes and user dislikes.
@app.route('/edit_user/', methods=['POST'])
@jwt_required()   # 👈 requires valid token in header "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..."
def edit_user():
    # Get the current logged-in user ID from the token
    current_user_id = int(get_jwt_identity())

    # Fetch full user object from DB
    user = db.session.get(User, current_user_id)
    if not user:
        return jsonify({"error": "User not found"}), 404

    data = request.get_json()
    allowed_fields = ['firstname', "lastname" , 'likes', 'dislikes']

    for field in allowed_fields:
        if field in data:
            setattr(user, field, data[field])

    db.session.commit()
    return jsonify({
        "message": "User updated successfully",
        "user": {
            "id": user.user_id,
            "username": user.username,
            "firstname": user.firstname,
            "lastname": user.lastname,
            "likes": user.likes,
            "dislikes": user.dislikes
        }
    }), 200


# ============================================================
# FRIENDSHIP ENDPOINTS
# ============================================================

# Get list of friends and pending requests
@app.route('/friends', methods=['GET'])
@jwt_required()
def get_friends():
    current_user_id = int(get_jwt_identity())

    # Fetch all friend relationships involving this user
    friendships = IsFriends.query.filter(
        (IsFriends.friend1_id == current_user_id) | 
        (IsFriends.friend2_id == current_user_id)
    ).all()

    friends = []
    incoming_requests = []
    outgoing_requests = []

    for f in friendships:
        # Identify the other person
        other_id = f.friend2_id if f.friend1_id == current_user_id else f.friend1_id
        other_user = db.session.get(User, other_id)

        if not other_user:
            continue

        # Confirmed friendships
        if f.relationship:
            friends.append({
                "user_id": other_user.user_id,
                "username": other_user.username,
                "firstname": other_user.firstname,
                "lastname": other_user.lastname
            })
        else:
            # Pending — check who initiated
            if f.initiator_id == current_user_id:
                outgoing_requests.append({
                    "user_id": other_user.user_id,
                    "username": other_user.username,
                    "firstname": other_user.firstname,
                    "lastname": other_user.lastname
                })
            else:
                incoming_requests.append({
                    "user_id": other_user.user_id,
                    "username": other_user.username,
                    "firstname": other_user.firstname,
                    "lastname": other_user.lastname
                })

    return jsonify({
        "friends": friends,
        "incoming_requests": incoming_requests,
        "outgoing_requests": outgoing_requests
    }), 200

# Sending a Friend Request
@app.route('/friends/request', methods=['POST'])
@jwt_required()
def send_friend_request():
    current_user_id = int(get_jwt_identity())
    data = request.get_json()
    receiver_id = data.get('user_id')

    if not receiver_id:
        return jsonify({"error": "Missing receiver user_id"}), 400
    if receiver_id == current_user_id:
        return jsonify({"error": "Cannot send a request to yourself"}), 400

    f1, f2 = sorted([current_user_id, receiver_id])

    # Check if any existing relationship already exists
    existing = IsFriends.query.filter_by(friend1_id=f1, friend2_id=f2).first()
    if existing:
        return jsonify({"error": "Friend request or friendship already exists"}), 409

    # Create new pending request
    new_request = IsFriends(
        friend1_id=f1,
        friend2_id=f2,
        relationship=False,
        initiator_id=current_user_id
    )
    db.session.add(new_request)
    db.session.commit()

    return jsonify({"message": "Friend request sent successfully"}), 201

# Accept friend request
@app.route('/friends/accept', methods=['POST'])
@jwt_required()
def accept_friend_request():
    current_user_id = int(get_jwt_identity())
    data = request.get_json()
    sender_id = data.get('user_id')

    if not sender_id:
        return jsonify({"error": "Missing sender user_id"}), 400

    f1, f2 = sorted([current_user_id, sender_id])

    friendship = IsFriends.query.filter_by(friend1_id=f1, friend2_id=f2, relationship=False).first()
    if not friendship:
        return jsonify({"error": "No pending friend request found"}), 404

    # ✅ Only receiver can accept
    if friendship.initiator_id == current_user_id:
        return jsonify({"error": "You cannot accept your own request"}), 403

    friendship.relationship = True
    db.session.commit()

    return jsonify({"message": "Friend request accepted"}), 200

# Decline an incoming Friend Request
@app.route('/friends/decline', methods=['POST'])
@jwt_required()
def decline_friend_request():
    current_user_id = int(get_jwt_identity())
    data = request.get_json()
    sender_id = data.get('user_id')

    if not sender_id:
        return jsonify({"error": "Missing sender user_id"}), 400

    f1, f2 = sorted([current_user_id, sender_id])

    friendship = IsFriends.query.filter_by(friend1_id=f1, friend2_id=f2, relationship=False).first()
    if not friendship:
        return jsonify({"error": "No pending friend request found"}), 404

    # ✅ Only receiver can decline
    if friendship.initiator_id == current_user_id:
        return jsonify({"error": "You cannot decline your own request"}), 403

    db.session.delete(friendship)
    db.session.commit()

    return jsonify({"message": "Friend request declined"}), 200

# Search for friends in the Add friends page by username. 
# Use this endpoint for performing a Debounced Username Search or Real-time type-ahead user search.
@app.route('/friends/search', methods=['GET'])
@jwt_required()
def search_users():
    current_user_id = int(get_jwt_identity())
    query = request.args.get('q', '').strip().lower()

    if not query:
        return jsonify([]), 200

    # Get matching users except yourself
    matched_users = User.query.filter(
        User.username.ilike(f"%{query}%"),
        User.user_id != current_user_id
    ).limit(20).all()

    results = []

    for user in matched_users:
        f1, f2 = sorted([current_user_id, user.user_id])
        friendship = IsFriends.query.filter_by(friend1_id=f1, friend2_id=f2).first()

        if friendship:
            if friendship.relationship:
                status = "friends"
            else:
                # Pending
                if friendship.initiator_id == current_user_id:
                    status = "pending_outgoing"
                else:
                    status = "pending_incoming"
        else:
            status = "none"

        results.append({
            "user_id": user.user_id,
            "username": user.username,
            "firstname": user.firstname,
            "lastname": user.lastname,
            "status": status
        })

    return jsonify(results), 200


# ============================================================
# TRIPS CRUD (Base)
# ============================================================

@app.route('/trips/create', methods=['POST'])
@jwt_required()
def generate_ai_trip():
    """AI-powered Trip Generator using Gemini"""
    try:
        current_user_id = int(get_jwt_identity())
        user = db.session.get(User, current_user_id)
        if not user:
            return jsonify({"error": "User not found"}), 404

        data = request.get_json()
        destination = data.get("destination", "").strip()
        duration_days = int(data.get("duration_days", 3))
        num_versions = int(data.get("num_versions", 1))
        trip_likes = data.get("likes", "")
        trip_dislikes = data.get("dislikes", "")
        important_dates = data.get("important_dates", "")

        if not destination:
            return jsonify({"error": "Destination is required"}), 400

        user_likes = user.likes or ""
        user_dislikes = user.dislikes or ""

        # 🧠 Gemini Prompt with ... markers for repetition clarity
        prompt = f"""
        You are TripView AI — an expert travel planner that generates realistic, structured itineraries.
        You must always output valid JSON strictly following the schema below.

        ---

        ### INPUT DETAILS
        Destination: {destination}
        Trip Duration (days): {duration_days}
        Number of Versions: {num_versions}

        Trip-Specific Likes (HIGH PRIORITY): {trip_likes}
        Trip-Specific Dislikes (HIGH PRIORITY): {trip_dislikes}
        User-Level Likes (LOWER PRIORITY): {user_likes}
        User-Level Dislikes (LOWER PRIORITY): {user_dislikes}
        Important Dates or Constraints: {important_dates}

        ---

        ### RULES
        1. Generate {num_versions} complete trip versions for the specified number of days.
        2. Each trip version must contain:
        - Multiple days (equal to duration_days) or 1 day if duration_days == 1.
        - Each day typically includes multiple activities (morning, afternoon, evening)
        - If one activity naturally consumes a full day (e.g., full-day hike or long journey), only include that one, but keep the same JSON structure.
        3. Each activity must contain at least one stop with:
        - name, coordinates (lat, lng), stop_type, and description (1-2 short sentences)
        4. Coordinates should be realistic near the destination.
        5. Prioritize trip-specific likes/dislikes. Use user-level preferences only for additional context.
        6. The JSON format must always remain identical and fully valid.
        7. Do not add explanations, Markdown, or text outside JSON.
        8. Try to make the trips as personalized as possible, using the various likes and dislikes.

        ---

        ### STRICT JSON SCHEMA EXAMPLE
        Use this as the structure and include "..." where multiple elements may appear.
        """
        schema_block = r"""
        {
        "trips": [
            {
            "version": 1,
            "destination": "string",
            "duration_days": number,
            "itinerary": [
                {
                "day": 1,
                "activities": [
                    {
                    "time_of_day": "morning",
                    "stops": [
                        {
                        "name": "string",
                        "coordinates": {"lat": number, "lng": number},
                        "stop_type": "string",
                        "description": "string"
                        },
                        ...
                    ]
                    },
                    ...
                ]
                },
                ...
            ]
            },
            ...
        ]
        }
        """
        final_prompt = prompt + "\n```json\n" + schema_block + "\n```\n\n" + f"Generate {num_versions} complete trip versions following this JSON schema exactly."
        ""
        gemini_response = model.generate_content(final_prompt)
        response_text = gemini_response.text.strip()

        print("Response from Gemini:", response_text)
        # 🧹 Extract valid JSON
        json_start = response_text.find('{')
        json_end = response_text.rfind('}') + 1
        json_str = response_text[json_start:json_end]

        try:
            itinerary_data = json.loads(json_str)
        except Exception:
            return jsonify({"error": "Gemini returned invalid JSON", "raw": response_text}), 500

        # ✅ Return AI-generated trip JSON
        return jsonify({
            "message": "AI-generated trip(s) created successfully!",
            "generated_trips": itinerary_data
        }), 200

    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/trips', methods=['GET'])
def get_trips():
    trips = Trip.query.all()
    return jsonify([{
        "trip_id": t.trip_id,
        "ownerid": t.owner_id,
        "status": str(t.status),
        "driving_polyline": str(t.driving_polyline),
        "driving_polyline_timestamp": t.driving_polyline_timestamp
    } for t in trips])


@app.route('/trips/<int:trip_id>', methods=['DELETE'])
def delete_trip(trip_id):
    trip = Trip.query.get_or_404(trip_id)
    db.session.delete(trip)
    db.session.commit()
    return jsonify({"message": f"Trip {trip_id} deleted successfully!"})

@app.route('/trips/<int:trip_id>/debug_polyline', methods=['GET'])
def debug_regenerate_polyline(trip_id):
    return regenerate_driving_polyline(trip_id, True)



# ============================================================
# PARTY MANAGEMENT (Owner Controlled)
# ============================================================

@app.route('/party/invite', methods=['POST'])
def invite_to_party():
    data = request.get_json()
    owner_id = data.get('owner_id')
    friend_id = data.get('friend_id')
    trip_id = data.get('trip_id')

    if not all([owner_id, friend_id, trip_id]):
        return jsonify({"error": "Missing required fields"}), 400
    if owner_id == friend_id:
        return jsonify({"error": "Cannot invite yourself"}), 400

    trip = Trip.query.get(trip_id)
    if not trip:
        return jsonify({"error": "Trip not found"}), 404
    if trip.owner_id != owner_id:
        return jsonify({"error": "Only the trip owner can invite members"}), 403

    # Only invite confirmed friends
    f1, f2 = sorted([owner_id, friend_id])
    friendship = IsFriends.query.filter_by(friend1_id=f1, friend2_id=f2, relationship=True).first()
    if not friendship:
        return jsonify({"error": "You can only invite confirmed friends"}), 403

    # Check existing membership
    existing = PartOf.query.filter_by(trip_id=trip_id, user_id=friend_id).first()
    if existing:
        return jsonify({"error": "User already in trip"}), 409

    db.session.add(PartOf(trip_id=trip_id, user_id=friend_id))
    db.session.commit()
    return jsonify({"message": f"User {friend_id} invited to trip {trip_id}"}), 201


@app.route('/party/remove', methods=['DELETE'])
def remove_from_party():
    data = request.get_json()
    owner_id = data.get('owner_id')
    friend_id = data.get('friend_id')
    trip_id = data.get('trip_id')

    if not all([owner_id, friend_id, trip_id]):
        return jsonify({"error": "Missing required fields"}), 400

    trip = Trip.query.get(trip_id)
    if not trip:
        return jsonify({"error": "Trip not found"}), 404
    if trip.owner_id != owner_id:
        return jsonify({"error": "Only the trip owner can remove members"}), 403

    member = PartOf.query.filter_by(trip_id=trip_id, user_id=friend_id).first()
    if not member:
        return jsonify({"error": "User not part of this trip"}), 404

    db.session.delete(member)
    db.session.commit()
    return jsonify({"message": f"User {friend_id} removed from trip {trip_id}"}), 200


@app.route('/party/<int:trip_id>', methods=['GET'])
def get_party_members(trip_id):
    trip = Trip.query.get(trip_id)
    if not trip:
        return jsonify({"error": "Trip not found"}), 404

    members = PartOf.query.filter_by(trip_id=trip_id).all()
    user_ids = [m.user_id for m in members] + [trip.owner_id]
    users = User.query.filter(User.user_id.in_(user_ids)).all()

    return jsonify({
        "trip_id": trip_id,
        "owner_id": trip.owner_id,
        "members": [
            {"user_id": u.user_id, "username": u.username, "fullname": u.fullname}
            for u in users
        ]
    }), 200


# ============================================================
# RUN APP
# ============================================================
if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.run(debug=True)