package com.example.bt12.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bt12.databinding.FragmentBudgetBinding
import com.example.bt12.viewmodel.SharedViewModel

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Quan sát ngân sách từ SharedViewModel
        sharedViewModel.budget.observe(viewLifecycleOwner) { budget ->
            binding.tvBudgetTitle.text = "Ngân sách hiện tại: $budget VND"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}