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
import ru.netology.community.databinding.FragmentEventsBinding
import ru.netology.community.dto.Event
import ru.netology.community.dto.FeedItem
import ru.netology.community.viewmodel.EventViewModel

class EventsFragment : Fragment() {
    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel by activityViewModels<EventViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)

        val adapter = FeedAdapter(object : OnInteractionListener {
            override fun onLike(feedItem: FeedItem) {
                eventViewModel.likeById(feedItem as Event)
            }

            override fun onRemove(feedItem: FeedItem) {
                eventViewModel.removeById(feedItem.id)
            }

            override fun onEdit(feedItem: FeedItem) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_newEventFragment,
                    bundleOf(
                        NewEventFragment.EVENT_ID to feedItem.id
                    )
                )
                eventViewModel.edit(feedItem as Event)
            }
        })

        binding.listEvents.adapter = adapter

        lifecycleScope.launch {
            eventViewModel.data.collectLatest { data ->
                adapter.submitData(data)
            }
        }

        eventViewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        return binding.root
    }
}