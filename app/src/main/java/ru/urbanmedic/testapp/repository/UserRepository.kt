/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 4 May 2025
 */

package ru.urbanmedic.testapp.repository;

import ru.urbanmedic.testapp.model.User

interface UserRepository {

    suspend fun allUsers(): Any

    suspend fun clear()

    suspend fun loadPage(page: Int): Any

    suspend fun createUser(user: User)

    suspend fun updateUser(user: User)

    suspend fun deleteUser(user: User)
}
