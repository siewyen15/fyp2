package com.example.fyp2

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fyp2.WorkoutGoal
import com.example.fyp2.databinding.ItemWorkoutGoalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WorkoutGoalAdapter(
    private val context: Context,
    private val onCompleteClickListener: (WorkoutGoal) -> Unit
) : ListAdapter<WorkoutGoal, WorkoutGoalAdapter.WorkoutGoalViewHolder>(WorkoutGoalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutGoalViewHolder {
        val binding = ItemWorkoutGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutGoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutGoalViewHolder, position: Int) {
        val currentGoal = getItem(position)
        holder.bind(currentGoal)
    }

    inner class WorkoutGoalViewHolder(private val binding: ItemWorkoutGoalBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnDone.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val goal = getItem(position)
                    onCompleteClickListener(goal)
                }
            }

            binding.btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val goal = getItem(position)
                    // Handle delete button click action
                    deleteGoal(goal)
                }
            }
        }

        fun bind(goal: WorkoutGoal) {
            binding.textViewExercise.text = goal.exercise
            binding.textViewGoal.text = "Goal: ${goal.goal}"
        }

        private fun deleteGoal(goal: WorkoutGoal) {
            val currentList = currentList.toMutableList()
            currentList.remove(goal) // Remove the goal from the list

            // Notify the adapter of the change
            submitList(currentList.toList())

            // Delete the goal document from Firestore
            val db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser

            user?.let { currentUser ->
                db.collection("users")
                    .document(currentUser.uid)
                    .collection("workoutGoals")
                    .document(goal.id) // Assuming goal has an id field
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Goal deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error deleting goal: $e", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}

class WorkoutGoalDiffCallback : DiffUtil.ItemCallback<WorkoutGoal>() {
    override fun areItemsTheSame(oldItem: WorkoutGoal, newItem: WorkoutGoal): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: WorkoutGoal, newItem: WorkoutGoal): Boolean {
        return oldItem == newItem
    }
}
