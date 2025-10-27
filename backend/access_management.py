from database import db, PartOf

def is_in_party(trip_id, user_id):
    return db.session.get(PartOf, (trip_id, user_id)) is not None