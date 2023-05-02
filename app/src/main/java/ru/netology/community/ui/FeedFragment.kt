package ru.netology.community.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.community.R
import ru.netology.community.adapter.ViewPagerAdapter
import ru.netology.community.databinding.FragmentFeedBinding
import ru.netology.community.viewmodel.AuthViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by viewModels<AuthViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)

        authViewModel.data.observe(viewLifecycleOwner) {
            binding.apply {
                if(authViewModel.isAuthorized(childFragmentManager)){
                    menuAuth.visibility = View.INVISIBLE
                    ownerGroup.visibility = View.VISIBLE
                    //todo отобразить данные юзера
                } else {
                    menuAuth.visibility = View.VISIBLE
                    ownerGroup.visibility = View.INVISIBLE
                }
            }
        }

        binding.menuAuth.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.menu_auth)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.login -> {
                            findNavController().navigate(R.id.loginFragment)
                            true
                        }
                        R.id.register -> {
                            findNavController().navigate(R.id.registerFragment)
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }

        // TabLayout + ViewPager2
        binding.listContainer.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(binding.tabLayout, binding.listContainer) { tab, position ->
            val feedList = listOf(getString(R.string.posts), getString(R.string.events))
            tab.text = feedList[position]
        }.attach()

        binding.addButton.setOnClickListener {
            if (authViewModel.isAuthorized(childFragmentManager)) {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu_add)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.new_post -> {
                                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
                                true
                            }
                            R.id.new_event -> {
                                findNavController().navigate(R.id.action_feedFragment_to_newEventFragment)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()

            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}