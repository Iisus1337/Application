package com.example.myapplication4444.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.PropertyName

data class Place(
    @PropertyName("id") val id: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("imageUrl") val imageUrl: String = "",
    @PropertyName("description") var description: String = "",
    @PropertyName("isFavorite") var isFavorite: Boolean = false,
    @PropertyName("isSelected") var isSelected: Boolean = false,
    @PropertyName("latitude") val latitude: Double = 0.0,
    @PropertyName("longitude") val longitude: Double = 0.0
) : Parcelable {
    constructor() : this("", "", "", "", false, false, 0.0, 0.0)

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(imageUrl)
        parcel.writeString(description)
        parcel.writeByte(if (isFavorite) 1 else 0)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Place> {
        override fun createFromParcel(parcel: Parcel): Place = Place(parcel)
        override fun newArray(size: Int): Array<Place?> = arrayOfNulls(size)
    }
}
