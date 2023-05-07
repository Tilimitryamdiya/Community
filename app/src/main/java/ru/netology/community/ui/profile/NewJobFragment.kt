package ru.netology.community.ui.profile

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.community.databinding.FragmentNewJobBinding
import ru.netology.community.ui.feed.DatePickerFragment
import ru.netology.community.utils.AndroidUtils
import ru.netology.community.viewmodel.JobViewModel
import java.util.*

class NewJobFragment : Fragment() {

    private var _binding: FragmentNewJobBinding? = null
    private val binding get() = _binding!!

    private val viewModel: JobViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewJobBinding.inflate(inflater, container, false)

        viewModel.getEditJob()?.apply {
            binding.jobName.setText(this.name)
            binding.jobPosition.setText(this.position)
            binding.jobStart.setText(this.start)
            binding.jobFinish.setText(this.finish)
        }

        binding.jobStart.setOnClickListener {
            val datePickerFragment =
                DatePickerFragment { day, month, year ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month - 1)
                    selectedDate.set(Calendar.DAY_OF_MONTH, day)
                    val date = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(selectedDate.time)
                    binding.jobStart.setText(date)
                }
            datePickerFragment.show(childFragmentManager, "datePicker")
        }

        binding.jobFinish.setOnClickListener {
            val datePickerFragment =
                DatePickerFragment { day, month, year ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month - 1)
                    selectedDate.set(Calendar.DAY_OF_MONTH, day)
                    val date = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(selectedDate.time)
                    binding.jobFinish.setText(date)
                }
            datePickerFragment.show(childFragmentManager, "datePicker")
        }

        binding.createButton.setOnClickListener {
            val name = binding.jobName.text.toString()
            val position = binding.jobPosition.text.toString()
            val start = binding.jobStart.text.toString() + "T00:00:00.000000Z"
            val finish = if (binding.jobFinish.text?.isNotBlank() == true) {
                binding.jobFinish.text.toString() + "T00:00:00.000000Z"
            } else null
            viewModel.changeContent(name, position, start, finish, null)
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }

        viewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}