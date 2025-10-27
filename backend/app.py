from flask import Flask, jsonify, request
from flask_cors import CORS
from flask_jwt_extended import JWTManager, create_access_token, jwt_required, get_jwt_identity
from werkzeug.security import generate_password_hash, check_password_hash
from sqlalchemy.exc import IntegrityError
from access_management import is_authorized_user
from database import db, User, Trip, Stop, IsFriends, PartOf
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
