package ru.netology.community.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.community.R
import ru.netology.community.databinding.FragmentGreetingBinding
import ru.netology.community.viewmodel.AuthViewModel

@AndroidEntryPoint
class GreetingFragment : Fragment() {
    private var _binding: FragmentGreetingBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGreetingBinding.inflate(inflater, container, false)
        authViewModel.data.observe(viewLifecycleOwner) {
            if (authViewModel.authorized) {
                findNavController().navigate(R.id.action_greetingFragment_to_feedFragment)
            }
        }

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_greetingFragment_to_registerFragment)
        }

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_greetingFragment_to_loginFragment)
        }

        binding.close.setOnClickListener {
            findNavController().navigate(R.id.action_greetingFragment_to_feedFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}