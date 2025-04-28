package ru.urbanmedic.testapp.vo

import com.google.gson.annotations.SerializedName

data class SuggestionsListVO(
    @SerializedName("suggestions") var suggestionsVO: List<SuggestionVO>
)