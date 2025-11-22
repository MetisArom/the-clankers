from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

class User(db.Model):
    __tablename__ = 'users'
    user_id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(50), unique=True, nullable=False)
    password = db.Column(db.String(255), nullable=False)
    email = db.Column(db.String(100), unique=True, nullable=False)
    firstname = db.Column(db.String(100))
    lastname = db.Column(db.String(100))
    likes = db.Column(db.Text)
    dislikes = db.Column(db.Text)

    trips = db.relationship('Trip', backref='owner', cascade="all, delete")


class IsFriends(db.Model):
    __tablename__ = 'isfriends'
    friend1_id = db.Column(db.Integer, db.ForeignKey('users.user_id', ondelete="CASCADE"), primary_key=True)
    friend2_id = db.Column(db.Integer, db.ForeignKey('users.user_id', ondelete="CASCADE"), primary_key=True)
    relationship = db.Column(db.Boolean, default=False)
    initiator_id = db.Column(db.Integer, db.ForeignKey('users.user_id', ondelete="CASCADE"), nullable=False)
    __table_args__ = (
        db.CheckConstraint('friend1_id < friend2_id', name='chk_order'),
    )


class Trip(db.Model):
    __tablename__ = 'trips'
    trip_id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    owner_id = db.Column(db.Integer, db.ForeignKey('users.user_id', ondelete="CASCADE"), nullable=False)
    status = db.Column(db.String(20), default='ongoing')
    driving_polyline = db.Column(db.Text)
    driving_polyline_timestamp = db.Column(db.DateTime)
    name = db.Column(db.String(255))
    description = db.Column(db.Text)
    stops = db.relationship('Stop', backref='trip', cascade="all, delete")
    chats = db.relationship('Chat', backref='trip', cascade="all, delete")
    participants = db.relationship('User', secondary='partof', backref='joined_trips')


class Stop(db.Model):
    __tablename__ = 'stops'
    stop_id = db.Column(db.Integer, primary_key=True)
    trip_id = db.Column(db.Integer, db.ForeignKey('trips.trip_id', ondelete="CASCADE"), nullable=False)
    stop_type = db.Column(db.String(50))
    latitude = db.Column(db.Text, nullable=False)
    longitude = db.Column(db.Text, nullable=False)
    name = db.Column(db.Text)
    completed = db.Column(db.Boolean, default=False)
    order = db.Column(db.Integer)
    address = db.Column(db.Text, default="")
    hours = db.Column(db.Text, default="")
    rating = db.Column(db.Text, default="")
    priceRange = db.Column(db.Text, default="")
    googleMapsUri = db.Column(db.Text, default="")


class Chat(db.Model):
    __tablename__ = 'chats'
    chat_id = db.Column(db.Integer, primary_key=True)
    trip_id = db.Column(db.Integer, db.ForeignKey('trips.trip_id', ondelete="CASCADE"), nullable=False)
    text = db.Column(db.Text, nullable=False)
    sender = db.Column(db.Integer, db.ForeignKey('users.user_id', ondelete="CASCADE"), nullable=False)
    timestamp = db.Column(db.DateTime, server_default=db.func.now())


class PartOf(db.Model):
    __tablename__ = 'partof'
    trip_id = db.Column(db.Integer, db.ForeignKey('trips.trip_id', ondelete="CASCADE"), primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('users.user_id', ondelete="CASCADE"), primary_key=True)