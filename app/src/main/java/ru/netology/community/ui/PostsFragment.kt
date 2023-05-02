package ru.netology.community.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.community.R
import ru.netology.community.adapter.FeedAdapter
import ru.netology.community.adapter.OnInteractionListener
import ru.netology.community.databinding.FragmentPostsBinding
import ru.netology.community.dto.FeedItem
import ru.netology.community.dto.Post
import ru.netology.community.viewmodel.PostViewModel

class PostsFragment : Fragment() {

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

    private val postViewModel by activityViewModels<PostViewModel>()

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

            override fun onRemove(feedItem: FeedItem) {
                postViewModel.removeById(feedItem.id)
            }

            override fun onEdit(feedItem: FeedItem) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    bundleOf(
                        NewPostFragment.POST_ID to feedItem.id
                    )
                )
                postViewModel.edit(feedItem as Post)
            }
        })

        binding.listPosts.adapter = adapter

        lifecycleScope.launch {
            postViewModel.data.collectLatest { data ->
                adapter.submitData(data)
            }
        }

        postViewModel.dataState.observe(viewLifecycleOwner) { state ->
//            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .show()
            }
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}