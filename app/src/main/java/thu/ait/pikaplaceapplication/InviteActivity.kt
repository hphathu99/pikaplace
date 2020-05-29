package thu.ait.pikaplaceapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_invite.*
import java.util.*
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import thu.ait.pikaplaceapplication.data.Group
import com.google.firebase.firestore.GeoPoint


class InviteActivity : AppCompatActivity() {

    var gName = ""
    var gDay = ""
    var  gTime = ""
    var type = ""
    private var members = mutableListOf<String>()
    private var memLocations = mutableListOf<GeoPoint>()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)

        val suggestions = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var email = document.data["email"].toString()
                    suggestions.add(email)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@InviteActivity,
                    "Error ${exception.message}", Toast.LENGTH_LONG).show()
            }

        nacho_text_view.setAdapter(adapter)

        btnAdd.setOnClickListener {
            members = nacho_text_view.chipValues
            members.add(FirebaseAuth.getInstance().currentUser!!.email)
            for (chipVal in nacho_text_view.chipValues) {
                db.collection("users").whereEqualTo("email",chipVal).get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            var lon = document.data["locLong"] as Double
                            var lat = document.data["locLat"] as Double
                            memLocations.add(GeoPoint(lat, lon))
                        }
                }.addOnFailureListener {
                        Toast.makeText(this@InviteActivity,
                            "Error", Toast.LENGTH_LONG).show()
                    }
            }

        }

        btnCreate.setOnClickListener {
            gName = etName.text.toString()
            this.type = spinType.selectedItem.toString()
            addGroup()
            var mapIntent = Intent(this@InviteActivity, MapActivity2::class.java)
            mapIntent.putExtra("CurGroup", gName)
            var myList = ArrayList<String>()
            myList.addAll(members)
            mapIntent.putExtra("Members",myList)
            startActivity(mapIntent)
        }

    }

    fun showDatePickerDialog(v: View) {
        val c = Calendar.getInstance()
        var mYear = c.get(Calendar.YEAR)
        var mMonth = c.get(Calendar.MONTH)
        var mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                this.gDay = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                tvDate.setText(gDay)
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }

    fun showTimePickerDialog(v: View) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val time = c.get(Calendar.AM_PM)
        val timePickerDialog = TimePickerDialog(this,
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                this.gTime = hour.toString() + ":" + minute.toString()
                tvTime.setText(gTime)
            }, hour, minute, true
        )
        timePickerDialog.show()
    }

    fun addGroup() {
        var group = Group (
            gName,
            members,
            gDay,
            gTime,
            type,
            null,
            memLocations
        )

        var groupCollection = FirebaseFirestore.getInstance().collection(
            "groups")

        var ref = groupCollection.add(group)
            .addOnSuccessListener {
                Toast.makeText(this@InviteActivity,
                    "User SAVED", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{
                Toast.makeText(this@InviteActivity,
                    "Error ${it.message}", Toast.LENGTH_LONG).show()
            }

    }
}
