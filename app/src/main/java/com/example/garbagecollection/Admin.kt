package com.example.garbagecollection

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        queryAdapter = QueryAdapter(queryList, this) // Pass 'this' for click listener
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
                    val query = Data(uri, userId, status, document.id) // Include document ID
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
        // Change the status of the Firestore document to "Approved"
        val queryDocRef = firestore.collection("queries").document(query.documentId)

        queryDocRef.update("Status", "Approved")
            .addOnSuccessListener {
                Log.d("Admin_Activity", "Document status successfully updated to 'Approved'!")

                // Optionally, you can also add reward points here
                // addRewardPoints(query.userid)

                // Remove item from RecyclerView
                queryList.remove(query)
                queryAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Item approved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Admin_Activity", "Error updating document status", e)
                Toast.makeText(this, "Failed to approve item", Toast.LENGTH_SHORT).show()
            }
    }

    // Optionally, you can add this function to handle declined items
    fun onDeclineClick(query: Data) {
        // Change the status of the Firestore document to "Declined"
        val queryDocRef = firestore.collection("queries").document(query.documentId)

        queryDocRef.update("Status", "Declined")
            .addOnSuccessListener {
                Log.d("Admin_Activity", "Document status successfully updated to 'Declined'!")

                // Remove item from RecyclerView
                queryList.remove(query)
                queryAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Item declined", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Admin_Activity", "Error updating document status", e)
                Toast.makeText(this, "Failed to decline item", Toast.LENGTH_SHORT).show()
            }
    }

}
