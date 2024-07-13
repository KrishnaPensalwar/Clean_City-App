package com.example.garbagecollection

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Admin : AppCompatActivity(), QueryAdapter.OnApproveClickListener {
    private lateinit var recycler: RecyclerView
    private lateinit var queryList: MutableList<Data>
    private lateinit var queryAdapter: QueryAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        recycler = findViewById(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        queryList = mutableListOf()
        queryAdapter = QueryAdapter(queryList)
        recycler.adapter = queryAdapter

        firestore = FirebaseFirestore.getInstance()

        fetchImages()
    }

    private fun fetchImages() {
        val imagesCollectionRef = firestore.collection("queries")

        imagesCollectionRef
            .whereEqualTo("Status", "Pending")
            .get()
            .addOnSuccessListener { documents ->
                queryList.clear() // Clear the existing list to avoid duplicates
                for (document in documents) {
                    val uriList = document.get("URI") as List<Int>
                    val uriBytes = uriList.map { it.toByte() }.toByteArray()
                    val uri = "data:image/jpeg;base64," + android.util.Base64.encodeToString(uriBytes, android.util.Base64.DEFAULT)
                    val userId = document.getString("userid") ?: ""
                    val status = document.getString("Status") ?: ""
                    val query = Data(uri, userId, status)
                    queryList.add(query)
                }
                queryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("Admin_Activity", "Error getting documents: ", exception)
                Toast.makeText(this, "Failed to load images", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onApproveClick(query: Data) {
        // Update Firestore document status
        val userDocRef = firestore.collection("users").document(query.userid)
        val imagesCollectionRef = userDocRef.collection("images")

        imagesCollectionRef
            // Assuming documentId is stored in Query or fetched earlier
            .whereEqualTo("Status", "Approved")
            .get()
            .addOnSuccessListener {
                Log.d("Admin_Activity", "Status updated successfully")

                // Remove item from RecyclerView
                queryList.remove(query)
                queryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("Admin_Activity", "Error updating status", e)
            }
    }



}