package ru.netology.community.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.community.R
import ru.netology.community.adapter.JobAdapter
import ru.netology.community.adapter.Listener
import ru.netology.community.databinding.FragmentJobsBinding
import ru.netology.community.dto.Job
import ru.netology.community.viewmodel.AuthViewModel
import ru.netology.community.viewmodel.JobViewModel

class JobsFragment : Fragment() {

    private val jobViewModel by activityViewModels<JobViewModel>()
    private val authViewModel by activityViewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentJobsBinding.inflate(inflater, container, false)

        binding.addJob.setOnClickListener {
            findNavController().navigate(R.id.action_jobsFragment_to_newJobFragment)
        }

        val userId = requireArguments().getInt(USER_ID)
        jobViewModel.getJobsByUserId(userId)

        val adapter = JobAdapter(object : Listener {
            override fun onEdit(job: Job) {
                findNavController().navigate(R.id.action_jobsFragment_to_newJobFragment)
                jobViewModel.edit(job)
            }

            override fun onRemove(job: Job) {
                jobViewModel.removeById(job.id)
            }

        })

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.listJobs.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                jobViewModel.data.collectLatest {
                    adapter.submitList(it)
                    binding.emptyText.isVisible = it.isEmpty()
                }
            }
        }

        jobViewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            binding.addJob.isVisible = it.id == userId
        }

        return binding.root
    }

    companion object {
        const val USER_ID = "USER_ID"
    }

}