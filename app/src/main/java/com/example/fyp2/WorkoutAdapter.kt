package com.example.fyp2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkoutAdapter(private val context: Context, private val workoutList: List<Workout>) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workoutList[position]
        holder.bind(workout)

        // Handle item click
        holder.itemView.setOnClickListener {
            // Start WorkoutDetailsActivity and pass workout details
            val intent = Intent(context, WorkoutDetailsActivity::class.java).apply {
                putExtra("title", workout.workoutName)
                putExtra("duration", workout.duration)
                putExtra("sets", workout.sets)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return workoutList.size
    }

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.workout_title)
        private val durationTextView: TextView = itemView.findViewById(R.id.workout_duration)
        private val setsTextView: TextView = itemView.findViewById(R.id.workout_sets)

        fun bind(workout: Workout) {
            titleTextView.text = workout.workoutName
            durationTextView.text = workout.duration.toString()
            setsTextView.text = workout.sets.toString()
        }
    }
}
