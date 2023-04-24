package ru.netology.community.dao

import androidx.room.TypeConverter
import ru.netology.community.enumeration.AttachmentType


class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)

    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name

    @TypeConverter
    fun fromSet(set: Set<Int>): String = set.joinToString(",")

    @TypeConverter
    fun toSet(data: String): Set<Int> =
        if (data.isBlank()) emptySet() else data.split(",").map { it.toInt() }.toSet()
}
