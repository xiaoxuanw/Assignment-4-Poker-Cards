package com.example.cse438.cse438_assignment4

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    //Default values
    private var defaultWins:Int = 0;
    private var defaultLosses:Int = 0;
    private var defaultChips:Int = 100;

    //A firebase authentication
    private lateinit var auth: FirebaseAuth
    private lateinit var userName: String
    //From layout
    private lateinit var signupButton: Button
    private lateinit var signupEmail: EditText
    private lateinit var signupPassword: EditText
    private lateinit var signupLoginButton: Button

    //onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //Initialize the firebase instance
        auth = FirebaseAuth.getInstance()

        //Define the late inits
        signupButton = signup_button
        signupEmail = signup_email
        signupPassword = signup_pswd
        signupLoginButton = signup_login_button

        //set onclick listener
        signupButton.setOnClickListener {
            //set the text to string
            var email: String = signupEmail.text.toString()
            var password: String = signupPassword.text.toString()
            userName = email.substring(0,email.indexOf('@'))

            val db = FirebaseFirestore.getInstance()
            val userData = HashMap<String, Any>()
            userData["email"] = email
            userData["username"] = userName
            userData["wins"] = defaultWins
            userData["losses"] = defaultLosses
            userData["chips"] = defaultChips
            //Check if the fields are empty
            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener{ task ->
                    if(task.isSuccessful){
                        //Store the values to the databse
                        db.document("players/${auth?.currentUser?.uid}")
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Player Created", Toast.LENGTH_LONG).show()
                                val intent = Intent(this, SigninActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to write player data", Toast.LENGTH_SHORT).show()
                            }
                    }else {
                        Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }//click listener

        //set onclick listener
        signupLoginButton.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }
    }//onCreate
}