package com.example.fyp2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkoutAdapter(
    private val context: Context,
    private val workoutList: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workoutType = workoutList[position]
        holder.bind(workoutType)
    }

    override fun getItemCount(): Int {
        return workoutList.size
    }

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val workoutTextView: TextView = itemView.findViewById(R.id.textView_workout)

        fun bind(workoutType: String) {
            workoutTextView.text = workoutType
            workoutTextView.setOnClickListener {
                onItemClick.invoke(workoutType)
            }
        }
    }
}
