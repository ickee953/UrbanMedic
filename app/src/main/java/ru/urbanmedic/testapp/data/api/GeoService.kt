/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 25 April 2025
 */

package ru.urbanmedic.testapp.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url
import ru.urbanmedic.testapp.vo.GeoVO

interface GeoService {

    @Headers("Content-type: application/json", "Content-Encoding: UTF-8")
    @POST()
    suspend fun getCity(
        @Url url: String,
        @Header("Authorization") token: String,
        @Body geo: GeoVO
    ) : Response<String?>
}