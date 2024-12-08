package com.example.bt12.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bt12.database.AppDatabase
import com.example.bt12.databinding.FragmentTransactionsBinding
import com.example.bt12.model.Transaction
import com.example.bt12.model.TransactionType
import com.example.bt12.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.util.Locale
import com.example.bt12.databinding.DialogAddTransactionBinding
import com.example.bt12.repository.TransactionRepository
import com.example.bt12.viewmodel.TransactionViewModelFactory

class TransactionsFragment : Fragment() {
    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transactionDao = AppDatabase.getDatabase(requireContext()).transactionDao()

        // Tạo Repository
        val repository = TransactionRepository(transactionDao)

        // Tạo Factory
        val factory = TransactionViewModelFactory(repository)

        // Khởi tạo ViewModel
        transactionViewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]

        // Thiết lập RecyclerView
        setupRecyclerView()

        // Nút thêm giao dịch
        binding.fabAddTransaction.setOnClickListener {
            showAddTransactionDialog()
        }

        // Observe danh sách giao dịch
        transactionViewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
            updateSummary(transactions)
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        binding.recyclerViewTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter

            // Thiết lập ItemTouchHelper để xử lý sự kiện kéo và xóa
            val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, // Chỉ định hướng kéo (Up, Down)
                ItemTouchHelper.LEFT // Chỉ cho phép kéo sang trái
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    // Chức năng kéo mục vào vị trí khác (nếu cần)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // Xử lý sự kiện khi mục bị xóa
                    val position = viewHolder.adapterPosition
                    val transaction = transactionAdapter.currentList[position]
                    if (direction == ItemTouchHelper.LEFT) {
                        // Nếu kéo sang trái, xóa giao dịch
                        transactionViewModel.delete(transaction)
                    }
                }
            }

            // Áp dụng ItemTouchHelper với RecyclerView
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
        }
    }

    private fun showAddTransactionDialog() {
        val dialogBinding = DialogAddTransactionBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Thêm Giao Dịch Mới")
            .setView(dialogBinding.root)
            .setPositiveButton("Thêm") { _, _ ->
                val amount = dialogBinding.etAmount.text.toString().toDoubleOrNull()
                val description = dialogBinding.etDescription.text.toString()
                val type = if (dialogBinding.rbExpense.isChecked)
                    TransactionType.EXPENSE
                else
                    TransactionType.INCOME
                if (amount != null) {
                    val transaction = Transaction(
                        amount = amount,
                        description = description,
                        type = type,
                        date = System.currentTimeMillis(),
                        categoryId = 1 // Tạm thời fix cứng
                    )
                    transactionViewModel.insert(transaction)
                }
            }
            .setNegativeButton("Hủy", null)
            .create()
        dialog.show()
    }

    private fun updateSummary(transactions: List<Transaction>) {
        val totalIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        val totalExpense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        binding.tvTotalIncome.text = formatCurrency(totalIncome)
        binding.tvTotalExpense.text = formatCurrency(totalExpense)
        binding.tvBalance.text = formatCurrency(totalIncome - totalExpense)
    }

    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
