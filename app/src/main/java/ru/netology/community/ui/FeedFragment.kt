package ru.netology.community.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.community.R
import ru.netology.community.adapter.FeedAdapter
import ru.netology.community.adapter.OnInteractionListener
import ru.netology.community.databinding.FragmentFeedBinding
import ru.netology.community.dto.FeedItem
import ru.netology.community.dto.Post
import ru.netology.community.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<PostViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = FeedAdapter(object : OnInteractionListener{
            override fun onLike(feedItem: FeedItem) {
                viewModel.likeById(feedItem as Post)
            }

            override fun onRemove(feedItem: FeedItem) {
                viewModel.removeById(feedItem.id)
            }

        })
        binding.list.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest { data ->
                    adapter.submitData(data)
                }
            }
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
//            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
            binding.newPostButton.setOnClickListener {
            if (viewModel.isAuthorized(childFragmentManager)) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}