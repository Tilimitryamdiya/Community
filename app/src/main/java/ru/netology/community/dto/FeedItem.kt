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
    val likeOwnerIds: List<Int>
    val likedByMe: Boolean
    val attachment: Attachment?
    val link: String?
    val ownedByMe: Boolean
    val users: Map<Int, UserPreview>
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
    override val likeOwnerIds: List<Int> = emptyList(),
    override val likedByMe: Boolean = false,
    override val attachment: Attachment? = null,
    override val link: String?,
    override val ownedByMe: Boolean = false,
    override val users: Map<Int, UserPreview>,
    val mentionIds: List<Int> = emptyList(),
    val mentionedMe: Boolean,
) : FeedItem

data class Event(
    override val id: Int,
    override val authorId: Int,
    override val author: String,
    override val authorAvatar: String?,
    override val authorJob: String?,
    override val content: String,
    override val published: String,
    override val coords: Coordinates?,
    override val likeOwnerIds: List<Int> = emptyList(),
    override val likedByMe: Boolean = false,
    override val attachment: Attachment? = null,
    override val link: String?,
    override val ownedByMe: Boolean = false,
    override val users: Map<Int, UserPreview>,
    val datetime: String,
    val type: EventType,
    val speakerIds: List<Int> = emptyList(),
    val participantsIds: List<Int> = emptyList(),
    val participatedByMe: Boolean
) : FeedItem

data class Attachment(
    val url: String,
    val type: AttachmentType
)