/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 27 April 2025
 */

package ru.urbanmedic.testapp.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import ru.urbanmedic.testapp.vo.GeoVO
import ru.urbanmedic.testapp.vo.SuggestionsListVO

interface GeoService {

    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "Content-Encoding: UTF-8",
        "Authorization: Token 0fc7d60da65943f6aa3ba2f4a289b50bc024d18f"
    )
    @POST(RetrofitBuilder.GEO_PATH)
    suspend fun getCity(
        @Body geo: GeoVO
    ) : Response<SuggestionsListVO>
}