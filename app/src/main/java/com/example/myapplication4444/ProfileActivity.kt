package com.example.myapplication4444

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication4444.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance(
        "https://monstermap-8b2c8-default-rtdb.europe-west1.firebasedatabase.app/"
    ).reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation(binding.bottomNavigation, R.id.nav_profile)

        binding.settingsIcon.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        loadUserProfileData()

        binding.buttonRoutes.setOnClickListener {
            startActivity(Intent(this, RoutesActivity::class.java))
        }

        binding.buttonAchievements.setOnClickListener {
            startActivity(Intent(this, AchievementsActivity::class.java))
        }
    }

    private fun loadUserProfileData() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            database.child("users").child(userId).get().addOnSuccessListener { snapshot ->
                binding.userName.text = snapshot.child("username").getValue(String::class.java) ?: "Неизвестно"
                val imageUrl = snapshot.child("profile_image_url").getValue(String::class.java)
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this).load(imageUrl).circleCrop().into(binding.userIcon)
                } else {
                    binding.userIcon.setImageResource(R.drawable.user)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Не удалось загрузить данные профиля", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
