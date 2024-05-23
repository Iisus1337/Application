package com.example.myapplication4444

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication4444.databinding.ActivitySettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference
    private val database: DatabaseReference = FirebaseDatabase.getInstance(
        "https://monstermap-8b2c8-default-rtdb.europe-west1.firebasedatabase.app/"
    ).reference
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize navigation and profile data loading
        loadUserProfileData()
        setupBottomNavigation(binding.bottomNavigation, R.id.nav_profile)
        binding.back.setOnClickListener {
            onBackPressed()
        }
        // Image upload button
        binding.buttonUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Update profile button
        binding.buttonUpdateAll.setOnClickListener {
            updateProfile()
        }
    }

    private fun loadUserProfileData() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val email = user.email ?: ""
            val username = email.substringBefore("@")
            binding.inputNewUsername.setText(username)

            val profileRef = storageRef.child("profiles/${user.uid}/profile_image.jpg")
            profileRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .placeholder(R.drawable.user)
                    .into(binding.userImage)
            }.addOnFailureListener {
                Toast.makeText(this, "Ошибка загрузки изображения профиля", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun updateProfile() {
        val currentUser = firebaseAuth.currentUser
        val newUsername = binding.inputNewUsername.text.toString()
        val currentPassword = binding.inputCurrentPassword.text.toString()
        val newPassword = binding.inputNewPassword.text.toString()

        if (newUsername.isNotEmpty() && currentPassword.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(currentUser?.email!!, currentPassword)
            currentUser.reauthenticate(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    database.child("users").child(currentUser.uid).child("username")
                        .setValue(newUsername)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Имя пользователя успешно обновлено",
                                Toast.LENGTH_SHORT
                            ).show()
                        }.addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Ошибка обновления имени пользователя",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    if (newPassword.isNotEmpty()) {
                        currentUser.updatePassword(newPassword).addOnCompleteListener { passTask ->
                            if (passTask.isSuccessful) {
                                Toast.makeText(this, "Пароль успешно обновлен", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(this, "Ошибка обновления пароля", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Неверный текущий пароль", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Поля не могут быть пустыми", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            val currentUser = firebaseAuth.currentUser

            currentUser?.let { user ->
                val profileRef = storageRef.child("profiles/${user.uid}/profile_image.jpg")
                profileRef.putFile(imageUri)
                    .addOnSuccessListener {
                        profileRef.downloadUrl.addOnSuccessListener { uri ->
                            database.child("users").child(user.uid).child("profile_image_url")
                                .setValue(uri.toString())
                            Glide.with(this)
                                .load(uri)
                                .circleCrop()
                                .placeholder(R.drawable.user)
                                .into(binding.userImage)
                            // Notify user of profile update
                            Toast.makeText(this, "Профиль успешно обновлен", Toast.LENGTH_SHORT)
                                .show()
                        }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "Не удалось загрузить изображение",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
            }
        }
    }
}
