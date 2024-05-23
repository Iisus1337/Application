package com.example.myapplication4444

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.myapplication4444.databinding.ActivityMoscowBinding
import com.example.myapplication4444.model.Place
import com.example.myapplication4444.utils.FavoritesRepository
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class MoscowActivity : BaseActivity() {
    private lateinit var binding: ActivityMoscowBinding
    private lateinit var adapter: PlacesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoscowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadBackgroundImage()
        loadPlacesData()

        setupBottomNavigation(binding.bottomNavigation, R.id.nav_places)

        binding.scanIcon.setOnClickListener {
            val intent = Intent(this, QRCodeScannerActivity::class.java)
            startActivity(intent)
        }

        binding.createPlaceButton.setOnClickListener {
            val intent = Intent(this, CreatePlaceActivity::class.java)
            startActivityForResult(intent, PLACE_CONSTRUCTOR_REQUEST_CODE)
        }
    }

    private fun setupRecyclerView() {
        adapter = PlacesAdapter(
            onItemClick = { place ->
                val intent = Intent(this, RedOctoberDetailActivity::class.java).apply {
                    putExtra("place_id", place.id)
                }
                startActivity(intent)
            },
            onFavoriteClick = { place, holder ->
                place.isFavorite = !place.isFavorite
                holder.updateFavoriteIcon(place.isFavorite)
                if (place.isFavorite) {
                    FavoritesRepository.addFavorite(place)
                } else {
                    FavoritesRepository.removeFavorite(place)
                }
                Toast.makeText(this, if (place.isFavorite) "Добавлено в избранное" else "Удалено из избранного", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerViewPlaces.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPlaces.adapter = adapter
    }

    private fun loadPlacesData() {
        val databaseReference = FirebaseDatabase.getInstance("https://monstermap-8b2c8-default-rtdb.europe-west1.firebasedatabase.app").getReference("places")
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val place = snapshot.getValue(Place::class.java)
                place?.let {
                    adapter.addPlace(it)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Implement update logic if needed
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Implement removal logic if needed
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Implement move logic if needed
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MoscowActivity, "Failed to load data: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun loadBackgroundImage() {
        FirebaseStorage.getInstance().getReference("images/bg_1.jpg").downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this).load(uri).into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    binding.coordinatorLayout.background = resource
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load background image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_CONSTRUCTOR_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.getParcelableExtra<Place>("new_place")?.let { adapter.addPlace(it) }
        }
    }

    companion object {
        const val PLACE_CONSTRUCTOR_REQUEST_CODE = 1001
    }
}

class PlacesAdapter(
    private val onItemClick: (Place) -> Unit,
    private val onFavoriteClick: (Place, PlaceViewHolder) -> Unit
) : RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder>() {
    private var places: MutableList<Place> = mutableListOf()

    fun addPlace(place: Place) {
        places.add(place)
        notifyItemInserted(places.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view, onItemClick, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(places[position])
    }

    override fun getItemCount() = places.size

    class PlaceViewHolder(
        itemView: View,
        private val onItemClick: (Place) -> Unit,
        private val onFavoriteClick: (Place, PlaceViewHolder) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(place: Place) {
            itemView.findViewById<TextView>(R.id.place_name).text = place.name
            Glide.with(itemView.context).load(place.imageUrl).into(itemView.findViewById<ImageView>(R.id.place_image))
            val favoriteIcon: ImageView = itemView.findViewById<ImageView>(R.id.favorite_icon)
            updateFavoriteIcon(place.isFavorite)

            favoriteIcon.setOnClickListener {
                onFavoriteClick(place, this)
            }

            itemView.setOnClickListener {
                onItemClick(place)
            }
        }

        fun updateFavoriteIcon(isFavorite: Boolean) {
            val favoriteIcon: ImageView = itemView.findViewById(R.id.favorite_icon)
            if (isFavorite) {
                favoriteIcon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.blue_green), PorterDuff.Mode.SRC_IN)
            } else {
                favoriteIcon.clearColorFilter()
            }
        }
    }
}
