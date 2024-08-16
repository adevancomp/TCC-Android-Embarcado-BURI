package br.edu.uea.buri.data.database.converter

import androidx.room.TypeConverter
import java.time.ZonedDateTime

class ZonedDateTimeConverter {
    @TypeConverter
    fun fromZonedDateTime(zonedDateTime: ZonedDateTime?): String? {
        return zonedDateTime?.toString()
    }

    @TypeConverter
    fun toZonedDateTime(zonedDateTimeString: String?): ZonedDateTime? {
        return zonedDateTimeString?.let { ZonedDateTime.parse(it) }
    }
}