/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 27 April 2025
 */

package ru.urbanmedic.testapp.repository

import ru.urbanmedic.testapp.data.api.ApiHelper

class UserRepository(private val apiHelper: ApiHelper) {

    suspend fun allUsers(url: String, seed: String) = apiHelper.allUsers( url, seed )

}