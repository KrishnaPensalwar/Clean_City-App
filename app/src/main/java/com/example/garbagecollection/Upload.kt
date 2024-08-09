package com.example.garbagecollection

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class Upload : AppCompatActivity() {

    private lateinit var Home_btn: ImageButton
    private lateinit var Upload_btn: ImageButton
    private lateinit var History_btn: ImageButton
    private lateinit var Click: Button
    private lateinit var uploadphoto: Button
    private lateinit var viewImage: ImageView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var desc: EditText

    private var imageBytes: ByteArray? = null
    val IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }

        Home_btn = findViewById(R.id.Home_btn)
        Upload_btn = findViewById(R.id.Upload_btn)
        History_btn = findViewById(R.id.History_btn)
        Click = findViewById(R.id.Click_Photo)
        viewImage = findViewById(R.id.view_Image)
        uploadphoto = findViewById(R.id.upload)
        desc = findViewById(R.id.detailedaddressinput)

        Home_btn.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        Upload_btn.setOnClickListener {
            val intent = Intent(this, Upload::class.java)
            startActivity(intent)
            finish()
        }

        History_btn.setOnClickListener {
            val intent = Intent(this, History::class.java)
            startActivity(intent)
            finish()
        }

        Click.isEnabled = false

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 111)
        } else {
            Click.isEnabled = true
        }

        Click.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, IMAGE_REQUEST)
            } else {
                Toast.makeText(this, "No camera application found.", Toast.LENGTH_SHORT).show()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100
            )
        }

        uploadphoto.setOnClickListener {
            if (imageBytes != null) {
                uploadImage(imageBytes!!)
            } else {
                Toast.makeText(this, "No image captured", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            val bitmap = extras?.get("data") as? Bitmap

            if (bitmap != null) {
                viewImage.setImageBitmap(bitmap)

                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                imageBytes = byteArrayOutputStream.toByteArray()
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchLocation(callback: (Pair<Double, Double>) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Location permissions are required", Toast.LENGTH_SHORT).show()
            callback(Pair(0.0, 0.0))
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    callback(Pair(latitude, longitude))
                } else {
                    Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
                    callback(Pair(0.0, 0.0))
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                callback(Pair(0.0, 0.0))
            }
    }

    private fun uploadImage(imageBytes: ByteArray) {
        val userid = FirebaseAuth.getInstance().currentUser?.uid

        if (userid != null) {
            val firestore = FirebaseFirestore.getInstance()
            val userDocRef = firestore.collection("users").document(userid)

            val imagesCollectionRef = userDocRef.collection("images")
            val queriesRef = firestore.collection("queries")

            // Create a new document in the images subcollection with a unique ID
            val newImageDocRef = imagesCollectionRef.document()
            val queryDocRef = queriesRef.document()

            val des = desc.text.toString()

            fetchLocation { coordinates ->
                val latitude = coordinates.first
                val longitude = coordinates.second

                val imageMap = mutableMapOf<String, Any>()
                imageMap["URI"] = imageBytes.map { it.toInt() }
                imageMap["Latitude"] = latitude
                imageMap["Longitude"] = longitude
                imageMap["Description"] = des
                imageMap["Status"] = "Pending"

                val queryMap = mutableMapOf<String, Any>()
                queryMap["userid"] = userid
                queryMap["URI"] = imageBytes.map { it.toInt() }
                queryMap["Latitude"] = latitude
                queryMap["Longitude"] = longitude
                queryMap["Description"] = des
                queryMap["Status"] = "Pending"

                newImageDocRef.set(imageMap)
                    .addOnSuccessListener {
                        viewImage.setImageBitmap(null)
                        desc.setText("")
                        Toast.makeText(this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show()
                        Log.d("Success", "Image metadata uploaded for user $userid")
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to upload image : ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("Failure", "Failed to upload image metadata: ${e.message}")
                    }

                queryDocRef.set(queryMap)
                    .addOnSuccessListener {
                        Log.d("Queries", "Image metadata updated in queries collection.")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Error Query", "Failed to update status: ${e.message}")
                    }
            }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}
