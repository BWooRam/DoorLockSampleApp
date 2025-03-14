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
        DeviceEventEntity::class
    ],
    version = 1
)
@TypeConverters(JsonObjectConverter::class)
abstract class AppLocalDatabase2 : RoomDatabase() {
    abstract fun deviceEventDao(): DeviceEventDao

    companion object {
        fun getInstance(context: Context): AppLocalDatabase2 {
            return synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppLocalDatabase2::class.java, "device_events_database.db"
                )
                    .build()
                instance
            }
        }
    }
}

@Entity(
    tableName = "device_events",
    indices = [Index(value = ["deviceId"])] // ✅ 색인 추가
)
data class DeviceEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = "deviceId")
    val deviceId: String,

    @ColumnInfo(name = "states")
    val states: JsonObject?,

    @ColumnInfo(name = "notification")
    val notification: JsonObject?,

    @ColumnInfo(name = "trigger")
    val trigger: JsonObject?
)

data class PushFile(
    val deviceEvent: JsonObject,
)

@Dao
interface DeviceEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pushEntity: DeviceEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pushList: List<DeviceEventEntity>)

    @Query("DELETE FROM device_events")
    suspend fun deleteAllList()

    @Query("SELECT COUNT(*) FROM device_events")
    suspend fun getItemCount(): Int

    @Query("SELECT * FROM device_events ORDER BY id ASC")
    suspend fun allList(): List<DeviceEventEntity>

    @Query("SELECT * FROM device_events WHERE deviceId = :deviceId ORDER BY id ASC")
    suspend fun findList(deviceId: String): List<DeviceEventEntity>

    @Query("SELECT * FROM device_events ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun allList(limit: Int, offset: Int): List<DeviceEventEntity>

    @Query("SELECT * FROM device_events")
    fun pagingSource(): PagingSource<Int, DeviceEventEntity>
}