package ru.netology.community.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.community.dto.Coordinates
import ru.netology.community.dto.UserPreview

class ListIntConverter {
    @TypeConverter
    fun toJson(list: List<Int>) = Gson().toJson(list)

    @TypeConverter
    fun fromJson(json: String): List<Int> {
        return Gson().fromJson(json, object : TypeToken<List<Int>>() {}.type)
    }
}

class MapUsersPrevConverter {
    @TypeConverter
    fun toJson(map: Map<Int, UserPreview>) = Gson().toJson(map)

    @TypeConverter
    fun fromJson(json: String): Map<Int, UserPreview> {
        return Gson().fromJson(json, object : TypeToken<Map<Int, UserPreview>>() {}.type)
    }
}

class CoordinatesConverter {
    @TypeConverter
    fun toJson(coordinates: Coordinates?) = Gson().toJson(coordinates)

    @TypeConverter
    fun fromJson(json: String): Coordinates? {
        return Gson().fromJson(json, object : TypeToken<Coordinates>() {}.type)
    }
}
