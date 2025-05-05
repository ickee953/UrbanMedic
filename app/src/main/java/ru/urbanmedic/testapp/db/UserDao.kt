/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 27 April 2025
 */

package ru.urbanmedic.testapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.urbanmedic.testapp.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    suspend fun all(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(user: User)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("DELETE from user")
    suspend fun clear()

}