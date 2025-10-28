import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT
from flask import Flask
from database import db, User, Trip, Stop, Chat, PartOf, IsFriends
from datetime import datetime, timezone
import time, os
from dotenv import load_dotenv

# -------------------------
# CONFIGURATION
# -------------------------
load_dotenv()

DB_NAME = "tripview"
DB_USER = "postgres"
DB_PASSWORD = os.getenv("DB_PASSWORD")
DB_HOST = "localhost"

# -------------------------
# STEP 1: DROP + RECREATE DATABASE
# -------------------------
try:
    conn = psycopg2.connect(dbname="postgres", user=DB_USER, password=DB_PASSWORD, host=DB_HOST)
    conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
    cur = conn.cursor()

    cur.execute(f"SELECT 1 FROM pg_database WHERE datname = '{DB_NAME}'")
    if cur.fetchone():
        cur.execute(f"DROP DATABASE {DB_NAME}")
        print(f"🗑️  Existing database '{DB_NAME}' dropped.")
    cur.execute(f"CREATE DATABASE {DB_NAME}")
    print(f"✅ Database '{DB_NAME}' created successfully!")
    cur.close()
    conn.close()
except Exception as e:
    print("❌ Error creating database:", e)
    exit()

time.sleep(1.5)

# -------------------------
# STEP 2: CONNECT FLASK APP
# -------------------------
app = Flask(__name__)
app.config["SQLALCHEMY_DATABASE_URI"] = f"postgresql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:5432/{DB_NAME}"
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
db.init_app(app)

# -------------------------
# STEP 3: CREATE + SEED DATA
# -------------------------
with app.app_context():
    db.create_all()
    print("✅ Tables created successfully!")

    # --- USERS ---
    # Password is 'easy'. So for login as Alice, username = alice, password = easy
    # This the hashed password that is used for login username, password checking.
    password_hash = "pbkdf2:sha256:1000000$MY7QTJcmlA3qrRS0$d17fdfedc906ae7ec8a1f9723de4369e7d9e8e0f98428a93968c23047c62dea0"

    alice = User(username="alice", password=password_hash, firstname="Alice", lastname="Johnson", likes="hiking", dislikes="rain")
    bob = User(username="bob", password=password_hash, firstname="Bob", lastname="Smith", likes="music", dislikes="traffic")
    charlie = User(username="charlie", password=password_hash, firstname="Charlie", lastname="Brown", likes="coding", dislikes="noise")
    david = User(username="david", password=password_hash, firstname="David", lastname="Attenbourgh", likes="outdoor activities", dislikes="zoos")
    emily = User(username="emily", password=password_hash, firstname="Emily", lastname="Nguyen", likes="hiking", dislikes="music")
    frank = User(username="frank", password=password_hash, firstname="Frank", lastname="Miller", likes="basketball", dislikes="long meetings")
    grace = User(username="grace", password=password_hash, firstname="Grace", lastname="Kim", likes="reading", dislikes="loud noises")
    harry = User(username="harry", password=password_hash, firstname="Harry", lastname="Wilson", likes="movies", dislikes="early mornings")
    irene = User(username="irene", password=password_hash, firstname="Irene", lastname="Lopez", likes="painting", dislikes="rush hour traffic")
    james = User(username="james", password=password_hash, firstname="James", lastname="Carter", likes="photography", dislikes="spicy food")
    karen = User(username="karen", password=password_hash, firstname="Karen", lastname="Patel", likes="baking", dislikes="cold weather")
    lucas = User(username="lucas", password=password_hash, firstname="Lucas", lastname="Wang", likes="cycling", dislikes="pollution")
    mia = User(username="mia", password=password_hash, firstname="Mia", lastname="Thompson", likes="yoga", dislikes="arguments")
    noah = User(username="noah", password=password_hash, firstname="Noah", lastname="Baker", likes="music", dislikes="delays")
    db.session.add_all([alice, bob, charlie, david, emily, frank, grace, harry, irene, james, karen, lucas, mia, noah])
    db.session.commit()

    # --- FRIENDSHIPS ---
    db.session.add_all([
        IsFriends(friend1_id=alice.user_id, friend2_id=bob.user_id, relationship=True, initiator_id=bob.user_id),
        IsFriends(friend1_id=alice.user_id, friend2_id=charlie.user_id, relationship=False, initiator_id=alice.user_id),
        IsFriends(friend1_id=bob.user_id, friend2_id=charlie.user_id, relationship=True, initiator_id=bob.user_id),
        IsFriends(friend1_id=charlie.user_id, friend2_id=david.user_id, relationship=False, initiator_id=charlie.user_id),
        IsFriends(friend1_id=charlie.user_id, friend2_id=emily.user_id, relationship=False, initiator_id=emily.user_id),
        IsFriends(friend1_id=charlie.user_id, friend2_id=frank.user_id, relationship=True, initiator_id=frank.user_id)
    ])

    # --- TRIPS ---
    trip1 = Trip(owner_id=alice.user_id, status="ongoing", driving_polyline="encoded_path1",
                 driving_polyline_timestamp=datetime.now(timezone.utc))
    trip2 = Trip(owner_id=bob.user_id, status="ongoing", driving_polyline="encoded_path2",
                 driving_polyline_timestamp=datetime.now(timezone.utc))
    trip3 = Trip(owner_id=charlie.user_id, status="archived", driving_polyline="encoded_path3",
                 driving_polyline_timestamp=datetime.now(timezone.utc))
    db.session.add_all([trip1, trip2, trip3])
    db.session.commit()

    # --- STOPS ---
    stops = [
        Stop(trip_id=trip1.trip_id, stop_type="pickup", latitude="42.2656", longitude="-83.7487", description="Start point 2", stop_order=3),
        Stop(trip_id=trip1.trip_id, stop_type="dropoff", latitude="42.2804", longitude="-83.7495", description="Destination 2", stop_order=4),
        Stop(trip_id=trip1.trip_id, stop_type="pickup", latitude="42.2776", longitude="-83.7409", description="Start point", stop_order=1),
        Stop(trip_id=trip1.trip_id, stop_type="dropoff", latitude="42.2456", longitude="-83.7106", description="Destination", stop_order=2),
    ]
    db.session.add_all(stops)

    # --- CHATS ---
    chats = [
        Chat(trip_id=trip1.trip_id, text="Ready for the trip?", sender=alice.user_id),
        Chat(trip_id=trip1.trip_id, text="Yes, let's go!", sender=bob.user_id),
    ]
    db.session.add_all(chats)

    # --- PARTOF ---
    db.session.add_all([
        PartOf(trip_id=trip1.trip_id, user_id=bob.user_id),
        PartOf(trip_id=trip2.trip_id, user_id=alice.user_id)
    ])

    db.session.commit()
    print("🌱 Mock data inserted successfully!")
