package ru.urbanmedic.testapp.vo

import com.google.gson.annotations.SerializedName

data class SuggestionVO(
    @SerializedName("data") var suggestionDataVO: SuggestionDataVO
)
