package thu.ait.pikaplaceapplication.data

import android.location.Location
import android.provider.ContactsContract

data class User(
    var uid: String,
    var email: String,
    var locLong: Double?,
    var locLat: Double?,
    var List: Group?
)

