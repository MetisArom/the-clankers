import requests
from flask import jsonify
from database import db, Stop
    
fieldMask = "places.rating,places.priceRange,places.currentOpeningHours,places.shortFormattedAddress"

def get_place_info(stop_id, debug, MAPS_API_KEY):
    stop = Stop.query.get_or_404(stop_id)
    
    debug_response = {}
    if debug:
        debug_response["latitude"] = stop.latitude
        debug_response["longitude"] = stop.longitude
        #debug_response["old_place_id"] = stop.place_id
        
    geocoding_url = "https://places.googleapis.com/v1/places:searchNearby"

    request_data = {
        "maxResultCount": 1,
        "locationRestriction": {
            "circle": {
                "center": {
                    "latitude": stop.latitude,
                    "longitude": stop.longitude,
                },
                "radius": 50.0
            }
        }
    }

    #request_data = json.dumps(request_data)

    request_headers = {
        "X-Goog-Api-Key": MAPS_API_KEY,
        "X-Goog-FieldMask": fieldMask,
        "Content-Type":"application/json; charset=UTF-8"
    }

    geocoding_request = requests.post(geocoding_url, json=request_data, headers=request_headers)
    try:
        geocoding_response = geocoding_request.json()
    except ValueError:
        return jsonify({"text": geocoding_request.text})

    if not "places" in geocoding_response:
        # leave as ""
        pass
    else:
        # update to response.results[0].place_id
        if "rating" in geocoding_response["places"][0]:
            stop.rating = geocoding_response["places"][0]["rating"]
        if "priceRange" in geocoding_response["places"][0]:
            startPrice = geocoding_response["places"][0]["priceRange"]["startPrice"]["units"]
            endPrice = geocoding_response["places"][0]["priceRange"]["endPrice"]["units"]
            stop.priceRange = f"${startPrice}-${endPrice}"
        if "shortFormattedAddress" in geocoding_response["places"][0]:
            stop.address = geocoding_response["places"][0]["shortFormattedAddress"]
        if "currentOpeningHours" in geocoding_response["places"][0]:
            hoursString = ""
            for dayHours in geocoding_response["places"][0]["currentOpeningHours"]["weekdayDescriptions"]:
                hoursString += dayHours + "\n"
            stop.hours = hoursString

    db.session.commit()

    #If debug, return some additional information.
    if debug:
        debug_response["new_rating"] = stop.rating
        debug_response["new_price_range"] = stop.priceRange
        debug_response["new_address"] = stop.address
        debug_response["new_hours"] = stop.hours
        debug_response["response"] = geocoding_response
        return jsonify(debug_response)
    else:
        return jsonify({"place_id": stop.place_id})
