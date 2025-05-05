/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 27 April 2025
 */

package ru.urbanmedic.testapp.repository

import retrofit2.Response
import ru.urbanmedic.testapp.data.api.ApiHelper
import ru.urbanmedic.testapp.data.api.RetrofitBuilder
import ru.urbanmedic.testapp.model.User
import ru.urbanmedic.testapp.vo.UsersResultListVO

class UserNetworkRepository(
    private val apiHelper: ApiHelper,
    private val seed: String
): UserRepository {

    //suspend fun allUsers(url: String) = apiHelper.allUsers( url )

    override suspend fun allUsers(): Response<UsersResultListVO> {
        return apiHelper.loadUsers("${RetrofitBuilder.USER_BASE_URL}/${RetrofitBuilder.USER_API_PATH}" +
                "?seed=${seed}")
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
    }


    override suspend fun loadPage(page: Int): Response<UsersResultListVO> {
        return apiHelper.loadUsers("${RetrofitBuilder.USER_BASE_URL}/${RetrofitBuilder.USER_API_PATH}" +
                "?page=${page}&results=${RetrofitBuilder.USER_API_RESULTS}&seed=${seed}")
    }

    override suspend fun createUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(user: User) {
        TODO("Not yet implemented")
    }


}