from flask import Flask, jsonify, request
from database import db, User, Trip
import os
from dotenv import load_dotenv

# -------------------------
# Flask App Config
# -------------------------
load_dotenv()
DB_PASSWORD = os.getenv("DB_PASSWORD")

if DB_PASSWORD == None:
    print("")
    os.exit(1)

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = "postgresql://postgres:" + DB_PASSWORD + "@localhost:5432/tripview"
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

# ✅ Initialize the existing db instance (do NOT re-create it)
db.init_app(app)

# -------------------------
# Routes
# -------------------------
@app.route('/')
def home():
    return jsonify({"message": "TripView API is running and connected to PostgreSQL!"})

# Create a new user
@app.route('/users', methods=['POST'])
def create_user():
    data = request.get_json()
    if not data or not data.get('name') or not data.get('email'):
        return jsonify({"error": "Missing required fields"}), 400
    user = User(name=data['name'], email=data['email'])
    db.session.add(user)
    db.session.commit()
    return jsonify({
        "id": user.id,
        "name": user.name,
        "email": user.email
    }), 201

# Get all users
@app.route('/users', methods=['GET'])
def get_users():
    users = User.query.all()
    return jsonify([{
        "id": u.id,
        "name": u.name,
        "email": u.email
    } for u in users])

# Create a new trip
@app.route('/trips', methods=['POST'])
def create_trip():
    data = request.get_json()
    if not data or not all(k in data for k in ('destination', 'start_date', 'end_date', 'user_id')):
        return jsonify({"error": "Missing required fields"}), 400

    trip = Trip(
        destination=data['destination'],
        start_date=data['start_date'],
        end_date=data['end_date'],
        user_id=data['user_id']
    )
    db.session.add(trip)
    db.session.commit()
    return jsonify({
        "id": trip.id,
        "destination": trip.destination,
        "start_date": str(trip.start_date),
        "end_date": str(trip.end_date),
        "user_id": trip.user_id
    }), 201

# Get all trips
@app.route('/trips', methods=['GET'])
def get_trips():
    trips = Trip.query.all()
    return jsonify([{
        "id": t.id,
        "destination": t.destination,
        "start_date": str(t.start_date),
        "end_date": str(t.end_date),
        "user_id": t.user_id
    } for t in trips])

# Delete a trip by ID
@app.route('/trips/<int:trip_id>', methods=['DELETE'])
def delete_trip(trip_id):
    trip = Trip.query.get_or_404(trip_id)
    db.session.delete(trip)
    db.session.commit()
    return jsonify({"message": f"Trip {trip_id} deleted successfully!"})

# -------------------------
# Run the Flask Server
# -------------------------
if __name__ == '__main__':
    with app.app_context():
        db.create_all()  # ensures tables exist
    app.run(debug=True)