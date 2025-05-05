/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 5 May 2025
 */

package ru.urbanmedic.testapp.repository

import android.content.Context
import ru.urbanmedic.testapp.db.UrbanMedicDB
import ru.urbanmedic.testapp.db.UserDao
import ru.urbanmedic.testapp.model.User

class UserLocalRepository(private val userDao: UserDao): UserRepository {

    constructor(context: Context) : this(UrbanMedicDB.getDatabase(context).userDao())

    override suspend fun loadUsers(errorCallback: (errorCode: Int?, errorMsg: String?) -> Unit): Collection<User> {
        val localUsersList = userDao.all()
        val resultList: ArrayList<User> = ArrayList(localUsersList.size)

        localUsersList.forEach{
            resultList.add(
                User(it.id, it.email, it.lastName)
            )
        }

        return resultList
    }

    override suspend fun loadPage(
        page: Int,
        errorCallback: (errorCode: Int?, errorMsg: String?) -> Unit
    ): Collection<User>? {
        TODO("Not yet implemented")
    }

    override suspend fun clear() {
        userDao.clear()
    }

    override suspend fun createUser(user: User) {
        userDao.create(user)
    }

    override suspend fun updateUser(user: User) {
        userDao.update(user)
    }

    override suspend fun deleteUser(user: User) {
        userDao.delete(user)
    }


}