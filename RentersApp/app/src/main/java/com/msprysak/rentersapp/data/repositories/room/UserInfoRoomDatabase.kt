package com.msprysak.rentersapp.data.repositories.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [UserInfo::class], version = 1, exportSchema = false)
public abstract class UserInfoRoomDatabase : RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDao
    companion object {
        @Volatile
        private var INSTANCE: UserInfoRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): UserInfoRoomDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserInfoRoomDatabase::class.java,
                    "users_info_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(UserInfoDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }

        }
    }

    private class UserInfoDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

        }

    }
}
