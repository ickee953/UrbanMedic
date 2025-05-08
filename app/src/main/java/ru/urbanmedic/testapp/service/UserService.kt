/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 5 May 2025
 */

package ru.urbanmedic.testapp.service

import ru.urbanmedic.testapp.model.User
import ru.urbanmedic.testapp.repository.UserRepository

class UserService(private val repository: UserRepository) {
    suspend fun loadUsers(
        errCallback:(code: Int?, msg: String?)->Unit = {_,_->}
    ): Collection<User>? = repository.loadUsers(errCallback)

    suspend fun loadPage(
        page: Int,
        errCallback:(code: Int?, msg: String?)->Unit
    ): Collection<User>? = repository.loadPage(page, errCallback)
}