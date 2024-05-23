package com.example.myapplication4444

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication4444.databinding.ActivityRoutesBinding
import com.google.firebase.storage.FirebaseStorage

class RoutesActivity : BaseActivity() {
    private lateinit var binding: ActivityRoutesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        val cities = listOf(
            City("Москва", ""),
            City("Санкт-Петербург", ""),
            City("Казань", "")
        )

        // Загрузка URL изображений из Firebase Storage
        val storageReference = FirebaseStorage.getInstance().reference.child("city")

        cities.forEach { city ->
            val imageName = when (city.name) {
                "Москва" -> "EKB.jpg"
                "Санкт-Петербург" -> "SPB.jpeg"
                "Казань" -> "Kazan.jpg"
                else -> ""
            }

            if (imageName.isNotEmpty()) {
                storageReference.child(imageName).downloadUrl.addOnSuccessListener { uri ->
                    city.imageUrl = uri.toString()
                    binding.recyclerViewCities.adapter?.notifyDataSetChanged()
                }.addOnFailureListener {
                    Toast.makeText(this, "Не удалось загрузить изображение для ${city.name}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val adapter = RoutesCityAdapter(cities) { city ->
            // Обработка клика на элемент списка (город)
        }

        binding.recyclerViewCities.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCities.adapter = adapter
    }

    inner class RoutesCityAdapter(
        private val cities: List<City>,
        private val onClick: (City) -> Unit
    ) : RecyclerView.Adapter<RoutesCityAdapter.CityViewHolder>() {

        inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(city: City) {
                itemView.findViewById<TextView>(R.id.textViewCityName).text = city.name
                Glide.with(itemView.context)
                    .load(city.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(itemView.findViewById<ImageView>(R.id.imageViewCity))

                itemView.setOnClickListener { onClick(city) }

                // Установка слушателя кликов на иконке перехода к избранному
                itemView.findViewById<ImageView>(R.id.imageViewArrow).setOnClickListener {
                    val intent = Intent(itemView.context, FavoritesActivity::class.java)
                    itemView.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city_2, parent, false)
            return CityViewHolder(view)
        }

        override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
            holder.bind(cities[position])
        }

        override fun getItemCount() = cities.size
    }
}
