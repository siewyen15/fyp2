package com.example.fyp2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class JournalAdapter(private var entries: MutableList<JournalEntry>) : RecyclerView.Adapter<JournalAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textViewTitle)
        val image: ImageView = itemView.findViewById(R.id.imageView)
        val description: TextView = itemView.findViewById(R.id.textViewDescription)
        val mood: TextView = itemView.findViewById(R.id.textViewMood)
        val date: TextView = itemView.findViewById(R.id.textViewDate)
        val time: TextView = itemView.findViewById(R.id.textViewTime)
    }

    fun updateEntries(newEntries: List<JournalEntry>) {
        entries.clear()
        entries.addAll(newEntries)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.journal_entry_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.title.text = entry.title
        entry.imageUrl?.let {
            holder.image.visibility = View.VISIBLE
            Glide.with(holder.itemView.context).load(it).into(holder.image)
        } ?: kotlin.run {
            holder.image.visibility = View.GONE
        }
        holder.description.text = entry.description
        holder.mood.text = "Mood: ${entry.mood}"
        holder.date.text = "Date: ${entry.date}"
        holder.time.text = "Time: ${entry.time}"
    }



    override fun getItemCount(): Int {
        return entries.size
    }
}
