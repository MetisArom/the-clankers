import requests
from flask import jsonify
from database import db, Trip, Stop

def regenerate_driving_polyline(trip_id, debug):
    trip = Trip.query.get_or_404(trip_id)
    trip = db.session.get(Trip, trip_id)
    stops = db.session.query(Stop).filter_by(trip_id=trip_id).order_by(Stop.order.asc()).all()
    
    debug_response = {}
    if debug:
        debug_response["before_polyline"] = trip.driving_polyline
        OSRMresponse = {}

    if (len(stops) == 0):
        trip.driving_polyline = ""
        db.session.commit()
    else:
        # Get a string of all lat/long pairs joined by ';' for OSRM then get full OSRM api URL
        coordinatesSubStr = ';'.join(f'{s.longitude},{s.latitude}' for s in stops)
        OSRM_url = f"https://routing.openstreetmap.de/routed-car/route/v1/driving/{coordinatesSubStr}?geometries=polyline6"

        OSRMreq = requests.get(OSRM_url)
        OSRMresponse = OSRMreq.json()
        if (OSRMreq.status_code == 200 and OSRMresponse["code"] == "Ok"):
            trip.driving_polyline = OSRMresponse["routes"][0]["geometry"]
            db.session.commit()
        else:
            if OSRMresponse["code"] == "NoRoute":
                trip.driving_polyline = ""
                db.session.commit()
    
    if debug:
        debug_response["after_polyline"] = trip.driving_polyline
        debug_response["OSRM"] = OSRMresponse
        return jsonify(debug_response)
    else:
        return jsonify({"new_polyline": trip.driving_polyline})