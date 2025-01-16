package com.example.garbagecollection

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var email_login: EditText
    private lateinit var password_login: EditText
    private lateinit var login: Button
    private lateinit var register: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var resetpassword: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email_login = findViewById(R.id.login_page_email)
        password_login = findViewById(R.id.login_page_password)
        login = findViewById(R.id.button_login)
        register = findViewById(R.id.button_register)
        resetpassword = findViewById(R.id.forgotpassword)

//        val login_admin = login.text.toString()
//        val pass_admin = password_login.text.toString()

//        if(login_admin == "admin123@gmail.com" && pass_admin.equals("Admin1234")){
//            val intent = Intent(this,AdminActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

        login.setOnClickListener {
            login_user()
        }

        register.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }

        resetpassword.setOnClickListener {
            resetpassword_()
        }


    }


    private fun login_user() {
        val mail = email_login.text.toString()
        val pass = password_login.text.toString()
        firebaseAuth = FirebaseAuth.getInstance()



        if (mail.equals("")) {
            Toast.makeText(this, "Please Enter email id", Toast.LENGTH_SHORT).show()
            email_login.error = "Please Enter email id"
        } else if (pass.equals("")) {
            Toast.makeText(this, "Please Enter password", Toast.LENGTH_SHORT).show()
            password_login.error = "Please Enter password"
        } else {
            if(mail == "admin123@gmail.com" && pass.equals("Admin1234")){
                val intent = Intent(this,Admin::class.java)
                startActivity(intent)
                finish()
            }

            firebaseAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener {

                if (it.isSuccessful) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    password_login.error = "Invalid Credentials"
                    email_login.error = "Invalid Credentials"
//                    Toast.makeText(this, "Login credentials do not match!!!", Toast.LENGTH_SHORT)
//                        .show()
                }
            }
        }
    }

    private fun resetpassword_() {
        val usern = email_login.text.toString()
        firebaseAuth = FirebaseAuth.getInstance()


        if (usern.isEmpty()) {
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()
        } else {
            firebaseAuth.sendPasswordResetEmail(usern).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Password Link send to your email id.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}