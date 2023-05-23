package com.example.palliativecareapp.authentication

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.palliativecareapp.R
import com.example.palliativecareapp.patient.PatientHome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.patient_register.*
import java.util.*


class RegisterPatient : AppCompatActivity() {
    lateinit var sharedPreferences : SharedPreferences
    lateinit var date : String
    private lateinit var auth: FirebaseAuth
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI(currentUser)
        }
    }
    private var datePickerDialog: DatePickerDialog? = null
    private var dateButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_register)
        sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        initDatePicker();
        dateButton = findViewById(R.id.datePickerBtn);
        dateButton!!.setText(getTodaysDate());

        auth = Firebase.auth
        val email = email.text
        val password = password.text
        val confirmPassword = confirmPassword.text
        buttonSignUp.setOnClickListener {
            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if (password.toString().equals(confirmPassword.toString())) {
                    Log.e("user email", email.toString())
                    Log.e("user password", password.toString())
                    Log.e("user confirm password", confirmPassword.toString())
                    createNewAccount(email.toString(), password.toString())
                }
                else{
                    Toast.makeText(baseContext, "Password not Match.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(baseContext, "Register Failed. Please enter the EMPTY Fields .",
                    Toast.LENGTH_SHORT).show()
            }

        }
        loginSignUp.setOnClickListener {
            val i = Intent(this, LoginPatient::class.java)
            startActivity(i)
        }
    }

    private fun createNewAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("User:createUser", "createUserWithEmail:success")
                    val user = auth.currentUser

                    // save user info
                    val db = Firebase.firestore
                    val userRef = db.collection("patients").document(user!!.uid)

                    var first = first.text.toString()
                    var middle = middle.text.toString()
                    var last = last.text.toString()

                    val userInfo = hashMapOf(
                        "name" to "$first $middle $last",
                        "address" to address.text.toString(),
                        "phone" to phone.text.toString(),
                        "birth" to date,
                        "email" to user.email.toString(),
                        "password" to password.toString(),
                        "image" to "",
                    )

                    userRef.set(userInfo)
                        .addOnSuccessListener {
                            Log.e("user info", "Saved successfully")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("user info", "Failed")
                        }

                    val editor = sharedPreferences.edit()
                    editor.putString("patient", "register")
                    editor.apply()

                    updateUI(user)
                    Toast.makeText(baseContext, "Authentication Success.",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Log.w("User:createUser", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        val patient = sharedPreferences.getString("patient", "error")
        if(patient!!.equals("login")) {
            val editor = sharedPreferences.edit()
            editor.putString("userEmail", user!!.email)
            editor.putString("userId", user.uid)
            editor.apply()
            var i = Intent(this, PatientHome::class.java)
            i.putExtra("email",user!!.email)
            i.putExtra("id",user.uid)
            startActivity(i)
        }
    }

    private fun getTodaysDate(): String? {
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        var month: Int = cal.get(Calendar.MONTH)
        month = month + 1
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year)
    }

    private fun initDatePicker() {
        val dateSetListener =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month = month + 1
                date = makeDateString(day, month, year)!!
                dateButton!!.setText(date)
            }
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        val style: Int = AlertDialog.THEME_HOLO_LIGHT
        datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String? {
        return getMonthFormat(month) + " " + day + " " + year
    }

    private fun getMonthFormat(month: Int): String {
        if (month == 1) return "JAN"
        if (month == 2) return "FEB"
        if (month == 3) return "MAR"
        if (month == 4) return "APR"
        if (month == 5) return "MAY"
        if (month == 6) return "JUN"
        if (month == 7) return "JUL"
        if (month == 8) return "AUG"
        if (month == 9) return "SEP"
        if (month == 10) return "OCT"
        if (month == 11) return "NOV"
        return if (month == 12) "DEC" else "JAN"

        //default should never happen
    }

    fun openDatePicker(view: View?) {
        datePickerDialog!!.show()
    }
}