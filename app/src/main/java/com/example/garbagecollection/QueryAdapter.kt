package com.example.garbagecollection

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class QueryAdapter(private val queryList: List<Data>) :
    RecyclerView.Adapter<QueryAdapter.QueryViewHolder>() {

    interface OnApproveClickListener {
        fun onApproveClick(query: Data)
    }

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("queries")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_query, parent, false)
        return QueryViewHolder(view)
    }

    override fun onBindViewHolder(holder: QueryViewHolder, position: Int) {
        val query = queryList[position]
        holder.bind(query)
        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("users").document(query.userid)
        val imagesCollectionRef = userDocRef.collection("images")

        Glide.with(holder.itemView.context).load(query.url).into(holder.imageView)

        holder.buttonApprove.setOnClickListener {
            // Handle approval
            imagesCollectionRef
                .get()
                .addOnSuccessListener { snapshots ->
                    for (document in snapshots.documents) {
                        // Update the "Status" field for each document in the collection
                        imagesCollectionRef.document(document.id).update("Status", "Approved")
                            .addOnSuccessListener {
                                Log.d("Admin_Activity", "Status updated successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Admin_Activity", "Error updating status", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Admin_Activity", "Error fetching images collection", e)
                }


            addRewardPoints(query.userid)
            Log.d("Approved", "Image Approved ")

        }

        holder.buttonDecline.setOnClickListener {

            databaseReference.child(query.userid).child("Status").setValue("declined")
            imagesCollectionRef
                .get()
                .addOnSuccessListener { snapshots ->
                    for (document in snapshots.documents) {
                        // Update the "Status" field for each document in the collection
                        imagesCollectionRef.document(document.id).update("Status", "Declined")
                            .addOnSuccessListener {
                                Log.d("Admin_Activity", "Status updated successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Admin_Activity", "Error updating status", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Admin_Activity", "Error fetching images collection", e)
                }
        }
    }

    override fun getItemCount(): Int {
        return queryList.size
    }

    class QueryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textViewUserId: TextView = itemView.findViewById(R.id.userid)
        val buttonApprove: Button = itemView.findViewById(R.id.approve)
        val buttonDecline: Button = itemView.findViewById(R.id.decline)

        fun bind(query: Data) {
            textViewUserId.text = query.userid

        }
    }

    fun addRewardPoints(userId:String) {
        // Get a reference to the Firestore database
        val pointsToAdd :Long= 5
        val firestore = FirebaseFirestore.getInstance()

        // Reference to the user's document
        val userDocRef: DocumentReference? = userId?.let { firestore.collection("users").document(it) }

        // Update the 'rewardPoints' field by incrementing it by the specified points
        if (userDocRef != null) {
            userDocRef.update("Reward_Points", FieldValue.increment(pointsToAdd))
                .addOnSuccessListener {
                    // Handle success
                    Log.d("AddRewardPoints", "Reward points successfully updated!")
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    Log.w("AddRewardPoints", "Error updating reward points", e)
                }
        }
    }
}


