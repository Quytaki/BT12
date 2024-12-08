package com.example.bt12.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    // LiveData cho thu nhập, chi tiêu và ngân sách
    private val _income = MutableLiveData<Double>(0.0) // Tổng thu nhập
    private val _expense = MutableLiveData<Double>(0.0) // Tổng chi tiêu
    private val _budget = MutableLiveData<Double>(0.0) // Ngân sách hiện tại

    val income: LiveData<Double> get() = _income
    val expense: LiveData<Double> get() = _expense
    val budget: LiveData<Double> get() = _budget

    // Cập nhật tổng thu nhập từ danh sách giao dịch
    fun updateIncome(newIncome: Double) {
        _income.value = newIncome
        calculateBudget()
    }

    // Cập nhật tổng chi tiêu từ danh sách giao dịch
    fun updateExpense(newExpense: Double) {
        _expense.value = newExpense
        calculateBudget()
    }

    // Tính toán ngân sách: thu nhập - chi tiêu
    private fun calculateBudget() {
        val currentIncome = _income.value ?: 0.0
        val currentExpense = _expense.value ?: 0.0
        _budget.value = currentIncome - currentExpense
    }
}
