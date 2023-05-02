package ru.netology.community.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.community.R
import ru.netology.community.databinding.CardItemBinding
import ru.netology.community.dto.Event
import ru.netology.community.dto.FeedItem
import ru.netology.community.enumeration.EventType
import ru.netology.community.view.load
import ru.netology.community.view.loadAttachment


class FeedAdapter(
    private val listener: OnInteractionListener
) : PagingDataAdapter<FeedItem, FeedViewHolder>(FeedDiffCallback()) {

    override fun onBindViewHolder(
        holder: FeedViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            payloads.forEach {
                (it as? Payload)?.let { payload ->
                    holder.bind(payload)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardItemBinding.inflate(inflater, parent, false)
        return FeedViewHolder(binding, listener)
    }
}

class FeedViewHolder(
    private val binding: CardItemBinding,
    private val listener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(feedItem: FeedItem) {
        binding.apply {
            feedItem.authorAvatar?.let { avatar.load(it) }
                ?: avatar.setImageResource(R.drawable.no_avatar)
            author.text = feedItem.author
            authorJob.text = feedItem.authorJob
            published.text = feedItem.published
            content.text = feedItem.content

            like.isChecked = feedItem.likedByMe
            like.text = feedItem.likeOwnerIds.size.toString()
            like.setOnClickListener { listener.onLike(feedItem) }

            menu.isVisible = feedItem.ownedByMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.delete -> {
                                listener.onRemove(feedItem)
                                true
                            }
                            R.id.edit -> {
                                listener.onEdit(feedItem)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            if (feedItem.attachment != null) {
                attachment.visibility = View.VISIBLE
                attachment.loadAttachment(feedItem.attachment!!.url)
            } else {
                attachment.visibility = View.GONE
            }

            if (feedItem is Event) {
                eventGroup.visibility = View.VISIBLE
                eventDate.text = feedItem.datetime
                eventType.text = feedItem.type.toString()
                when (feedItem.type) {
                    EventType.ONLINE -> typeOfEventIcon.setImageResource(R.drawable.online_event)
                    EventType.OFFLINE -> typeOfEventIcon.setImageResource(R.drawable.offline_event)
                }
            } else {
                eventGroup.visibility = View.GONE
            }
        }
    }

    fun bind(payload: Payload) {
        payload.likeByMe?.let {
            binding.like.isChecked = it
        }

        payload.content?.let {
            binding.content.text = it
        }
    }
}

data class Payload(
    val likeByMe: Boolean? = null,
    val content: String? = null,
)


class FeedDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}

interface OnInteractionListener {
    fun onLike(feedItem: FeedItem)
    fun onRemove(feedItem: FeedItem)

    fun onEdit(feedItem: FeedItem)
}