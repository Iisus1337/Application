package com.example.myapplication4444.utils

import com.example.myapplication4444.model.Place

object FavoritesRepository {
    private val favorites = mutableListOf<Place>()



    fun addFavorite(place: Place) {
        if (!favorites.any { it.name == place.name }) {
            favorites.add(place)
        }
    }

    fun removeFavorite(place: Place) {
        favorites.removeAll { it.name == place.name }
    }

    fun getFavorites(): List<Place> = favorites

    fun toggleFavorite(place: Place) {
        if (isFavorite(place.name)) {
            removeFavorite(place)
        } else {
            addFavorite(place)
        }
    }

    fun isFavorite(placeName: String): Boolean {
        return favorites.any { it.name == placeName }
    }
}
