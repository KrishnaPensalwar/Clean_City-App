package com.example.garbagecollection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.garbagecollection.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class Register : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var mobile: EditText
    private lateinit var password: EditText
    private lateinit var confirmpassword: EditText
    private lateinit var db: DbHandler
    private lateinit var usernameerror: TextView
    private lateinit var passworderror: TextView
    private lateinit var emailerror: TextView
    private lateinit var cpassworderror: TextView
    private lateinit var user_pass:user_pass

    // Database store data
    private lateinit var database: DatabaseReference
    private lateinit var database1: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        username = findViewById(R.id.usernamer)
        mobile = findViewById(R.id.emailr)
        password = findViewById(R.id.passwordr)
        confirmpassword = findViewById(R.id.confirmpasswordr)
        usernameerror = findViewById(R.id.usernameerror)
        emailerror = findViewById(R.id.textView2)
        passworderror = findViewById(R.id.textView3)
        cpassworderror = findViewById(R.id.textView4)
        db = DbHandler(this)

        binding.button.setOnClickListener {

            cpassworderror.text = " "
            passworderror.text = ""
            emailerror.text = ""
            usernameerror.text = ""

//           IF SQLITE IS USED

//            if (username.text.toString().isNotBlank() &&
//                mobile.text.toString().isNotBlank() &&
//                password.text.toString().isNotBlank() &&
//                confirmpassword.text.toString().isNotBlank()
//            ) {
//                try {
//
//                    val uname=username.text.toString()
//                    val pass=password.text.toString()
//                    val cpass=confirmpassword.text.toString()
//                    val mob = mobile.text.toString()
//
//                   if (pass.equals(cpass)) {
////                       Toast.makeText(this, "12334", Toast.LENGTH_SHORT).show()
////                       val user=User(0,uname,mob,pass)
////                       if ( db.insertuser(user)) {
////                           Toast.makeText(this, "User added successfully!!!!", Toast.LENGTH_SHORT).show()
////                           val intent = Intent(this, login::class.java)
////                           startActivity(intent)
////                           finish()
////                       } else {
////                           Toast.makeText(this, "User Already Exists!!!", Toast.LENGTH_SHORT).show()
////                       }
//
//
//                   } else {
//                       cpassworderror.text=" Password and Confirm Password Do not match!!"
//                       passworderror.text=" Password and Confirm Password Do not match!!"
//                       Toast.makeText(this, "Password and confirm password do not match!!!", Toast.LENGTH_SHORT).show()
//                   }
//               }
//                catch(e:Exception){
////                    Toast.makeText(this, "Something unusual happened!!!!", Toast.LENGTH_SHORT).show()
//                    Log.e("error", e.printStackTrace().toString())
//                }
//            }
//                else {
//                    if(username.text.toString().isBlank()){
//                        usernameerror.text="Please Enter Username!! "
//                    }
//                if(mobile.text.toString().isBlank()){
//                        emailerror.text="Please Enter Email!! "
//                    }
//                if(password.text.toString().isBlank()){
//                        passworderror.text="Please Enter Password!! "
//                    }
//                if(confirmpassword.text.toString().isBlank()){
//                        cpassworderror.text="Please Re-Enter Password !! "
//                    }
//
////                Toast.makeText(this, "Please Fill all the Details", Toast.LENGTH_SHORT).show()
//            }
//        }

            firebaseAuth = FirebaseAuth.getInstance()
            database = FirebaseDatabase.getInstance().getReference("users")

            binding.button.setOnClickListener {
                registeruser()
            }
            binding.button2.setOnClickListener {
                val intent = Intent(this, login::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun registeruser() {
        val username = binding.usernamer.text.toString()
        val pass = binding.passwordr.text.toString()
        val cpass = binding.confirmpasswordr.text.toString()
        val email = binding.emailr.text.toString()

        if (username.isNotEmpty() && pass.isNotEmpty() && cpass.isNotEmpty() && email.isNotEmpty()) {
            if (pass == cpass) {

                        database = FirebaseDatabase.getInstance().getReference("users")
                        database1 = FirebaseDatabase.getInstance().getReference("user_pass")

                        val userandpass=user_pass(username,pass)
                        val User = users_firebase(username, email, 0)

                        database1.child(username).setValue(userandpass).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("Success", "Succesfully inserted data in database")
                            } else {
                                Toast.makeText(
                                    this,
                                    it.exception.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()

                                Log.e("error","Error in inserting data in database."+it.exception.toString())
                            }
                        }

                        database.child(username).setValue(User).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("Success", "Succesfully inserted data in database")
                            } else {
                                Toast.makeText(
                                    this,
                                    it.exception.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()

                                Log.e("error","Error in inserting data in database."+it.exception.toString())
                            }
                        }
                        // Example: Navigate to another activity
                        val intent = Intent(this, login::class.java)
                        startActivity(intent)
                        finish()

            } else {
                Toast.makeText(this,"Password and confirm password don't match",Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill all the details.", Toast.LENGTH_SHORT).show()
        }
    }
}