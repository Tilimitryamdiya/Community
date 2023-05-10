package ru.netology.community.ui.attachment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import ru.netology.community.databinding.FragmentVideoBinding

class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)

        val url = requireArguments().getString(URL)
        binding.apply {
            video.setVideoURI(Uri.parse(url))
            val mediaController = MediaController(requireContext())
            mediaController.setAnchorView(video)
            mediaController.setMediaPlayer(video)
            video.setMediaController(mediaController)
            video.start()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        val URL = "URL"
    }
}