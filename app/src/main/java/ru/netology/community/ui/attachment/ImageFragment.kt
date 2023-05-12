package ru.netology.community.ui.attachment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.community.databinding.FragmentImageBinding
import ru.netology.community.view.loadAttachment

class ImageFragment: Fragment() {

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageBinding.inflate(inflater, container, false)

        requireArguments().getString(VideoFragment.URL)?.let {
            binding.fullscreenImage.loadAttachment(it)
        }

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        const val URL = "URL"
    }
}