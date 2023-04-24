package ru.netology.community.dto

import ru.netology.community.enumeration.AttachmentType
import ru.netology.community.enumeration.EventType

sealed interface FeedItem {
    val id: Int
    val authorId: Int
    val author: String
    val authorAvatar: String?
    val authorJob: String?
    val content: String
    val published: String
    val coords: Coordinates?
    val likedByMe: Boolean
    val attachment: Attachment?
    val ownedByMe: Boolean
}

data class Post(
    override val id: Int,
    override val authorId: Int,
    override val author: String,
    override val authorAvatar: String?,
    override val authorJob: String?,
    override val content: String,
    override val published: String,
    override val coords: Coordinates?,
    val link: String?,
    val likeOwnerIds: List<Int> = emptyList(),
    val mentionIds: List<Int> = emptyList(),
    val mentionedMe: Boolean,
    override val likedByMe: Boolean = false,
    override val attachment: Attachment? = null,
    override val ownedByMe: Boolean = false,
    val users: Map<Int, UserPreview>,
    val isPaying: Boolean = false
) : FeedItem

data class Event(
    override val id: Int,
    override val authorId: Int,
    override val author: String,
    override val authorAvatar: String? = null,
    override val authorJob: String = "",
    override val content: String,
    override val published: String,
    override val coords: Coordinates? = null,
    override val likedByMe: Boolean = false,
    override val attachment: Attachment? = null,
    override val ownedByMe: Boolean = false,
    val datetime: String,
    val type: EventType,
    val speakerIds: Int = 0,
    val participantsIds: Int = 0,
    val participatedByMe: Boolean = false,
) : FeedItem

data class Attachment(
    val url: String,
    val type: AttachmentType
)

data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}
