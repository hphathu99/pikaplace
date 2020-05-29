package thu.ait.pikaplaceapplication.data

import android.location.Location
import com.google.firebase.firestore.GeoPoint

data class Group(
    var name: String,
    var users: List<String>,
    var date: String,
    var time: String,
    var type: String,
    var finalLocation: String?,
    var locations: List<GeoPoint>?
)