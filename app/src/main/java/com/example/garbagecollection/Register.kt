package com.example.garbagecollection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {

    private lateinit var username_register:EditText
    private lateinit var password_register:EditText
    private lateinit var cpassword_register:EditText
    private lateinit var email_register:EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private  lateinit var register: Button
    private lateinit var login: Button
    private lateinit var firebasefireStore:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        username_register = findViewById(R.id.register_page_username)
        password_register = findViewById(R.id.register_page_password)
        cpassword_register = findViewById(R.id.register_page_confirmpasswordr)
        email_register = findViewById(R.id.register_page_email)
        register = findViewById(R.id.register_button)
        login = findViewById(R.id.login_button)
        firebaseAuth = FirebaseAuth.getInstance()
        firebasefireStore = FirebaseFirestore.getInstance()


        register.setOnClickListener {
            register_user()
        }

        login.setOnClickListener{
            login_user()
        }

    }

    private fun login_user() {
        val intent = Intent(this,Login::class.java)
        startActivity(intent)
        finish()
    }

    private fun register_user() {

        val uname = username_register.text.toString()
        val pass = password_register.text.toString()
        val cpass = cpassword_register.text.toString()
        val email = email_register.text.toString()

        if(uname.equals("")){
            Toast.makeText(this, "Username should not be empty", Toast.LENGTH_SHORT).show()
        }
        else if(pass.equals("")){
            Toast.makeText(this, "Password should not be empty", Toast.LENGTH_SHORT).show()
        }
        else if(cpass.equals("")){
            Toast.makeText(this, "Confirm Password should not be empty", Toast.LENGTH_SHORT).show()
        }
        else if(email.equals("")){
            Toast.makeText(this, "Email should not be empty", Toast.LENGTH_SHORT).show()
        }

        else if(pass != cpass){
            Toast.makeText(this, "Password and confirm password do not match.", Toast.LENGTH_SHORT).show()
        }
        else{
            firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, "User created succesfully", Toast.LENGTH_SHORT).show()
                    val userid = firebaseAuth.currentUser?.uid
                    val documentref: DocumentReference? =userid?.let { it1 -> firebasefireStore.collection("users").document(it1) }
                    val myMap = mutableMapOf<String, Any>()
                    myMap.put("Name",uname)
                    myMap.put("Email",email)
                    myMap.put("Reward_Points",0)
                    if (documentref != null) {
                        documentref.set(myMap).addOnSuccessListener {
                            Log.d("Success", "User created with ID "+userid)
                        }
                        documentref.set(myMap).addOnFailureListener {
                            Log.d("Failure", "User created with ID "+it.toString())
                        }
                    }

                    val intent = Intent(this,Home::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this, "Something Went Wrong !!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}