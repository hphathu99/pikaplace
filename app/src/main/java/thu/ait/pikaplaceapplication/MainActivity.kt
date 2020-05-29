package thu.ait.pikaplaceapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.firestore.FirebaseFirestore
import thu.ait.pikaplaceapplication.data.User


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginClick (v: View) {
        if (!isFormValid()) {
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            etEmail.text.toString(), etPassword.text.toString()
        ).addOnSuccessListener {
            startActivity(Intent(this@MainActivity, CurLocActivity::class.java))
        }.addOnFailureListener{
            Toast.makeText(this@MainActivity,
                "Login error: ${it.message}",
                Toast.LENGTH_LONG).show()
        }
    }

    fun registerClick (v: View) {
        if (!isFormValid()){
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            etEmail.text.toString(), etPassword.text.toString()
        ).addOnSuccessListener {
            Toast.makeText(this@MainActivity,
                "Registrasion OK",
                Toast.LENGTH_LONG).show()
            addUserToDb()
        }.addOnFailureListener{
            Toast.makeText(this@MainActivity,
                "Error: ${it.message}",
                Toast.LENGTH_LONG).show()
        }


    }

    fun addUserToDb() {
        var userCollection = FirebaseFirestore.getInstance().collection(
            "users")

        val user = User(
            FirebaseAuth.getInstance().currentUser!!.uid,
            FirebaseAuth.getInstance().currentUser!!.email!!,
            null,
            null,
            null
        )


        userCollection.document(FirebaseAuth.getInstance().currentUser!!.uid).set(user)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity,
                    "User SAVED", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{
                Toast.makeText(this@MainActivity,
                    "Error ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    fun isFormValid(): Boolean {
        return when {
            etEmail.text.isEmpty() -> {
                etEmail.error = "This field can not be empty"
                false
            }
            etPassword.text.isEmpty() -> {
                etPassword.error = "The password can not be empty"
                false
            }
            else -> true
        }
    }
}
