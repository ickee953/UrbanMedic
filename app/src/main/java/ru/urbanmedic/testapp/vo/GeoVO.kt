package ru.urbanmedic.testapp.vo

import com.google.gson.annotations.SerializedName

data class GeoVO (
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lon") val lon: Double?
)