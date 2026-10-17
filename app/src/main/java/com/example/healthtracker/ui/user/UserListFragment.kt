package com.example.healthtracker.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthtracker.HealthTrackerApplication
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentUserListBinding

class UserListFragment : Fragment() {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((activity?.application as HealthTrackerApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = UserListAdapter { user ->
            val action = UserListFragmentDirections.actionUserListFragmentToHealthRecordListFragment(user.id)
            findNavController().navigate(action)
        }
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        userViewModel.allUsers.observe(viewLifecycleOwner) { users ->
            users?.let { adapter.submitList(it) }
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_userListFragment_to_addUserFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
