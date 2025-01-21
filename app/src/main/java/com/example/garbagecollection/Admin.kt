package com.example.garbagecollection

import QueryAdapter
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
        queryAdapter = QueryAdapter(queryList, this, this) // Pass both listeners
        recycler.adapter = queryAdapter

        firestore = FirebaseFirestore.getInstance()

        fetchImages()
    }

    private fun fetchImages() {
        val imagesCollectionRef = firestore.collection("queries")

        imagesCollectionRef
            .whereEqualTo("Status", "Pending") // Fetch only "Pending" queries
            .get()
            .addOnSuccessListener { documents ->
                queryList.clear() // Clear the existing list to avoid duplicates
                for (document in documents) {
                    // Extract all fields with proper null checks
                    val description = document.getString("Description") ?: "No description"
                    val latitude = document.getDouble("Latitude") ?: 0.0
                    val longitude = document.getDouble("Longitude") ?: 0.0
                    val status = document.getString("Status") ?: "Pending"
                    val uriList = document.get("URI") as? List<Int> ?: emptyList()
                    val uriBytes = uriList.map { it.toByte() }.toByteArray()
                    val uri = "data:image/jpeg;base64," + android.util.Base64.encodeToString(uriBytes, android.util.Base64.DEFAULT)
                    val userId = document.getString("userid") ?: ""

                    // Create a query object with all fields
                    val query = Data(
                        uri = uri,
                        userId = userId,
                        status = status,
                        documentId = document.id,
                        description = description,
                        latitude = latitude,
                        longitude = longitude
                    )
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

                // Update reward points for the user
                addRewardPoints(query.userId)

                // Update the status in the query list locally
                val updatedQuery = query.copy(status = "Approved")
                val index = queryList.indexOf(query)
                queryList[index] = updatedQuery
                queryAdapter.notifyItemChanged(index) // Notify that specific item was changed

                // Now update the image status in the user's 'images' collection
                updateImageStatusInUserCollection(query.userId, query.documentId)

                Toast.makeText(this, "Item approved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Admin_Activity", "Error updating document status", e)
                Toast.makeText(this, "Failed to approve item", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateImageStatusInUserCollection(userId: String, queryDocId: String) {
        val userImagesRef = firestore.collection("users")
            .document(userId)
            .collection("images")

        // Log the queryDocId and userId for debugging
        Log.d("Debug", "Searching for queryDocId: $queryDocId in userId: $userId")

        userImagesRef.whereEqualTo("queryDocId", queryDocId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        Log.d("Debug", "Updating image document: ${document.id}")
                        userImagesRef.document(document.id)
                            .update("Status", "Approved")
                            .addOnSuccessListener {
                                Log.d("Admin_Activity", "Image status successfully updated to 'Approved'!")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Admin_Activity", "Error updating image status", e)
                            }
                    }
                } else {
                    // Log error if no matching document is found
                    Log.e(
                        "Admin_Activity",
                        "No image found for queryDocId: $queryDocId in userId: $userId"
                    )
                }
            }
            .addOnFailureListener { e ->
                Log.e("Admin_Activity", "Error fetching user's images collection", e)
            }
    }

    private fun addRewardPoints(userId: String) {
        // Assume the user data is in the "users" collection
        val userDocRef = firestore.collection("users").document(userId)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentPoints = document.getLong("Reward_Points") ?: 0L
                    val updatedPoints = currentPoints + 5

                    // Update the reward points in the user's document
                    userDocRef.update("Reward_Points", updatedPoints)
                        .addOnSuccessListener {
                            Log.d("Admin_Activity", "Reward points successfully updated!")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Admin_Activity", "Error updating reward points", e)
                        }

                } else {
                    Log.e("Admin_Activity", "User document does not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Admin_Activity", "Error getting user document", e)
            }
    }

    private fun updateImageStatusInUserCollectionDecline(userId: String, queryDocId: String) {
        val userImagesRef = firestore.collection("users")
            .document(userId)
            .collection("images")

        // Log the queryDocId and userId for debugging
        Log.d("Debug", "Searching for queryDocId: $queryDocId in userId: $userId")

        userImagesRef.whereEqualTo("queryDocId", queryDocId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        Log.d("Debug", "Updating image document: ${document.id}")
                        userImagesRef.document(document.id)
                            .update("Status", "Declined")
                            .addOnSuccessListener {
                                Log.d("Admin_Activity", "Image status successfully updated to 'Approved'!")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Admin_Activity", "Error updating image status", e)
                            }
                    }
                } else {
                    // Log error if no matching document is found
                    Log.e(
                        "Admin_Activity",
                        "No image found for queryDocId: $queryDocId in userId: $userId"
                    )
                }
            }
            .addOnFailureListener { e ->
                Log.e("Admin_Activity", "Error fetching user's images collection", e)
            }
    }

    // Handle declined items
    fun onDeclineClick(query: Data) {
        // Change the status of the Firestore document to "Declined"
        val queryDocRef = firestore.collection("queries").document(query.documentId)

        queryDocRef.update("Status", "Declined")
            .addOnSuccessListener {
                Log.d("Admin_Activity", "Document status successfully updated to 'Declined'!")

                // Update the status in the query list locally
                val updatedQuery = query.copy(status = "Declined") // Update status locally
                val index = queryList.indexOf(query)
                queryList[index] = updatedQuery
                queryAdapter.notifyItemChanged(index) // Notify that specific item was changed

                // Now update the image status in the user's 'images' collection
                updateImageStatusInUserCollectionDecline(query.userId, query.documentId)

                Toast.makeText(this, "Item declined", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Admin_Activity", "Error updating document status", e)
                Toast.makeText(this, "Failed to decline item", Toast.LENGTH_SHORT).show()
            }
    }
}
