import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.garbagecollection.Admin
import com.example.garbagecollection.Data
import com.example.garbagecollection.R
import com.google.firebase.firestore.FirebaseFirestore

class QueryAdapter(
    private val queryList: MutableList<Data>,
    private val approveClickListener: OnApproveClickListener,
    private val declineClickListener: Admin
) : RecyclerView.Adapter<QueryAdapter.QueryViewHolder>() {

    interface OnApproveClickListener {
        fun onApproveClick(query: Data)
    }

    interface OnDeclineClickListener {
        fun onDeclineClick(query: Data)
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

        // Handle Approve button click
        holder.buttonApprove.setOnClickListener {
            approveClickListener.onApproveClick(query)
        }

        // Handle Decline button click
        holder.buttonDecline.setOnClickListener {
            // Ask for confirmation before declining the query
            declineClickListener.onDeclineClick(query)
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
            textViewUserId.text = query.userId
        }
    }
}
