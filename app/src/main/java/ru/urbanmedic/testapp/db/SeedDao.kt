/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 25 April 2025
 */

package ru.urbanmedic.testapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.urbanmedic.testapp.model.Seed

@Dao
interface SeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun login( seed: Seed)

    @Query("SELECT * FROM seed LIMIT 1")
    suspend fun loggedIn(): Seed?

    @Query("DELETE FROM seed")
    suspend fun logout()

}