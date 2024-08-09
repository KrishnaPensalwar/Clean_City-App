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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class QueryAdapter(
    private val queryList: List<Data>,
    private val approveClickListener: OnApproveClickListener
) : RecyclerView.Adapter<QueryAdapter.QueryViewHolder>() {

    interface OnApproveClickListener {
        fun onApproveClick(query: Data)
    }

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_query, parent, false)
        return QueryViewHolder(view)
    }

    override fun onBindViewHolder(holder: QueryViewHolder, position: Int) {
        val query = queryList[position]
        holder.bind(query)

        Glide.with(holder.itemView.context).load(query.uri).into(holder.imageView)

        holder.buttonApprove.setOnClickListener {
            approveClickListener.onApproveClick(query)
            // You can add additional logic here if needed, but approval is handled by the listener
        }

        holder.buttonDecline.setOnClickListener {
            // Handle decline
            val queryDocRef = firestore.collection("queries").document(query.documentId)

            queryDocRef.delete()
                .addOnSuccessListener {
                    Log.d("QueryAdapter", "Document successfully deleted!")
                    Toast.makeText(holder.itemView.context, "Query declined and deleted", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.w("QueryAdapter", "Error deleting document", e)
                    Toast.makeText(holder.itemView.context, "Failed to decline query", Toast.LENGTH_SHORT).show()
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

    fun addRewardPoints(userId: String) {
        val pointsToAdd: Long = 5
        val userDocRef: DocumentReference = FirebaseFirestore.getInstance().collection("users").document(userId)

        userDocRef.update("Reward_Points", FieldValue.increment(pointsToAdd))
            .addOnSuccessListener {
                Log.d("AddRewardPoints", "Reward points successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("AddRewardPoints", "Error updating reward points", e)
            }
    }
}
