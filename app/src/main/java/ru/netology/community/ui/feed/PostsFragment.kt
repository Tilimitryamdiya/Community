package ru.netology.community.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.community.R
import ru.netology.community.adapter.FeedAdapter
import ru.netology.community.adapter.OnInteractionListener
import ru.netology.community.databinding.FragmentPostsBinding
import ru.netology.community.dto.FeedItem
import ru.netology.community.dto.Post
import ru.netology.community.enumeration.AttachmentType
import ru.netology.community.ui.MediaLifecycleObserver
import ru.netology.community.ui.profile.UserFragment
import ru.netology.community.viewmodel.PostViewModel

class PostsFragment : Fragment() {

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

    private val postViewModel by activityViewModels<PostViewModel>()
    private val mediaObserver = MediaLifecycleObserver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)

        val adapter = FeedAdapter(object : OnInteractionListener {
            override fun onLike(feedItem: FeedItem) {
                postViewModel.likeById(feedItem as Post)
            }

            override fun onRemove(id: Int) {
                postViewModel.removeById(id)
            }

            override fun onEdit(feedItem: FeedItem) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
                postViewModel.edit(feedItem as Post)
            }

            override fun onUser(userId: Int) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_userFragment,
                    bundleOf(
                        UserFragment.USER_ID to userId
                    )
                )
            }

            override fun onPlayPause(feedItem: FeedItem) {
                if (feedItem.attachment?.type == AttachmentType.AUDIO) {
                    feedItem.attachment?.url?.let { mediaObserver.playPause(it) }
                }
            }
        })

        binding.listPosts.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.listPosts.smoothScrollToPosition(0)
                }
            }
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.data.collectLatest { data ->
                    adapter.submitData(data)
                }
            }
        }


        postViewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
        mediaObserver.player?.setOnCompletionListener {
            mediaObserver.player?.stop()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.swiperefresh.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }

        binding.swiperefresh.setOnRefreshListener(adapter::refresh)


        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}