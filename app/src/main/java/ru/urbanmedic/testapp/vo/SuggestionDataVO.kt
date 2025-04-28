package ru.urbanmedic.testapp.vo

import com.google.gson.annotations.SerializedName

data class SuggestionDataVO(
    @SerializedName("city") var city: String?
)
