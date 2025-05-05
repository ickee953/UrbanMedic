/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 27 April 2025
 */

package ru.urbanmedic.testapp.repository

import ru.urbanmedic.testapp.data.api.ApiHelper
import ru.urbanmedic.testapp.data.api.RetrofitBuilder
import ru.urbanmedic.testapp.model.User

class UserNetworkRepository(
    private val seed: String,
    private val apiHelper: ApiHelper = ApiHelper(RetrofitBuilder.apiService)
): UserRepository {

    override suspend fun loadPage(
        page: Int,
        errorCallback: (errorCode: Int?, errorMsg: String?) -> Unit
    ): Collection<User>? {
        val url = "${RetrofitBuilder.USER_BASE_URL}/${RetrofitBuilder.USER_API_PATH}" +
                "?page=${page}&results=${RetrofitBuilder.USER_API_RESULTS}&seed=${seed}"

        return load(url, errorCallback)
    }

    override suspend fun loadUsers( errorCallback: (errorCode: Int?, errorMsg: String?) -> Unit ): Collection<User>? {

        val url = "${RetrofitBuilder.USER_BASE_URL}/${RetrofitBuilder.USER_API_PATH}?seed=${seed}"

        return load(url, errorCallback)
    }

    private suspend fun load(url: String, errorCallback: (errorCode: Int?, errorMsg: String?) -> Unit ): Collection<User>? {
        var resultList: ArrayList<User>? = null

        try {
            val response = apiHelper.loadUsers(url)

            if( response.code() == 200 ){
                val usersResponse = response.body()

                resultList = ArrayList(usersResponse!!.results.size)

                usersResponse.results.forEach{
                    resultList.add(
                        User(0, it.email, it.userName?.lastName)
                    )
                }

            } else {
                errorCallback( response.code(), null )
            }
        } catch (exception : Exception){
            exception.printStackTrace()
            exception.message?.let {
                errorCallback(null, it)
            }
        }
        return resultList
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
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