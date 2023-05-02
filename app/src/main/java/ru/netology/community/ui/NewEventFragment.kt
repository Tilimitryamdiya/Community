package ru.netology.community.ui

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
import ru.netology.community.enumeration.EventType
import ru.netology.community.utils.AndroidUtils
import ru.netology.community.viewmodel.EventViewModel

class NewEventFragment : Fragment() {

    companion object {
        const val EVENT_ID = "EVENT_ID"
    }

    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by activityViewModels()
    private var imageLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPostBinding.inflate(inflater, container, false)
        binding.eventGroup.isVisible = true

        arguments?.getInt(EVENT_ID)?.apply {
            viewModel.getById(this)
        }
        viewModel.event.observe(viewLifecycleOwner) {
            binding.editNewPost.setText(it?.content)
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

        binding.addDate.setOnClickListener {
            val newFragment =
                DatePickerFragment { day, month, year ->
                    val d = if (day < 10) "0$day" else "$day"
                    val m = if (month < 10) "0$month" else "$month"
                    viewModel.addDate("$year-$m-$d")
                }
            newFragment.show(childFragmentManager, "datePicker")

        }

        binding.addTime.setOnClickListener {
            //TODO: отнимает 3 часа после приведения к формату!
            val newFragment =
                TimePickerFragment { hourOfDay, minute ->
                    val h = if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"
                    val m = if (minute < 10) "0$minute" else "$minute"
                    viewModel.addTime("$h:$m")
                }
            newFragment.show(childFragmentManager, "timePicker")
        }

        viewModel.date.observe(viewLifecycleOwner) { date ->
            binding.textViewDate.text = date ?: ""
        }

        viewModel.time.observe(viewLifecycleOwner) { time ->
            binding.textViewTime.text = time ?: ""
        }

        binding.createButton.setOnClickListener {
            val eventType =
                if (binding.typeOffline.isChecked) {
                    EventType.OFFLINE
                } else {
                    EventType.ONLINE
                }
            viewModel.changeContent(
                binding.editNewPost.text.toString(),
                eventType = eventType
            )
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }

        viewModel.eventCreated.observe(viewLifecycleOwner) {
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