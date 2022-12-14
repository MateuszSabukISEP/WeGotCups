package fr.isep.wegotcups.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.isep.wegotcups.R

class NotificationsRecyclerViewAdapter (private val mList: List<NotificationItemViewModel>, private val onItemClicked: (position: Int) -> Unit) : RecyclerView.Adapter<NotificationsRecyclerViewAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_notification_view, parent, false)

        return ViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: NotificationsRecyclerViewAdapter.ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
        holder.imageView.setImageResource(ItemsViewModel.image)
        holder.textName.text = ItemsViewModel.name
        holder.textDateAndTime.text = ItemsViewModel.dateAndTime
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View, private val onItemClicked: (position: Int) -> Unit)
        : RecyclerView.ViewHolder(ItemView), View.OnClickListener {
        val imageView: ImageView = itemView.findViewById(R.id.event_cover_photo_image_view)
        val textName: TextView = itemView.findViewById(R.id.event_name)
        val textDateAndTime: TextView = itemView.findViewById(R.id.event_date)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            onItemClicked(position)
        }
    }
}