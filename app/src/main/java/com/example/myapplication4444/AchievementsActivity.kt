package com.example.myapplication4444

import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.myapplication4444.databinding.ActivityAchievementsBinding
import com.google.firebase.storage.FirebaseStorage

class AchievementsActivity : BaseActivity() {  // Наследование от BaseActivity
    private lateinit var binding: ActivityAchievementsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation(binding.bottomNavigation, R.id.nav_profile)

        binding.back.setOnClickListener {
            onBackPressed()
        }

        loadBackgroundImage()
    }


    private fun loadBackgroundImage() {
        val storageReference = FirebaseStorage.getInstance().reference.child("Achivments/WB.jpg")
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(binding.imageViewAchievement)  // Убедитесь, что imageViewAchievement это ID вашего ImageView в XML.
        }.addOnFailureListener {
            // Обработка ошибки загрузки
        }
    }
}
