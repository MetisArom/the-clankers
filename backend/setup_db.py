import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT
from flask import Flask
from database import db, User, Trip
from datetime import date
import time
import os
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
    conn = psycopg2.connect(dbname="postgres", user=DB_USER,
                            password=DB_PASSWORD, host=DB_HOST)
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
    print("❌ Error while creating database:", e)
    exit()

# Wait briefly to ensure the new DB is ready for connections
time.sleep(1.5)

# -------------------------
# STEP 2: CONNECT NEW FLASK APP TO tripview
# -------------------------
app = Flask(__name__)
uri = f"postgresql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:5432/{DB_NAME}"
app.config["SQLALCHEMY_DATABASE_URI"] = uri
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
db.init_app(app)

# -------------------------
# STEP 3: CREATE TABLES AND SEED DATA
# -------------------------
with app.app_context():
    # --- force fresh metadata binding ---
    db.engine.dispose()

    # ensure new connection
    db.create_all()
    db.session.commit()
    print(f"✅ Tables created successfully inside '{DB_NAME}' database!")

    # --- SEED USERS ---
    users = [
        User(name="Alice Johnson", email="alice@example.com"),
        User(name="Bob Smith", email="bob@example.com"),
        User(name="Charlie Brown", email="charlie@example.com"),
    ]
    for u in users:
        db.session.add(u)
    db.session.commit()
    print("👤 3 users inserted successfully!")

    # --- SEED TRIPS ---
    trips = [
        Trip(destination="Paris", start_date=date(2025, 5, 10),
             end_date=date(2025, 5, 20), user_id=users[0].id),
        Trip(destination="Tokyo", start_date=date(2025, 6, 15),
             end_date=date(2025, 6, 25), user_id=users[1].id),
        Trip(destination="New York", start_date=date(2025, 7, 5),
             end_date=date(2025, 7, 10), user_id=users[2].id),
    ]
    for t in trips:
        db.session.add(t)
    db.session.commit()
    print("✈️ 3 trips inserted successfully!")

print("🌱 Database rebuilt, tables created, and data seeded successfully!")
