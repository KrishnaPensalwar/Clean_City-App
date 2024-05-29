package com.example.garbagecollection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class login : AppCompatActivity() {

    private lateinit var Login :Button
    private lateinit var Registerbtn :Button
    private lateinit var uname:EditText
    private lateinit var pword:EditText
    private lateinit var errorusername:TextView
    private lateinit var errorpassword:TextView
    private lateinit var db:DbHandler
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database:DatabaseReference
    private lateinit var forgotpassword:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Login=findViewById(R.id.button3)
        Registerbtn=findViewById(R.id.button4)
        uname=findViewById(R.id.editTextText4)
        pword=findViewById(R.id.editTextText5)
        errorusername=findViewById(R.id.errorusername)
        errorpassword=findViewById(R.id.errorpassword)
        db= DbHandler(this)
        forgotpassword=findViewById(R.id.forgotpassword)

        Login.setOnClickListener {
            val usern=uname.text.toString()
            val userp=pword.text.toString()
            try {
                if (usern.isNotEmpty() && userp.isNotEmpty()) {

//                    IF SQLITE IS USED

//                    val checkuser = db.checkusernamepassword(usern, userp)
//                    if (checkuser) {
//                        Toast.makeText(this, "Login Successful !!!", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this, Home::class.java)
//                        startActivity(intent)
//                        finish()

//                    firebaseAuth=FirebaseAuth.getInstance()
//                    database=FirebaseDatabase.getInstance().getReference("user_pass")
//
//                    database.child(usern).get().addOnSuccessListener {
//                        if(it.exists()){
//                            val name=it.child("name").value.toString()
//                            val pass=it.child("password").toString()
//
//                        }
//                    }

                    checkUser()

                    firebaseAuth.signInWithEmailAndPassword(usern,userp).addOnCompleteListener{
                        if(it.isSuccessful){
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            errorusername.text="Invalid Credentials!!!"
                            errorpassword.text="Invalid Credentials!!!"
                        }
                    }

                } else {
                    if(usern.isEmpty()){
                        errorusername.text="Please Enter Username!!!"
                    }
                    if(userp.isEmpty()){
                        pword.error="Please Enter password"
                    }
                }
            }catch(e:Exception){
                Log.e("Error",e.printStackTrace().toString())
            }
        }
        Registerbtn.setOnClickListener {
            val intent=Intent(this,Register::class.java)
            startActivity(intent)
            finish()
        }
        forgotpassword.setOnClickListener {
            val usern=uname.text.toString()
            if(usern.isEmpty()){
                Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()
            }else{
                firebaseAuth.sendPasswordResetEmail(usern).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this, "Password Link send to your email id.", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }
    fun checkUser() {
        val userUsername = uname.text.toString().trim()
        val userPassword = pword.text.toString().trim()

        val reference = FirebaseDatabase.getInstance().getReference("user_pass")
        val checkUserDatabase = reference.orderByChild("username").equalTo(userUsername)

        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    uname.error = null
                    val passwordFromDB = snapshot.child(userUsername).child("password").getValue(String::class.java)

                    if (passwordFromDB == userPassword) {
                        uname.error = null

                        val nameFromDB = snapshot.child(userUsername).child("name").getValue(String::class.java)
                        val emailFromDB = snapshot.child(userUsername).child("email").getValue(String::class.java)
                        val usernameFromDB = snapshot.child(userUsername).child("username").getValue(String::class.java)

                        val intent = Intent(this@login, MainActivity::class.java)

                        intent.putExtra("name", nameFromDB)
                        intent.putExtra("email", emailFromDB)
                        intent.putExtra("username", usernameFromDB)
                        intent.putExtra("password", passwordFromDB)

                        startActivity(intent)
                    } else {
                        pword.setError("Invalid Credentials")
                        pword.requestFocus()
                    }
                } else {
                    uname.setError("User does not exist")
                    pword.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }


}