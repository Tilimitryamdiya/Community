package ru.netology.community.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.community.R
import ru.netology.community.adapter.FeedVpAdapter
import ru.netology.community.databinding.FragmentFeedBinding
import ru.netology.community.dialog.SignOutDialog
import ru.netology.community.ui.profile.UserFragment
import ru.netology.community.view.load
import ru.netology.community.viewmodel.AuthViewModel
import ru.netology.community.viewmodel.EventViewModel
import ru.netology.community.viewmodel.PostViewModel
import ru.netology.community.viewmodel.UserViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by viewModels<AuthViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private val postViewModel by viewModels<PostViewModel>()
    private val eventViewModel by viewModels<EventViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)

        authViewModel.data.observe(viewLifecycleOwner) { authModel ->
            binding.apply {
                if (authViewModel.authorized) {
                    menuAuth.visibility = View.INVISIBLE
                    ownerGroup.visibility = View.VISIBLE
                    logOut.visibility = View.VISIBLE
                } else {
                    menuAuth.visibility = View.VISIBLE
                    ownerGroup.visibility = View.INVISIBLE
                    logOut.visibility = View.INVISIBLE
                }
            }

            userViewModel.getUserById(authModel.id)
        }

        userViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.ownerName.text = user.name
            user.avatar?.apply {
                binding.ownerAvatar.load(this)
            } ?: binding.ownerAvatar.setImageResource(R.drawable.no_avatar)
            binding.ownerAvatar.setOnClickListener {
                findNavController().navigate(
                    R.id.action_feedFragment_to_userFragment,
                    bundleOf(UserFragment.USER_ID to user.id)
                )
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

        binding.logOut.setOnClickListener {
            authViewModel.confirmLogout(
                childFragmentManager,
                object : SignOutDialog.ConfirmationListener {
                    override fun confirmButtonClicked() {
                        authViewModel.logout()
                        findNavController().navigate(R.id.action_feedFragment_to_greetingFragment)
                    }
                })
        }

        // TabLayout + ViewPager2
        binding.listContainer.adapter = FeedVpAdapter(childFragmentManager, lifecycle)
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
                                postViewModel.clearEdited()
                                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
                                true
                            }
                            R.id.new_event -> {
                                eventViewModel.clearEdited()
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