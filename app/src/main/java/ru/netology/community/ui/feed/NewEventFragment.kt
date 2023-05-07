package ru.netology.community.ui.feed

import android.content.Intent
import android.icu.text.SimpleDateFormat
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
import ru.netology.community.dto.Event
import ru.netology.community.enumeration.AttachmentType
import ru.netology.community.enumeration.EventType
import ru.netology.community.utils.AndroidUtils
import ru.netology.community.viewmodel.EventViewModel
import java.util.*

class NewEventFragment : Fragment() {

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

        var event: Event? = null
        viewModel.edited.observe(viewLifecycleOwner) {
            if (it?.content != "") {
                event = it
                binding.editNewPost.setText(it.content)
                binding.textViewDate.text = AndroidUtils.formatDateTime(it.datetime)
                if (it.type == EventType.OFFLINE) {
                    binding.typeOffline.isChecked
                } else {
                    binding.typeOnline.isChecked
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

        binding.addDate.setOnClickListener {
            val datePickerFragment =
                DatePickerFragment { day, month, year ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month - 1)
                    selectedDate.set(Calendar.DAY_OF_MONTH, day)
                    val date = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.ROOT
                    ).format(selectedDate.time)
                    binding.textViewDate.text = date
                }
            datePickerFragment.show(childFragmentManager, "datePicker")

        }

        binding.addTime.setOnClickListener {
            val timePickerFragment =
                TimePickerFragment { hour, minute ->
                    val selectedTime = Calendar.getInstance()
                    selectedTime.set(Calendar.HOUR_OF_DAY, hour)
                    selectedTime.set(Calendar.MINUTE, minute)
                    val time =
                        SimpleDateFormat("HH:mm", Locale.ROOT).format(selectedTime.time)
                    binding.textViewTime.text = time
                }
            timePickerFragment.show(childFragmentManager, "timePicker")
        }

        binding.createButton.setOnClickListener {
            val content = binding.editNewPost.text.toString()
            val datetime =
                if (binding.textViewDate.text.isNotBlank() && binding.textViewTime.text.isNotBlank()) {
                    binding.textViewDate.text.toString() + "T" + binding.textViewTime.text.toString() + ":00.000000Z"
                } else event?.datetime ?: ""
            if (datetime.isBlank()) {
                Snackbar.make(
                    binding.root,
                    R.string.empty_datetime,
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            val eventType =
                if (binding.typeOffline.isChecked) {
                    EventType.OFFLINE
                } else {
                    EventType.ONLINE
                }
            viewModel.changeContent(
                content = content,
                datetime = datetime,
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