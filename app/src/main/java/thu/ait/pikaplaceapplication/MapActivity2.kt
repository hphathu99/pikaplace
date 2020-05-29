package thu.ait.pikaplaceapplication

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_map2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import thu.ait.pikaplaceapplication.adapter.PlacesAdapter
import thu.ait.pikaplaceapplication.data.PlaceItem
import thu.ait.pikaplaceapplication.data.PlacesResult
import thu.ait.pikaplaceapplication.data.Venue
import thu.ait.pikaplaceapplication.network.PlacesAPI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class MapActivity2 : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var midPoint: GeoPoint
    lateinit var placesAdapter: PlacesAdapter
    val db = FirebaseFirestore.getInstance()
    val locList = mutableListOf<GeoPoint>()
    var venues = ArrayList<Venue>()
    lateinit var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map2)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        placesAdapter = PlacesAdapter(this)
        recyclerPlaces.adapter = placesAdapter
        recyclerPlaces.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // get list of all members
        var memList = intent.getSerializableExtra("Members") as ArrayList<String>
        var groupName = intent.getStringExtra("CurGroup")
        db.collection("groups").whereEqualTo("name", groupName).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                   type = document.data["type"] as String
                }
            }
            .addOnFailureListener {
                Log.d("TAG", "FAIL")
            }
        // for member in members get location
        for (mem in memList) {
            db.collection("users").whereEqualTo("email",mem).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document != null) {
                            var lat = document.data["locLat"].toString().toDouble()
                            var long = document.data["locLong"].toString().toDouble()
                            locList.add(GeoPoint(lat, long))
                            createMemMarker(lat, long, mem)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "ERRRRR")
                }
                .addOnCompleteListener {
                    midPoint = getCentralLongLat(locList)
                    var center = LatLng(midPoint.latitude, midPoint.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(center))
                }
        }

        btnPlaces.setOnClickListener {
            Log.d("POINT", midPoint.latitude.toString() + " " + midPoint.longitude.toString())
            getPlaces(midPoint.latitude, midPoint.longitude, type)
        }
        
        btnPika.setOnClickListener {
            var n = venues.size-1
            val rnds = (0..n).random()
            if (venues.isNotEmpty()) {
                var finLoc = venues.get(rnds).name
                val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
                builder1.setMessage("Your group will meet in ${finLoc}")
                builder1.setCancelable(true)

                builder1.setPositiveButton(
                    "Yes",
                    DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

                builder1.setNegativeButton(
                    "No",
                    DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

                val alert11: AlertDialog = builder1.create()
                alert11.show()
            }
        }
    }

    fun getCentralLongLat (locations: List<GeoPoint>): GeoPoint {

        if(locations.size == 1) return GeoPoint(locations.get(0).latitude, locations.get(0).latitude)

        var x = 0.0
        var y = 0.0
        var z = 0.0

        for (location in locations) {
            var lat = location.latitude * Math.PI / 180
            var lon = location.longitude * Math.PI / 180

            x += cos(lat) * cos(lon)
            y += cos(lat) * sin(lon)
            z += sin(lat)
        }

        var total = locations.size

        x /= total
        y /= total
        z /= total

        var centralLon = atan2(y,x) * 180 / Math.PI
        var centralSquareRoot = sqrt(x * x + y * y)
        var centralLat = atan2(z, centralSquareRoot) * 180/Math.PI

        return GeoPoint(centralLat, centralLon)
    }

    fun createMemMarker(
        latitude: Double,
        longitude: Double,
        title: String
    ): Marker {

        return mMap.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
        )
    }


    fun getPlaces(latitude: Double, longitude: Double, category: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.foursquare.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var longlat = latitude.toString() + ","+ longitude.toString()
        var placesAPI = retrofit.create(PlacesAPI::class.java)

        val placeCall = placesAPI.getPlaces(
            "HPTY2KNYXWKQMUIWB5DEZQRL1EJC2VH2LSCKWPZOCFSEIKLZ",
            "YEQMSQMLCWWPGJMK2ZR1BVBO15GR0CDI2J5YVJWNN11S2YMW",
            "20180323",
            longlat,
            getCateIdFromType(category),
            5000,
            5
        )
        var places = ""
        placeCall.enqueue(object : Callback<PlacesResult> {
            override fun onFailure(call: Call<PlacesResult>, t: Throwable) {
                Log.d("ERROR", "Unable to grasp places")
            }

            override fun onResponse(call: Call<PlacesResult>, response: Response<PlacesResult>) {
                var placesResult = response.body()
                if(placesResult != null) {
                    venues = placesResult.response?.venues as ArrayList<Venue>

                    for (v in venues) {
                        var p= PlaceItem(v.name, v.location?.address, category, "Distance: ${v.location?.distance}")
                        handleItemCreated(p)
                        createPlacesMarker(v.location?.lat?.toDouble(), v.location?.lng?.toDouble(), v.name)
                    }
                }
            }
        })
    }

    protected fun createPlacesMarker(
        latitude: Double?,
        longitude: Double?,
        title: String?
    ): Marker {
        if(latitude == null || longitude == null) return Marker(null)

        return this.mMap.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
    }

    fun getCateIdFromType (category: String): String {
        when (category) {
            "Arts,Entertainment" -> return "4d4b7104d754a06370d81259"
            "Restaurant" -> return "4bf58dd8d48988d1c4941735"
            "Coffee" -> return "4bf58dd8d48988d1e0931735"
            "Bar" -> return "4d4b7105d754a06376d81259"
            "Outdoors" -> return "4d4b7105d754a06377d81259"
        }
        return ""
    }

    private fun handleItemCreated(placeItem: PlaceItem) {
        placesAdapter.addItem(placeItem)
    }
}

