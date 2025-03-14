package com.hyundaiht.doorlocksampleapp.push

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.JsonObject


@Database(
    entities = [
        PushEntity::class
    ],
    version = 1
)
@TypeConverters(JsonObjectConverter::class)
abstract class AppLocalDatabase1 : RoomDatabase() {
    abstract fun pushDao(): PushDao

    companion object {
        fun getInstance(context: Context): AppLocalDatabase1 {
            return synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppLocalDatabase1::class.java, "push_database.db"
                )
                    .build()
                instance
            }
        }
    }
}

class JsonObjectConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromJsonObject(jsonObject: JsonObject?): String {
        return jsonObject?.toString() ?: "{}"
    }

    @TypeConverter
    fun toJsonObject(jsonString: String): JsonObject {
        return gson.fromJson(jsonString, JsonObject::class.java)
    }
}

/**
 * 인덱스 추가 필요
 */
@Entity(tableName = "pushEntity", indices = [Index(value = ["deviceEvent"])])
data class PushEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "deviceEvent") val deviceEvent: JsonObject,
    @ColumnInfo(name = "listenerInfoMap") val listenerInfoMap: JsonObject
)

@Dao
interface PushDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pushEntity: PushEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pushList: List<PushEntity>)

    @Query("DELETE FROM pushEntity")
    suspend fun deleteAllList()

    @Query("SELECT COUNT(*) FROM pushEntity")
    suspend fun getItemCount(): Int

    @Query("SELECT * FROM pushEntity ORDER BY id ASC")
    suspend fun allList(): List<PushEntity>

    @Query("SELECT * FROM pushEntity WHERE deviceEvent GLOB '*'||:search||'*' ORDER BY id ASC")
    suspend fun findList(search: String): List<PushEntity>

    @Query("SELECT * FROM pushEntity ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun allList(limit: Int, offset: Int): List<PushEntity>

    @Query("SELECT * FROM pushEntity")
    fun pagingSource(): PagingSource<Int, PushEntity>
}