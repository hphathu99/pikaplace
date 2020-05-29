package thu.ait.pikaplaceapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_cur_loc.*

class CurLocActivity : AppCompatActivity(), MyLocationProvider.OnNewLocationAvailable {

    private lateinit var myLocationProvider: MyLocationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cur_loc)

        requestNeededPermission()

        btnSetLoc.setOnClickListener {
            startActivity(Intent(this@CurLocActivity, InviteActivity::class.java))
        }
    }

    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Toast.makeText(
                    this,
                    "I need it for location", Toast.LENGTH_SHORT
                ).show()
            }

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        } else {
            startLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            101 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "ACCESS_FINE_LOCATION perm granted", Toast.LENGTH_SHORT)
                        .show()

                    startLocation()
                } else {
                    Toast.makeText(
                        this,
                        "ACCESS_FINE_LOCATION perm NOT granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onNewLocation(location: Location) {
        saveLocation(location.longitude, location.latitude)
    }

    fun startLocation() {
        myLocationProvider = MyLocationProvider(
            this, this
        )
        myLocationProvider.startLocationMonitoring()
    }

    override fun onStop() {
        super.onStop()
        myLocationProvider.stopLocationMonitoring()
    }

    fun saveLocation(locLong: Double, locLat: Double) {
        var userCollection = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
        userCollection
            .update("locLong", locLong)
            .addOnSuccessListener { Log.d("e", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("e", "Error updating document", e) }
        userCollection
            .update("locLat", locLat)
            .addOnSuccessListener { Log.d("e", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("e", "Error updating document", e) }
    }
}
