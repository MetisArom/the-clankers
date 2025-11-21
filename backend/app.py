from flask import Flask, jsonify, request
from flask_cors import CORS
from flask_jwt_extended import JWTManager, create_access_token, jwt_required, get_jwt_identity
from werkzeug.security import generate_password_hash, check_password_hash
from sqlalchemy.exc import IntegrityError
from database import db, User, Trip, Stop, IsFriends, PartOf
from datetime import timedelta
import google.generativeai as genai
import os
import json
from polyline import regenerate_driving_polyline
from place import get_place_info
from dotenv import load_dotenv
import base64

# -------------------------
# Flask App Config
# -------------------------
load_dotenv()
DB_PASSWORD = os.getenv("DB_PASSWORD")
MAPS_API_KEY = os.getenv("MAPS_API_KEY")
genai.configure(api_key=os.getenv("GEMINI_API_KEY"))
model = genai.GenerativeModel("models/gemini-2.5-flash")

if not DB_PASSWORD:
    raise ValueError("❌ Missing DB_PASSWORD in environment variables")

if not MAPS_API_KEY:
    raise ValueError("❌ Missing MAPS_API_KEY in environment variables")

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

# ============================================================
# FRIENDSHIP ENDPOINTS
# ============================================================

# Get list of friends of user_id, return a list of strings
@app.route('/get_friends/<int:user_id>', methods=['GET'])
@jwt_required()
def get_friends(user_id):
    friendships = IsFriends.query.filter(
        (
            (IsFriends.friend1_id == user_id) | 
            (IsFriends.friend2_id == user_id)
        ) &
        (IsFriends.relationship.is_(True))
    ).all()


    friends_list = []

    for f in friendships:
        # Identify the other person
        other_id = f.friend2_id if f.friend1_id == user_id else f.friend1_id
        other_user = db.session.get(User, other_id)

        if not other_user:
            continue

        friends_list.append(other_user.user_id)

    return jsonify(friends_list), 200
    
@app.route('/get_invites/<int:user_id>', methods=['GET'])
def get_invites(user_id):
    # Fetch all incoming friend requests for this user
    friendships = IsFriends.query.filter(
        ((IsFriends.friend1_id == user_id) | (IsFriends.friend2_id == user_id)) &
        (IsFriends.relationship == False) &
        (IsFriends.initiator_id != user_id)
    ).all()

    incoming_requests = []

    for f in friendships:
        # Identify the other person (the sender)
        other_id = f.friend2_id if f.friend1_id == user_id else f.friend1_id
        other_user = db.session.get(User, other_id)

        if not other_user:
            continue

        incoming_requests.append(other_user.user_id)

    return jsonify(incoming_requests), 200

@app.route('/get_relationship/<int:user_id1>/<int:user_id2>', methods=['GET'])
def get_relationship(user_id1, user_id2):
    f1, f2 = sorted([user_id1, user_id2])

    if user_id1 == user_id2:
        return jsonify({"status": "self"}), 200

    friendship = IsFriends.query.filter_by(friend1_id=f1, friend2_id=f2).first()

    if not friendship:
        return jsonify({"status": "none"}), 200

    if friendship.relationship:
        return jsonify({"status": "friends"}), 200
    else:
        if friendship.initiator_id == user_id1:
            return jsonify({"status": "pending_outgoing"}), 200
        else:
            return jsonify({"status": "pending_incoming"}), 200

# Sending a Friend Request
@app.route('/send_friend_request/<int:user_id>', methods=['POST'])
@jwt_required()
def send_friend_request(user_id):
    current_user_id = int(get_jwt_identity())
    receiver_id = user_id

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

# Accept friend request, the user_id provided in the params is the sender of the request
@app.route('/accept_friend_request/<int:user_id>', methods=['POST'])
@jwt_required()
def accept_friend_request(user_id):
    current_user_id = int(get_jwt_identity())
    sender_id = user_id

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
@app.route('/decline_friend_request/<int:user_id>', methods=['POST'])
@jwt_required()
def decline_friend_request(user_id):
    current_user_id = int(get_jwt_identity())
    sender_id = user_id
    #sender_id = data.get('user_id')

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

# Revoke a sent Friend Request
@app.route('/revoke_friend_request/<int:user_id>', methods=['POST'])
@jwt_required()
def revoke_friend_request(user_id):
    current_user_id = int(get_jwt_identity())
    receiver_id = user_id

    if not receiver_id:
        return jsonify({"error": "Missing receiver user_id"}), 400

    f1, f2 = sorted([current_user_id, receiver_id])

    friendship = IsFriends.query.filter_by(friend1_id=f1, friend2_id=f2, relationship=False).first()
    if not friendship:
        return jsonify({"error": "No pending friend request found"}), 404

    # ✅ Only sender can revoke
    if friendship.initiator_id != current_user_id:
        return jsonify({"error": "You can only revoke your own sent requests"}), 403

    db.session.delete(friendship)
    db.session.commit()

    return jsonify({"message": "Friend request revoked"}), 200

# Remove an existing friend
@app.route('/remove_friend/<int:user_id>', methods=['POST'])
@jwt_required()
def remove_friend(user_id):
    current_user_id = int(get_jwt_identity())
    friend_id = user_id

    if not friend_id:
        return jsonify({"error": "Missing friend user_id"}), 400

    f1, f2 = sorted([current_user_id, friend_id])

    friendship = IsFriends.query.filter_by(friend1_id=f1, friend2_id=f2, relationship=True).first()
    if not friendship:
        return jsonify({"error": "No existing friendship found"}), 404

    db.session.delete(friendship)
    db.session.commit()

    return jsonify({"message": "Friend removed successfully"}), 200

# Search for friends in the Add friends page by username. 
# Use this endpoint for performing a Debounced Username Search or Real-time type-ahead user search.
# Use ?query=${query}
@app.route('/search_friends', methods=['GET'])
@jwt_required()
def search_users():
    current_user_id = int(get_jwt_identity())
    query = request.args.get('query', '').strip().lower()
    
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

        results.append(user.user_id)

    return jsonify(results), 200


# ============================================================
# TRIPS CRUD (Base)
# ============================================================

@app.route('/submit_form', methods=['POST'])
@jwt_required()
def generate_ai_trip():
    """AI-powered Trip Generator using Gemini"""
    current_user_id = int(get_jwt_identity())
    user = db.session.get(User, current_user_id)
    if not user:
        return jsonify({"error": "User not found"}), 404

    data = request.get_json()
    destination = data.get("destination", "").strip()
    num_versions = int(data.get("num_versions", 1))
    num_days = data.get("num_days", 1)
    stops = data.get("stops", "")
    timeline = data.get("timeline", "")

    if not destination:
        return jsonify({"error": "Destination is required"}), 400

    prompt = f"""
    You are TripView AI — an expert travel planner that generates realistic, structured trip itineraries.

    ---

    ### INPUT DETAILS
    Destination: {destination}
    Number of Versions: {num_versions}

    User Likes (HIGH PRIORITY): {user.likes}
    User Dislikes (HIGH PRIORITY): {user.dislikes}

    Trip Duration (days): {num_days}

    Additional Trip Preferences: Stops = {stops}, Timeline = {timeline}

    ---

    ### RULES
    1. Generate {num_versions} complete trip versions for the given destination.
    2. Each trip version should include:
    - A single list of **stops**.
    - Each stop should represent a key experience, activity, or location aligned with the user's interests.
    3. Each stop must include:
    - name, coordinates (lat, lng), stop_type, and a short description (1-2 sentences). 
    - Include details like how long the activity at the stop usually takes in the short description whenever applicable.
    4. Coordinates should be realistic near the destination.
    5. Incorporate trip-specific additional information about stops and user-suggested timeline alongside user-level preferences. 
    - Trip-specific information represent this trip's most immediate information and goals.
    - User-level preferences represent general tendencies of the user.
    6. The JSON format must always remain identical, valid, and strictly follow the schema below.
    7. Do not include Markdown, explanations, or any text outside the JSON block.
    8. Always generate at least 5-10 stops depending on the duration and trip type.
    9. Personalize every stop so it feels custom-tailored to the traveler's interests.

    ---

    ### STRICT JSON SCHEMA EXAMPLE
    Use this structure exactly and include "..." where multiple elements may appear.
    """

    schema_block = r"""
        {
        "trips": [
            {
            "version": 1,
            "name": "string",
            "description": "string",
            "stops": [
                {
                "name": "string",
                "latitude": number,
                "longitude": number,
                "stop_type": "string",
                "order": number,
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

    return itinerary_data, 200

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

# ============================================================
# Ethan added these routes
# ============================================================
# TODO: Fix /create_user endpoint
# Rename it to /signup (consistent with login)
# Make sure it collects "firstname", "lastname", "email", "username", "password", "likes", "dislikes"
# Change "fullname" to "firstname" and "lastname" separately
# Collect "email" as well

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

# TODO: Fix /login endpoint
# The username parameter should also accept email for login

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
        "user_id": user.user_id
    }), 200
    
@app.route('/user/<int:user_id>', methods=['GET'])
def get_user(user_id):
    user = User.query.get_or_404(user_id)
    return jsonify({
        "user_id": user.user_id,
        "username": user.username,
        "email": user.email,
        "firstname": user.firstname,
        "lastname": user.lastname,
        "likes": user.likes,
        "dislikes": user.dislikes
    })
    
# TODO: Fix /edit_user endpoint
# Adjust the route to accept parameter /edit_user/<int:user_id>
# Make sure it accepts firstName, lastName, username, likes, and dislikes
@app.route('/edit_user', methods=['POST'])
@jwt_required()   # 👈 requires valid token in header "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..."
def edit_user():
    # Get the current logged-in user ID from the token
    current_user_id = int(get_jwt_identity())

    # Fetch full user object from DB
    user = db.session.get(User, current_user_id)
    if not user:
        return jsonify({"error": "User not found"}), 404

    data = request.get_json()
    allowed_fields = ['firstname', "lastname", "username" , "likes", "dislikes"]

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

@app.route('/get_active_trips/<int:user_id>', methods=['GET'])
def get_active_trips(user_id):
    # Get trip_ids where the user is the owner and status is not "completed"
    # ONLY return the trip_ids
    trips = Trip.query.filter(Trip.owner_id == user_id, Trip.status != 'completed').all()
    return jsonify([t.trip_id for t in trips])
    
@app.route('/get_completed_trips/<int:user_id>', methods=['GET'])
def get_completed_trips(user_id):
    # Get trips where the user is the owner and status is "completed"
    trips = Trip.query.filter_by(owner_id=user_id, status='completed').all()
    return jsonify([t.trip_id for t in trips])
    
@app.route('/get_friends_trips/<int:user_id>', methods=['GET'])
def get_friends_trips(user_id):
    # Get trips where the user is a member but not the owner
    part_of_entries = PartOf.query.filter_by(user_id=user_id).all()
    trip_ids = [entry.trip_id for entry in part_of_entries]
    trips = Trip.query.filter(Trip.trip_id.in_(trip_ids), Trip.owner_id != user_id).all()
    return jsonify([t.trip_id for t in trips])

@app.route('/stop/<int:stop_id>', methods=['GET'])
def get_stop(stop_id):
    stop = Stop.query.get_or_404(stop_id)
    return jsonify({
        "stop_id": stop.stop_id,
        "trip_id": stop.trip_id,
        "stop_type": stop.stop_type,
        "latitude": stop.latitude,
        "longitude": stop.longitude,
        "name": stop.name,
        "completed": stop.completed,
        "order": stop.order
    })
    
@app.route('/trip/<int:trip_id>', methods=['GET'])
def get_trip(trip_id):
    trip = Trip.query.get_or_404(trip_id)
    sorted_stops = db.session.query(Stop).filter_by(trip_id=trip_id).order_by(Stop.order.asc()).all()
    return jsonify({
        "trip_id": trip.trip_id,
        "owner_id": trip.owner_id,
        "status": str(trip.status),
        "name": trip.name,
        "description": trip.description,
        "driving_polyline": str(trip.driving_polyline),
        "driving_polyline_timestamp": trip.driving_polyline_timestamp,
        "stop_ids": [stop.stop_id for stop in sorted_stops]
    })
    
@app.route('/update_stop_completed/<int:stop_id>', methods=['PUT'])
def update_stop_completed(stop_id):
    data = request.get_json()
    stop = Stop.query.get_or_404(stop_id)
    stop.completed = data.get('completed', stop.completed)
    db.session.commit()
    return jsonify({"message": f"Stop {stop_id} updated successfully!"})

@app.route('/stops/<int:stop_id>', methods=['DELETE'], endpoint="delete_stop")
# @jwt_required
def delete_stop(stop_id):
    stop = Stop.query.filter_by(stop_id=stop_id).first()
    if not stop:
        return jsonify({"ERROR": f" stop_id {stop_id} not found"})
    db.session.delete(stop)
    db.session.commit()
    return jsonify({"message": f"Stop with stop_id {stop_id} successfully deleted"})

# ============================================================
# Ethan added these routes
# ============================================================

@app.route('/trips/<int:trip_id>/stops', methods=['GET'])
def display_itinerary(trip_id):
    stops = Stop.query.filter_by(trip_id=trip_id).order_by(Stop.order).all()
    return jsonify([
        {
            "stop_id": s.stop_id,
            "stop_type": s.stop_type,
            "latitude": s.latitude,
            "longitude": s.longitude,
            "name": s.name,
            "completed": s.completed,
            "order": s.order
        }
        for s in stops
    ])

@app.route('/trips/<int:trip_id>/stops', methods=['PATCH'])
def modify_itinerary(trip_id):
    data = request.get_json()

    stops_data = data['stops']
    existing_stops = Stop.query.filter_by(trip_id=trip_id).all()
    existing_dict = {stop.stop_id: stop for stop in existing_stops}

    # loop through new stop data sent by user
    # if there is a new stop created, initial stop_id should be null (sent by app)
    for s in stops_data:
        stop_id = s.get('stop_id')
        stop_order = s.get('order')

        # checking if existing stop's order changed
        if stop_id and stop_id in existing_dict:
            stop = existing_dict[stop_id]
            stop.order = stop_order

        # adding newly added stop
        elif not stop_id:
            new_stop = Stop(
                trip_id=trip_id,
                stop_type=s.get('stop_type', ''),
                latitude=s['latitude'],
                longitude=s['longitude'],
                name=s.get('name', ''),
                completed=s.get('completed', False),
                order=(len(existing_stops)+1)
            )
            db.session.add(new_stop)
        
        elif stop_id not in existing_dict:
            return jsonify({"error": f"stop_id {stop_id} not found in this trip"}), 404

    db.session.commit()

    #If stops provided, get place information:
    # get all stop ids for trip
    # call get_place_info for each

    regenerate_driving_polyline(trip_id, True)

    return jsonify({"message": "Stops updated successfully"}), 200

@app.route('/choose_trip', methods=['POST'])
@jwt_required()
def save_trip():
    """
    Save a trip to the database with its stops
    """
    try:
        print("---- save_trip called ----")
        current_user_id = int(get_jwt_identity())
        print(f"Current user ID: {current_user_id}")

        user = db.session.get(User, current_user_id)
        if not user:
            print("User not found!")
            return jsonify({"error": "User not found"}), 404
        print(f"User found: {user}")

        data = request.get_json()
        print(f"Received JSON data: {data}")

        if not data.get("name"):
            print("Trip name missing in request")
            return jsonify({"error": "Trip name is required"}), 400

        # Create new trip
        new_trip = Trip(
            owner_id=current_user_id,
            name=data.get("name").strip(),
            description=data.get("description", "").strip(),
            status="active",
            driving_polyline="",
            driving_polyline_timestamp=None
        )
        print(f"Created Trip object: {new_trip}")

        db.session.add(new_trip)
        db.session.flush()  # Get trip_id before adding stops
        print(f"Trip ID after flush: {new_trip.trip_id}")

        # Add stops if provided
        stops_data = data.get("stops", [])
        print(f"Stops data: {stops_data}")
        if stops_data:
            for i, stop_info in enumerate(stops_data):
                print(f"Processing stop {i}: {stop_info}")
                new_stop = Stop(
                    trip_id=new_trip.trip_id,
                    name=stop_info.get("name", "").strip(),
                    latitude=str(stop_info.get("latitude", "")),
                    longitude=str(stop_info.get("longitude", "")),
                    stop_type=stop_info.get("stop_type", "attraction"),
                    order=stop_info.get("order", 0),
                    completed=False
                )
                print(f"Adding stop: {new_stop}")
                db.session.add(new_stop)

        db.session.commit()
        print("Database commit successful!")

        #If stops provided, get place information:
        # get all stop ids for trip
        # call get_place_info for each

        regenerate_driving_polyline(new_trip.trip_id, True)
        print("Driving polyline regenerated")

        return jsonify({
            "message": "Trip saved successfully",
            "trip_id": new_trip.trip_id,
            "trip": {
                "trip_id": new_trip.trip_id,
                "owner_id": new_trip.owner_id,
                "name": new_trip.name,
                "description": new_trip.description,
                "status": new_trip.status,
                "stop_count": len(stops_data)
            }
        }), 201

    except ValueError as e:
        db.session.rollback()
        print(f"ValueError: {str(e)}")
        return jsonify({"error": f"Invalid data format: {str(e)}"}), 400
    except Exception as e:
        db.session.rollback()
        print(f"Exception occurred: {str(e)}")
        import traceback
        traceback.print_exc()
        return jsonify({"error": f"Failed to save trip: {str(e)}"}), 500

    
@app.route('/trips/<int:trip_id>', methods=['DELETE'])
def delete_trip(trip_id):
    trip = Trip.query.get_or_404(trip_id)
    db.session.delete(trip)
    db.session.commit()
    return jsonify({"message": f"Trip {trip_id} deleted successfully!"})


@app.route('/trips/<int:trip_id>/debug_polyline', methods=['GET'])
def debug_regenerate_polyline(trip_id):
    return regenerate_driving_polyline(trip_id, True)

@app.route('/stops/<int:stop_id>/debug_place_id', methods=['GET'])
def debug_place_id(stop_id):
    return get_place_info(stop_id, True, MAPS_API_KEY)


# ===========================================================
# CAMERA ENDPOINTS
# ===========================================================

# Send a photo to landmark context generator.
@app.route('/landmark_context', methods=['POST'])
@jwt_required()
def landmark_context():
    if "image" not in request.files:
        return jsonify({"error": "Missing image multipart form data"}), 400

    image = request.files.get('image')

    image.stream.seek(0, os.SEEK_END)
    size = image.stream.tell()
    image.stream.seek(0)

    if size == 0:
        return jsonify({"error": "Empty file uploaded"}), 400

    MAX_INLINE_BYTES = 10*1024*1024 # 10 MB
    if size > MAX_INLINE_BYTES:
        return jsonify({ "error": "Image too large for inline request."}), 413

    image_bytes = image.read()

    mime_type = image.mimetype or "image/jpeg"

    prompt_text = f"A user took this photo of a landmark. " \
        "Identify the landmark and provide short contextual information: " \
        "- name of landmark\n" \
        "- city/country\n" \
        "- brief historical or contextual description\n" \
        "- confidence level or 'unknown' if uncertain.\n" \
        "Return the result as a plain text paragraph in the tone of a tour guide."
    prompt_image = { "mime_type": mime_type, "data": image_bytes}

    inputs = [prompt_text, prompt_image]

    response = model.generate_content(inputs)
    text_response = response.text.strip() if response.text else "No response from model."

    return jsonify({"context": text_response}), 201

# ============================================================
# PARTY MANAGEMENT (Owner Controlled)
# ============================================================

@app.route('/party/invite', methods=['POST'])
@jwt_required()
def invite_to_party():
    # Get current logged-in user from JWT
    current_user_id = int(get_jwt_identity())

    data = request.get_json()
    friend_id = data.get('friend_id')
    trip_id = data.get('trip_id')

    if not all([friend_id, trip_id]):
        return jsonify({"error": "Missing required fields"}), 400

    if friend_id == current_user_id:
        return jsonify({"error": "Cannot invite yourself"}), 400

    trip = db.session.get(Trip, trip_id)
    if not trip:
        return jsonify({"error": "Trip not found"}), 404

    # ✅ Only allow if current user is the trip owner
    if trip.owner_id != current_user_id:
        return jsonify({"error": "Only the trip owner can invite members"}), 403

    # ✅ Ensure invitee is a confirmed friend
    f1, f2 = sorted([current_user_id, friend_id])
    friendship = IsFriends.query.filter_by(friend1_id=f1, friend2_id=f2, relationship=True).first()
    if not friendship:
        return jsonify({"error": "You can only invite confirmed friends"}), 403

    # ✅ Check if friend is already part of the trip
    existing = PartOf.query.filter_by(trip_id=trip_id, user_id=friend_id).first()
    if existing:
        return jsonify({"error": "User already in trip"}), 409

    # ✅ Add invited member to the trip
    db.session.add(PartOf(trip_id=trip_id, user_id=friend_id))
    db.session.commit()

    return jsonify({"message": f"User {friend_id} invited to trip {trip_id}"}), 201


@app.route('/party/remove', methods=['POST'])
@jwt_required()
def remove_from_party():
    # Get the current logged-in user ID from the JWT token
    current_user_id = int(get_jwt_identity())

    data = request.get_json()
    friend_id = data.get('friend_id')
    trip_id = data.get('trip_id')

    if not all([friend_id, trip_id]):
        return jsonify({"error": "Missing required fields"}), 400

    # Fetch the trip and verify ownership
    trip = db.session.get(Trip, trip_id)
    if not trip:
        return jsonify({"error": "Trip not found"}), 404

    # ✅ Only the owner of the trip can remove members
    if trip.owner_id != current_user_id:
        return jsonify({"error": "Only the trip owner can remove members"}), 403

    # Check if the target user is part of the trip
    member = PartOf.query.filter_by(trip_id=trip_id, user_id=friend_id).first()
    if not member:
        return jsonify({"error": "User not part of this trip"}), 404

    # ✅ Remove the member
    db.session.delete(member)
    db.session.commit()

    return jsonify({
        "message": f"User {friend_id} removed from trip {trip_id}",
        "removed_by": current_user_id
    }), 200


@app.route('/party/<int:trip_id>', methods=['GET'])
@jwt_required()
def get_party_members(trip_id):
    # Get the user ID from the JWT token
    current_user_id = int(get_jwt_identity())

    # Check if the trip exists
    trip = db.session.get(Trip, trip_id)
    if not trip:
        return jsonify({"error": "Trip not found"}), 404

    # Get all current members of this trip (excluding owner for now)
    members = PartOf.query.filter_by(trip_id=trip_id).all()
    member_ids = [m.user_id for m in members]

    # ✅ Access Control: Only owner or members can view
    if current_user_id != trip.owner_id and current_user_id not in member_ids:
        return jsonify({"error": "You are not part of this trip"}), 403

    # Combine owner + members
    user_ids = member_ids + [trip.owner_id]
    users = User.query.filter(User.user_id.in_(user_ids)).all()

    # ✅ Return the trip’s full member list
    return jsonify({
        "trip_id": trip_id,
        "owner_id": trip.owner_id,
        "members": [
            {"user_id": u.user_id, "username": u.username, "firstname": u.firstname, "lastname": u.lastname}
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
