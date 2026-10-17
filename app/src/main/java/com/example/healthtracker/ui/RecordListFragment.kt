package com.example.healthtracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.healthtracker.data.HealthRepository
import com.example.healthtracker.data.database.AppDatabase
import com.example.healthtracker.databinding.FragmentRecordListBinding
import com.example.healthtracker.ui.viewmodel.RecordListViewModel
import com.example.healthtracker.ui.viewmodel.ViewModelFactory

class RecordListFragment : Fragment() {

    private var _binding: FragmentRecordListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RecordListViewModel
    private lateinit var adapter: RecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val repository = HealthRepository(database.userDao(), database.healthRecordDao())
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(RecordListViewModel::class.java)

        viewModel.latestUser.observe(viewLifecycleOwner) { user ->
            if (user == null) {
                findNavController().navigate(R.id.action_to_addUserFragment)
            } else {
                adapter = RecordAdapter(viewModel, user.age)
                binding.recyclerView.adapter = adapter
                viewModel.getRecordsForUser(user.id).observe(viewLifecycleOwner) { records ->
                    adapter.submitList(records)
                }
            }
        }

        binding.fabAddRecord.setOnClickListener {
            viewModel.latestUser.value?.let { user ->
                val action = RecordListFragmentDirections.actionToAddRecordFragment(user.id)
                findNavController().navigate(action)
            }
        }

        binding.exportPdfButton.setOnClickListener {
            viewModel.latestUser.value?.let { user ->
                val recordsLiveData = viewModel.getRecordsForUser(user.id)
                recordsLiveData.observe(viewLifecycleOwner) { records ->
                    if (records.isNotEmpty()) {
                        lifecycleScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                            val pdfFile = PdfGenerator().generatePdf(requireContext(), records)
                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                pdfFile?.let {
                                    sharePdf(it)
                                }
                            }
                        }
                        recordsLiveData.removeObservers(viewLifecycleOwner) // Prevent multiple exports
                    }
                }
            }
        }
    }

    private fun sharePdf(file: java.io.File) {
        val uri = androidx.core.content.FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(android.content.Intent.EXTRA_STREAM, uri)
        intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(android.content.Intent.createChooser(intent, "PDF Paylaş"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
