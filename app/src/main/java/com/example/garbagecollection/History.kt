package com.example.garbagecollection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class History : AppCompatActivity() {

    private lateinit var Home_btn: ImageButton
    private lateinit var Upload_btn: ImageButton
    private lateinit var History_btn : ImageButton
    private lateinit var recyclerView : RecyclerView
    private lateinit var imageAdapter : ImagesAdapter
    private val imagesList = mutableListOf<ImageData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        Home_btn=findViewById(R.id.Home_btn)

        Home_btn.setOnClickListener {
            val intent =  Intent(this,Home::class.java)
            startActivity(intent)
            finish()
        }
        Upload_btn=findViewById(R.id.Upload_btn)

        Upload_btn.setOnClickListener {
            val intent =  Intent(this,Upload::class.java)
            startActivity(intent)
            finish()
        }
        History_btn=findViewById(R.id.History_btn)

        History_btn.setOnClickListener {
            val intent =  Intent(this,History::class.java)
            startActivity(intent)
            finish()
        }

        recyclerView = findViewById(R.id.recycler_view)
        imageAdapter = ImagesAdapter(imagesList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = imageAdapter

        fetchImages()

    }

    private fun fetchImages() {
        val userid = FirebaseAuth.getInstance().currentUser?.uid
        if (userid != null) {
            val firestore = FirebaseFirestore.getInstance()
            val userDocRef = firestore.collection("users").document(userid)
            val imagesCollectionRef = userDocRef.collection("images")

            imagesCollectionRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val uriList = document.get("URI") as List<Int>
                        val uriBytes = uriList.map { it.toByte() }.toByteArray()
                        val uri = "data:image/jpeg;base64," + android.util.Base64.encodeToString(uriBytes, android.util.Base64.DEFAULT)
                        val latitude = document.getDouble("Latitude") ?: 0.0
                        val longitude = document.getDouble("Longitude") ?: 0.0
                        val description = document.getString("Description") ?: ""
                        val status = document.getString("Status") ?:""
                        val imageData = ImageData(uri, latitude, longitude, description,status)
                        imagesList.add(imageData)
                    }
                    imageAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.w("History", "Error getting documents: ", exception)
                    Toast.makeText(this, "Failed to load images", Toast.LENGTH_SHORT).show()
                }
        }
    }



}
