/**
 * © Panov Vitaly 2025 - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Panov Vitaly 25 April 2025
 */

package ru.urbanmedic.testapp.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.urbanmedic.testapp.model.Seed
import ru.urbanmedic.testapp.model.User

@Database(
    version = 1,
    entities = [Seed::class, User::class]
)
abstract class UrbanMedicDB : RoomDatabase() {

    abstract fun seedDao(): SeedDao

    abstract fun userDao(): UserDao

    companion object {
        private var INSTANCE: UrbanMedicDB? = null
        private const val DB_NAME = "urban_medic.db"

        fun getDatabase( context: Context ): UrbanMedicDB {
            if( INSTANCE == null ){
                synchronized( UrbanMedicDB::class.java ){
                    if( INSTANCE == null ){
                        INSTANCE = Room.databaseBuilder(context.applicationContext
                                                        , UrbanMedicDB::class.java, DB_NAME)
                            .addCallback(object : Callback() {
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    Log.d("UrbanMedicDB", "Created local database.")
                                }
                            }).build()
                    }
                }
            }

            return INSTANCE!!
        }
    }

}