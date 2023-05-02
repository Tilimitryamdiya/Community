package ru.netology.community.entity.event

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.community.dto.Attachment
import ru.netology.community.dto.Coordinates
import ru.netology.community.dto.Event
import ru.netology.community.dto.UserPreview
import ru.netology.community.enumeration.EventType

@Entity
data class EventEntity(
    @PrimaryKey
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String?,
    val content: String,
    val published: String,
    val coords: Coordinates?,
    val likeOwnerIds: List<Int> = emptyList(),
    val likedByMe: Boolean = false,
    @Embedded
    val attachment: Attachment? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false,
    val users: Map<Int, UserPreview> = emptyMap(),
    val datetime: String,
    @ColumnInfo(name = "event_type")
    val type: EventType,
    val speakerIds: List<Int> = emptyList(),
    val participantsIds: List<Int> = emptyList(),
    val participatedByMe: Boolean = false
) {
    fun toDto() = Event(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        authorJob = authorJob,
        content = content,
        published = published,
        coords = coords,
        likeOwnerIds = likeOwnerIds,
        likedByMe = likedByMe,
        attachment = attachment,
        link = link,
        ownedByMe = ownedByMe,
        users = users,
        datetime = datetime,
        type = type,
        speakerIds = speakerIds,
        participantsIds = participantsIds,
        participatedByMe = participatedByMe
    )

    companion object {
        fun fromDto(event: Event) = EventEntity(
            id = event.id,
            authorId = event.authorId,
            author = event.author,
            authorAvatar = event.authorAvatar,
            authorJob = event.authorJob,
            content = event.content,
            published = event.published,
            coords = event.coords,
            likeOwnerIds = event.likeOwnerIds,
            likedByMe = event.likedByMe,
            attachment = event.attachment,
            link = event.link,
            ownedByMe = event.ownedByMe,
            users = event.users,
            datetime = event.datetime,
            type = event.type,
            speakerIds = event.speakerIds,
            participantsIds = event.participantsIds,
            participatedByMe = event.participatedByMe
        )
    }
}

fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity::fromDto)