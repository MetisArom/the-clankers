package theclankers.tripview.ui.utils

import android.R.attr.factor
import android.R.attr.path
import android.util.Log
import com.google.android.gms.maps.model.LatLng

//Takes in a Google-encoded polyline (https://developers.google.com/maps/documentation/utilities/polylinealgorithm)
//And decodes it into a list of LatLng's for plotting on Navigation page.
//Translated from https://github.com/googlemaps/js-polyline-codec/blob/main/src/index.ts
fun decodePolyline(encodedPath: String): List<LatLng> {
    val len = encodedPath.length
    val path = mutableListOf<LatLng>()
    var index = 0
    var lat = 0
    var lng = 0

    while (index < len) {
        var result = 1
        var shift = 0
        var b: Int
        do {
            b = encodedPath[index++].code - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lat += if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)

        result = 1
        shift = 0
        do {
            b = encodedPath[index++].code - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lng += if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)

        path.add(LatLng(lat * 1e-6, lng * 1e-6))
    }

    return path
}