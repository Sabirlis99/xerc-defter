package com.subhan.xercdefteri.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subhan.xercdefteri.data.AppDatabase
import com.subhan.xercdefteri.data.Category
import com.subhan.xercdefteri.data.Expense
import com.subhan.xercdefteri.data.ExpenseDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class CategoryTotal(val category: Category, val total: Double)

data class ExpenseUiState(
    val viewMonth: YearMonth = YearMonth.now(),
    val monthExpenses: List<Expense> = emptyList(),
    val monthTotal: Double = 0.0,
    val breakdown: List<CategoryTotal> = emptyList(),
    val groupedByDate: List<Pair<LocalDate, List<Expense>>> = emptyList()
)

class ExpenseViewModel(private val dao: ExpenseDao) : ViewModel() {

    private val viewMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<ExpenseUiState> = combine(
        dao.getAll(),
        viewMonth
    ) { all, month ->
        val monthExpenses = all.filter {
            val d = LocalDate.parse(it.dateIso)
            YearMonth.from(d) == month
        }
        val total = monthExpenses.sumOf { it.amount }
        val breakdown = Category.values().map { cat ->
            CategoryTotal(cat, monthExpenses.filter { it.categoryId == cat.id }.sumOf { it.amount })
        }.filter { it.total > 0.0 }.sortedByDescending { it.total }

        val grouped = monthExpenses
            .groupBy { LocalDate.parse(it.dateIso) }
            .toList()
            .sortedByDescending { it.first }

        ExpenseUiState(
            viewMonth = month,
            monthExpenses = monthExpenses,
            monthTotal = total,
            breakdown = breakdown,
            groupedByDate = grouped
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExpenseUiState())

    fun shiftMonth(delta: Long) {
        viewMonth.value = viewMonth.value.plusMonths(delta)
    }

    fun addExpense(amount: Double, category: Category, note: String, date: LocalDate) {
        viewModelScope.launch {
            dao.insert(
                Expense(
                    amount = amount,
                    categoryId = category.id,
                    note = note.trim(),
                    dateIso = date.toString()
                )
            )
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch { dao.delete(expense) }
    }

    companion object {
        fun factory(dao: ExpenseDao) = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ExpenseViewModel(dao) as T
            }
        }
    }
}
