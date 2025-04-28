/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 28 April 2025
 */

package ru.urbanmedic.testapp.vo

import com.google.gson.annotations.SerializedName

data class SuggestionVO(
    @SerializedName("data") var suggestionDataVO: SuggestionDataVO
)
