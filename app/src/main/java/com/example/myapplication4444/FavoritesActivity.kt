package com.example.myapplication4444

import FavoritesAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication4444.databinding.ActivityFavoritesBinding
import com.example.myapplication4444.model.Place
import com.example.myapplication4444.utils.FavoritesRepository

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var favoritesAdapter: FavoritesAdapter // Changed to lateinit for better null safety

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            onBackPressed()
        }

        setupRecyclerView()
        setupCreateRouteButton()
    }

    private fun setupRecyclerView() {
        favoritesAdapter = FavoritesAdapter(
            favorites = FavoritesRepository.getFavorites(),
            onFavoriteToggle = { place ->
                FavoritesRepository.toggleFavorite(place)
                favoritesAdapter?.updateFavorites(FavoritesRepository.getFavorites()) // Ensure favorites list is updated
            },
            onSelect = { place, isSelected ->
                place.isSelected = isSelected
            }
        )
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFavorites.adapter = favoritesAdapter
    }

    private fun setupCreateRouteButton() {
        binding.buttonPerformLogin.setOnClickListener {
            val selectedPlaces = FavoritesRepository.getFavorites().filter { it.isSelected }
            if (selectedPlaces.isNotEmpty()) {
                val intent = Intent(this, LocationActivity::class.java).apply {
                    putExtra("selected_places", ArrayList(selectedPlaces))
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Выберите хотя бы одно место для маршрута", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
