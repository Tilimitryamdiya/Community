package ru.netology.community.ui.feed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import ru.netology.community.R
import ru.netology.community.databinding.FragmentNewPostBinding
import ru.netology.community.enumeration.AttachmentType
import ru.netology.community.utils.AndroidUtils
import ru.netology.community.viewmodel.PostViewModel

class NewPostFragment: Fragment() {

    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by activityViewModels()
    private var imageLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        binding.eventGroup.isVisible = false

        viewModel.getEditPost()?.let { post ->
            binding.editNewPost.setText(post.content)
            post.attachment?.let {
                when (it.type) {
                    AttachmentType.IMAGE -> {
                        binding.textViewImage.text = it.url
                    }
                    AttachmentType.AUDIO -> {
                        binding.textViewMusic.text = it.url
                    }
                    AttachmentType.VIDEO -> {}
                }
            }
        }

        imageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else -> {
                        val uri = it.data?.data ?: return@registerForActivityResult
                        viewModel.addPhoto(uri, uri.toFile(), AttachmentType.IMAGE)
                    }
                }
            }

        binding.createButton.setOnClickListener {
            viewModel.changeContent(binding.editNewPost.text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.addImageButton.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .createIntent(imageLauncher!!::launch)
        }

        binding.clearImage.setOnClickListener {
            viewModel.clearPhoto()
        }

        viewModel.media.observe(viewLifecycleOwner) { media ->
            binding.clearImage.isVisible = media != null
            binding.textViewImage.text = media?.file?.name
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        imageLauncher = null
    }
}