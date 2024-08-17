package br.edu.uea.buri.data.database.converter

import androidx.room.TypeConverter
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZonedDateTimeConverter {

    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    @TypeConverter
    fun fromZonedDateTime(zonedDateTime: ZonedDateTime): String {
        return zonedDateTime.format(formatter)
    }

    @TypeConverter
    fun toZonedDateTime(zonedDateTimeString: String): ZonedDateTime {
        return ZonedDateTime
            .parse(zonedDateTimeString,formatter)
            .withZoneSameInstant(ZoneId.of("America/Manaus"))
    }
}