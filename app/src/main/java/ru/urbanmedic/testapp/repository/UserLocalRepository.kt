package ru.urbanmedic.testapp.repository

import android.content.Context
import ru.urbanmedic.testapp.db.UrbanMedicDB
import ru.urbanmedic.testapp.db.UserDao
import ru.urbanmedic.testapp.model.User

class UserLocalRepository(private val userDao: UserDao): UserRepository {

    constructor(context: Context) : this(UrbanMedicDB.getDatabase(context).userDao())

    override suspend fun allUsers(): List<User> = userDao.all()

    override suspend fun clear() = userDao.clear()


    override suspend fun loadPage(page: Int): List<User> {
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