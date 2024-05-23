package com.example.myapplication4444

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication4444.databinding.ActivityCitySelectionBinding

class CitySelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCitySelectionBinding
    private var selectedCity: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backImageView = findViewById<ImageView>(R.id.back)
        backImageView.setOnClickListener { onBackPressed() }

        val cities = listOf("Екатеринбург", "Санкт-Петербург", "Новосибирск")
        val adapter = CityAdapter(this, cities) { city ->
            selectedCity = city
            binding.btnContinue.isEnabled = city == "Екатеринбург"
            binding.btnContinue.setBackgroundColor(if (city == "Екатеринбург") Color.GREEN else Color.GRAY)
        }

        val listViewCities = findViewById<ListView>(R.id.lvCities)
        listViewCities.adapter = adapter

        binding.btnContinue.setOnClickListener {
            if (selectedCity == "Екатеринбург") {
                startActivity(Intent(this, MoscowActivity::class.java))
            } else {
                Toast.makeText(this, "Выберите город для продолжения", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCityList(cityName: String) {
        Toast.makeText(this, "Selected: $cityName", Toast.LENGTH_SHORT).show()
    }
}

class CityAdapter(private val context: Context, private val cities: List<String>, private val onClick: (String) -> Unit) : BaseAdapter() {
    override fun getCount(): Int = cities.size

    override fun getItem(position: Int): Any = cities[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_city, parent, false)
        val textViewCityName = view.findViewById<TextView>(R.id.textViewCityName)
        textViewCityName.text = cities[position]
        view.setOnClickListener { onClick(cities[position]) }
        return view
    }
}
