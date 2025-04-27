/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 27 April 2025
 */

package ru.urbanmedic.testapp.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Url
import ru.urbanmedic.testapp.vo.UserVO

interface ApiService {

    @Headers("Content-type: application/json", "Content-Encoding: UTF-8")
    @GET()
    suspend fun allUsers(@Url url: String, @Path(value = "seed") seed: String) : Response<List<UserVO>>

}