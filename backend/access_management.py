from database import db, PartOf


def is_authorized_user(trip_id, user_id):
    return db.session.get(PartOf, (trip_id, user_id)) is not None