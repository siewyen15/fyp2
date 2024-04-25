package com.example.fyp2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class AddPostActivity : AppCompatActivity() {

    private lateinit var typeSpinner: Spinner
    private lateinit var imageView: ImageView
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var firestore: FirebaseFirestore

    private companion object {
        private const val IMAGE_PICK_REQUEST_CODE = 1
        private const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize views
        typeSpinner = findViewById(R.id.typeSpinner)
        imageView = findViewById(R.id.imageView)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        saveButton = findViewById(R.id.saveButton)

        // Set up spinner
        val typeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.post_types_array,
            android.R.layout.simple_spinner_item
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeAdapter

        // Set up button click listener
        saveButton.setOnClickListener {
            // Get the image URI from the imageView
            val imageUri = getImageUriFromImageView()
            // Pass the imageUri to savePostToFirestore function
            savePostToFirestore(imageUri)
        }

        // Check for and request WRITE_EXTERNAL_STORAGE permission if needed
        if (!isWriteExternalStoragePermissionGranted()) {
            requestWriteExternalStoragePermission()
        }
    }

    private fun isWriteExternalStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestWriteExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            WRITE_EXTERNAL_STORAGE_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the operation
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
        }
    }

    private fun savePostToFirestore(imageUri: Uri?) {
        val type = typeSpinner.selectedItem.toString()
        val description = descriptionEditText.text.toString()
        val currentDate = Calendar.getInstance().time
        val formattedDate = currentDate.toString()

        // Check if any field is empty
        if (type.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if an image is selected
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        // Upload the image to Firebase Storage
        val imageName = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().reference.child("images/$imageName")
        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                // Create a new post object with image URL
                val post = hashMapOf(
                    "type" to type,
                    "description" to description,
                    "date" to formattedDate,
                    "imageUrl" to downloadUri.toString()
                    // Add other fields as needed
                )

                // Check if the current user is not null
                val currentUser = FirebaseAuth.getInstance().currentUser
                currentUser?.let { user ->
                    // Access the Firestore instance
                    val db = FirebaseFirestore.getInstance()

                    // Reference to the "Posts" subcollection of the current user
                    val userPostsCollection = db.collection("users")
                        .document(user.uid)
                        .collection("Posts")

                    // Add the post to the user's "Posts" subcollection
                    userPostsCollection.add(post)
                        .addOnSuccessListener { documentReference ->
                            // Show success toast
                            Toast.makeText(this, "Post successfully saved", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            // Show failure toast
                            Toast.makeText(this, "Failed to save post", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                // Handle failures
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun selectImage(view: View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Image selected successfully, handle the selected image URI here
            val selectedImageUri = data?.data
            // Now you can use the selectedImageUri to display the image or upload it to Firestore
            imageView.setImageURI(selectedImageUri)
        }
    }
    private fun getImageUriFromImageView(): Uri? {
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val uri = bitmapToUri(bitmap)
            return uri
        }
        return null
    }

    private fun bitmapToUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    private fun navigateToCommunityFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, CommunityFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
