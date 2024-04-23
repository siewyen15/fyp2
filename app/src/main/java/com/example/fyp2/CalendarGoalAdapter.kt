package com.example.fyp2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CalendarGoalAdapter : ListAdapter<CalendarGoal, CalendarGoalViewHolder>(CalendarGoalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarGoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_goal, parent, false)
        return CalendarGoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarGoalViewHolder, position: Int) {
        val currentGoal = getItem(position)
        holder.bind(currentGoal)
    }
}

class CalendarGoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val exerciseTextView: TextView = itemView.findViewById(R.id.exerciseTextView)
    private val goalTextView: TextView = itemView.findViewById(R.id.goalTextView)
    private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    private val completedTextView: TextView = itemView.findViewById(R.id.completedTextView)

    fun bind(goal: CalendarGoal) {
        exerciseTextView.text = goal.exercise
        goalTextView.text = goal.goalValue
        dateTextView.text = goal.dateString
        completedTextView.text = if (goal.isCompleted) "Yes" else "No"
    }
}

class CalendarGoalDiffCallback : DiffUtil.ItemCallback<CalendarGoal>() {
    override fun areItemsTheSame(oldItem: CalendarGoal, newItem: CalendarGoal): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CalendarGoal, newItem: CalendarGoal): Boolean {
        return oldItem == newItem
    }
}
