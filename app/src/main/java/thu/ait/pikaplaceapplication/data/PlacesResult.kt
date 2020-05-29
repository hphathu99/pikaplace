package thu.ait.pikaplaceapplication.data

data class PlacesResult(val meta: Meta?, val response: Response?)

data class Categories(val id: String?, val name: String?, val pluralName: String?, val shortName: String?, val icon: Icon?, val primary: Boolean?)

data class Icon(val prefix: String?, val sizes: List<Number>?, val name: String?)

data class LabeledLatLngs(val label: String?, val lat: Number?, val lng: Number?)

data class Location(val address: String?, val crossStreet: String?, val lat: Number?, val lng: Number?, val labeledLatLngs: List<LabeledLatLngs>?, val distance: Number?, val postalCode: String?, val cc: String?, val city: String?, val state: String?, val country: String?, val formattedAddress: List<String>?)

data class Meta(val code: Number?, val requestId: String?)

data class Response(val venues: List<Venue>?, val confident: Boolean?)

data class Venue(val id: String?, val name: String?, val location: Location?, val categories: List<Categories>?)

