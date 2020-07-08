package cl.jlaak.demoroom.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "subscriber_data_table")
data class Subscriber(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "subscribe_id")
    var id: Int,
    @ColumnInfo(name = "subscribe_name")
    var name: String,
    @ColumnInfo(name = "subscribe_email")
    var email: String
) {
}