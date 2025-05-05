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
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Url
import ru.urbanmedic.testapp.vo.UserVO
import ru.urbanmedic.testapp.vo.UsersResultListVO

interface ApiService {

    @GET()
    suspend fun loadUsers(@Url url: String) : Response<UsersResultListVO>

}