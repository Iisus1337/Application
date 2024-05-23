package com.example.myapplication4444

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myapplication4444.databinding.ActivityRedOctoberDetailBinding
import com.example.myapplication4444.model.Place
import com.google.firebase.database.FirebaseDatabase
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

class RedOctoberDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityRedOctoberDetailBinding
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        binding = ActivityRedOctoberDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapView = binding.mapview

        val placeId = intent.getStringExtra("place_id")
        if (placeId != null) {
            loadPlaceDetails(placeId)
        } else {
            Toast.makeText(this, "Place ID is null", Toast.LENGTH_SHORT).show()
        }

        setupClickListeners()
    }

    private fun loadPlaceDetails(placeId: String) {
        val database = FirebaseDatabase.getInstance("https://monstermap-8b2c8-default-rtdb.europe-west1.firebasedatabase.app")
        val placeRef = database.getReference("places/$placeId")

        placeRef.get().addOnSuccessListener { dataSnapshot ->
            val place = dataSnapshot.getValue(Place::class.java)
            if (place != null) {
                updateUI(place)
                setupMap(place)
            } else {
                Toast.makeText(this, "Place not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load place details: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(place: Place) {
        binding.headerTitle.text = place.name
        Glide.with(this).load(place.imageUrl).into(binding.backgroundImageView)
        binding.placeDescription.text = place.description
    }

    private fun setupMap(place: Place) {
        val location = Point(place.latitude, place.longitude)
        mapView.mapWindow.map.move(
            CameraPosition(location, 15.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1.5f),
            null
        )
        mapView.mapWindow.map.mapObjects.addPlacemark(location, ImageProvider.fromResource(this, R.drawable.ic_location))
    }

    private fun setupClickListeners() {
        binding.back.setOnClickListener { onBackPressed() }
        binding.scanIcon.setOnClickListener {
            startActivity(Intent(this, QRCodeScannerActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
        mapView.onStop()
    }
}
