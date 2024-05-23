package com.example.myapplication4444

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication4444.databinding.ActivityCreatePlaceBinding
import com.example.myapplication4444.model.Place
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class CreatePlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePlaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createButton.setOnClickListener {
            createNewPlace()
        }
    }

    private fun createNewPlace() {
        val name = binding.nameEditText.text.toString().trim()
        val imageName = binding.imageNameEditText.text.toString().trim()
        val descriptionName = binding.descriptionNameEditText.text.toString().trim()
        val latitude = binding.latitudeEditText.text.toString().toDoubleOrNull()
        val longitude = binding.longitudeEditText.text.toString().toDoubleOrNull()

        if (name.isEmpty() || imageName.isEmpty() || descriptionName.isEmpty() || latitude == null || longitude == null) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        val imageRef = FirebaseStorage.getInstance().reference.child("place/$imageName")
        val descriptionRef = FirebaseStorage.getInstance().reference.child("Text/$descriptionName")

        imageRef.downloadUrl.addOnSuccessListener { imageUri ->
            descriptionRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { descriptionBytes ->
                val description = String(descriptionBytes)
                val databaseRef = FirebaseDatabase.getInstance().getReference("places")
                val placeId = databaseRef.push().key ?: return@addOnSuccessListener  // Ensure the key is not null
                val place = Place(placeId, name, imageUri.toString(), description, false, false, latitude, longitude)

                savePlaceToDatabase(place)
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load description", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePlaceToDatabase(place: Place) {
        val database = FirebaseDatabase.getInstance("https://monstermap-8b2c8-default-rtdb.europe-west1.firebasedatabase.app")
        val databaseRef = database.getReference("places")
        databaseRef.child(place.id).setValue(place).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Place added successfully", Toast.LENGTH_SHORT).show()
                finish()  // Finish activity and return to the previous screen
            } else {
                Toast.makeText(this, "Failed to add place", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
