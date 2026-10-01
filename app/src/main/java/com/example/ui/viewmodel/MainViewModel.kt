package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.RecurringTransactionEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.SplitExpenseEntity
import com.example.data.model.TransactionEntity
import com.example.data.repository.PocketLedgerRepository
import com.example.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class CategorySpending(
    val category: String,
    val amount: Double,
    val percentage: Float,
    val count: Int,
    val colorHex: String,
    val iconName: String
)

data class DailySpending(
    val dayOfMonth: Int,
    val label: String,
    val amount: Double
)

data class DashboardSummary(
    val currentBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val todaySpending: Double = 0.0,
    val weekSpending: Double = 0.0,
    val overallMonthlyBudget: Double = 0.0,
    val remainingBudget: Double = 0.0,
    val budgetPercentage: Float = 0.0f,
    val highestCategoryName: String = "",
    val highestCategoryAmount: Double = 0.0,
    val highestTransactionAmount: Double = 0.0,
    val dailyAverage: Double = 0.0,
    val insightText: String = "Track your daily spending to stay in control."
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository: PocketLedgerRepository
    val userPrefs: UserPreferencesRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = PocketLedgerRepository(db)
        userPrefs = UserPreferencesRepository(application)

        viewModelScope.launch {
            repository.ensureDefaultCategories()
        }
    }

    // --- Current Selected Month ("YYYY-MM") ---
    private val monthFormat = SimpleDateFormat("yyyy-MM", Locale.US)
    private val _selectedMonth = MutableStateFlow(monthFormat.format(Date()))
    val selectedMonth: StateFlow<String> = _selectedMonth.asStateFlow()

    // --- Search & Filters for Transactions Tab ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow("ALL") // "ALL", "EXPENSE", "INCOME"
    val filterType: StateFlow<String> = _filterType.asStateFlow()

    private val _filterCategory = MutableStateFlow("ALL")
    val filterCategory: StateFlow<String> = _filterCategory.asStateFlow()

    private val _filterPaymentMethod = MutableStateFlow("ALL")
    val filterPaymentMethod: StateFlow<String> = _filterPaymentMethod.asStateFlow()

    private val _sortOrder = MutableStateFlow("NEWEST") // "NEWEST", "OLDEST", "HIGHEST", "LOWEST"
    val sortOrder: StateFlow<String> = _sortOrder.asStateFlow()

    // Combined filter flow to keep combine clean and strongly typed
    private data class FilterState(
        val query: String,
        val type: String,
        val category: String,
        val paymentMethod: String,
        val sort: String
    )

    private val _filterState: Flow<FilterState> = combine(
        combine(_searchQuery, _filterType, _filterCategory) { q, t, c -> Triple(q, t, c) },
        combine(_filterPaymentMethod, _sortOrder) { m, s -> Pair(m, s) }
    ) { (q, t, c), (m, s) ->
        FilterState(q, t, c, m, s)
    }

    // --- Raw Flow Data from Room ---
    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBudgets: StateFlow<List<BudgetEntity>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGoals: StateFlow<List<SavingsGoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecurring: StateFlow<List<RecurringTransactionEntity>> = repository.allRecurring
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSplits: StateFlow<List<SplitExpenseEntity>> = repository.allSplits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- App Navigation and Dialog State ---
    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    fun unlockApp() {
        _isUnlocked.value = true
    }

    fun lockApp() {
        if (userPrefs.isPinEnabled.value) {
            _isUnlocked.value = false
        }
    }

    // --- Month Navigation ---
    fun previousMonth() {
        val cal = Calendar.getInstance()
        try {
            val parts = _selectedMonth.value.split("-")
            cal.set(Calendar.YEAR, parts[0].toInt())
            cal.set(Calendar.MONTH, parts[1].toInt() - 1)
            cal.add(Calendar.MONTH, -1)
            _selectedMonth.value = monthFormat.format(cal.time)
        } catch (_: Exception) {}
    }

    fun nextMonth() {
        val cal = Calendar.getInstance()
        try {
            val parts = _selectedMonth.value.split("-")
            cal.set(Calendar.YEAR, parts[0].toInt())
            cal.set(Calendar.MONTH, parts[1].toInt() - 1)
            cal.add(Calendar.MONTH, 1)
            _selectedMonth.value = monthFormat.format(cal.time)
        } catch (_: Exception) {}
    }

    fun setSelectedMonth(ym: String) {
        _selectedMonth.value = ym
    }

    fun getMonthDisplayName(ym: String): String {
        return try {
            val parts = ym.split("-")
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, parts[0].toInt())
            cal.set(Calendar.MONTH, parts[1].toInt() - 1)
            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
        } catch (_: Exception) {
            ym
        }
    }

    // --- Filter Setters ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(type: String) {
        _filterType.value = type
    }

    fun setFilterCategory(category: String) {
        _filterCategory.value = category
    }

    fun setFilterPaymentMethod(method: String) {
        _filterPaymentMethod.value = method
    }

    fun setSortOrder(order: String) {
        _sortOrder.value = order
    }

    // --- Filtered Transactions for Transactions Screen ---
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        _filterState
    ) { txs, filter ->
        var list = txs.filter { tx ->
            val matchesQuery = filter.query.isBlank() ||
                    tx.notes.contains(filter.query, ignoreCase = true) ||
                    tx.category.contains(filter.query, ignoreCase = true) ||
                    tx.amount.toString().contains(filter.query) ||
                    tx.paymentMethod.contains(filter.query, ignoreCase = true)
            val matchesType = filter.type == "ALL" || tx.type.equals(filter.type, ignoreCase = true)
            val matchesCategory = filter.category == "ALL" || tx.category.equals(filter.category, ignoreCase = true)
            val matchesMethod = filter.paymentMethod == "ALL" || tx.paymentMethod.equals(filter.paymentMethod, ignoreCase = true)
            matchesQuery && matchesType && matchesCategory && matchesMethod
        }

        list = when (filter.sort) {
            "OLDEST" -> list.sortedBy { it.date }
            "HIGHEST" -> list.sortedByDescending { it.amount }
            "LOWEST" -> list.sortedBy { it.amount }
            else -> list.sortedByDescending { it.date } // NEWEST
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Dashboard & Analytics Summary ---
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        allTransactions,
        allBudgets,
        _selectedMonth,
        userPrefs.startingBalance
    ) { txs, budgets, currentYm, startingBal ->
        val cal = Calendar.getInstance()
        val todayYear = cal.get(Calendar.YEAR)
        val todayMonth = cal.get(Calendar.MONTH)
        val todayDay = cal.get(Calendar.DAY_OF_MONTH)

        // Today start & end
        cal.set(todayYear, todayMonth, todayDay, 0, 0, 0)
        val todayStart = cal.timeInMillis
        cal.set(todayYear, todayMonth, todayDay, 23, 59, 59)
        val todayEnd = cal.timeInMillis

        // Week start (7 days ago)
        val weekStart = todayStart - 6 * 24 * 60 * 60 * 1000L

        var allTimeIncome = 0.0
        var allTimeExpense = 0.0
        var monthIncome = 0.0
        var monthExpense = 0.0
        var todayExpense = 0.0
        var weekExpense = 0.0
        var highestTx = 0.0
        val categoryExpenses = mutableMapOf<String, Double>()

        val txMonthFormat = SimpleDateFormat("yyyy-MM", Locale.US)

        for (tx in txs) {
            if (tx.type == "INCOME") {
                allTimeIncome += tx.amount
            } else {
                allTimeExpense += tx.amount
            }

            val txYm = txMonthFormat.format(Date(tx.date))
            if (txYm == currentYm) {
                if (tx.type == "INCOME") {
                    monthIncome += tx.amount
                } else {
                    monthExpense += tx.amount
                    categoryExpenses[tx.category] = (categoryExpenses[tx.category] ?: 0.0) + tx.amount
                    if (tx.amount > highestTx) {
                        highestTx = tx.amount
                    }
                }
            }

            if (tx.type == "EXPENSE") {
                if (tx.date in todayStart..todayEnd) {
                    todayExpense += tx.amount
                }
                if (tx.date in weekStart..todayEnd) {
                    weekExpense += tx.amount
                }
            }
        }

        val currentBalance = startingBal + allTimeIncome - allTimeExpense

        // Budget for current month
        val overallBudget = budgets.find { it.month == currentYm && it.category == "ALL" && it.isEnabled }
            ?.monthlyLimit ?: 0.0

        val remainingBudget = if (overallBudget > 0) overallBudget - monthExpense else 0.0
        val budgetPct = if (overallBudget > 0) ((monthExpense / overallBudget) * 100.0).toFloat().coerceIn(0f, 100f) else 0.0f

        // Highest Category
        val highestCat = categoryExpenses.maxByOrNull { it.value }
        val highestCatName = highestCat?.key ?: ""
        val highestCatAmt = highestCat?.value ?: 0.0

        // Daily average for month
        val daysInMonth = 30
        val dailyAvg = if (monthExpense > 0) monthExpense / daysInMonth else 0.0

        // Contextual Insight
        val currencySym = userPrefs.currencySymbol.value
        val insight = when {
            overallBudget > 0 && monthExpense > overallBudget ->
                "⚠️ Alert: You have exceeded your monthly budget by $currencySym${String.format(Locale.US, "%.0f", monthExpense - overallBudget)}."
            highestCatName.isNotEmpty() && monthExpense > 0 -> {
                val pct = ((highestCatAmt / monthExpense) * 100).toInt()
                "$highestCatName is your largest expense (${pct}% of spending this month)."
            }
            monthExpense > 0 ->
                "You have spent $currencySym${String.format(Locale.US, "%.0f", monthExpense)} so far in this period."
            else ->
                "All clear! Add transactions or tap Quick Add to log expenses effortlessly."
        }

        DashboardSummary(
            currentBalance = currentBalance,
            totalIncome = allTimeIncome,
            totalExpense = allTimeExpense,
            monthIncome = monthIncome,
            monthExpense = monthExpense,
            todaySpending = todayExpense,
            weekSpending = weekExpense,
            overallMonthlyBudget = overallBudget,
            remainingBudget = remainingBudget,
            budgetPercentage = budgetPct,
            highestCategoryName = highestCatName,
            highestCategoryAmount = highestCatAmt,
            highestTransactionAmount = highestTx,
            dailyAverage = dailyAvg,
            insightText = insight
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    // --- Category Spending for selected Month ---
    val monthCategoryBreakdown: StateFlow<List<CategorySpending>> = combine(
        allTransactions,
        allCategories,
        _selectedMonth
    ) { txs, cats, currentYm ->
        val txMonthFormat = SimpleDateFormat("yyyy-MM", Locale.US)
        val monthExpenses = txs.filter {
            it.type == "EXPENSE" && txMonthFormat.format(Date(it.date)) == currentYm
        }
        val totalExp = monthExpenses.sumOf { it.amount }
        val catMap = monthExpenses.groupBy { it.category }

        catMap.map { (catName, items) ->
            val sum = items.sumOf { it.amount }
            val meta = cats.find { it.name.equals(catName, ignoreCase = true) }
            val pct = if (totalExp > 0) ((sum / totalExp) * 100.0).toFloat() else 0f
            CategorySpending(
                category = catName,
                amount = sum,
                percentage = pct,
                count = items.size,
                colorHex = meta?.colorHex ?: "#2563EB",
                iconName = meta?.iconName ?: "category"
            )
        }.sortedByDescending { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Daily Spending for Selected Month ---
    val monthDailySpending: StateFlow<List<DailySpending>> = combine(
        allTransactions,
        _selectedMonth
    ) { txs, currentYm ->
        val cal = Calendar.getInstance()
        try {
            val parts = currentYm.split("-")
            cal.set(Calendar.YEAR, parts[0].toInt())
            cal.set(Calendar.MONTH, parts[1].toInt() - 1)
        } catch (_: Exception) {}

        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayMap = mutableMapOf<Int, Double>()
        for (d in 1..maxDays) {
            dayMap[d] = 0.0
        }

        val txMonthFormat = SimpleDateFormat("yyyy-MM", Locale.US)
        val txDayCal = Calendar.getInstance()

        for (tx in txs) {
            if (tx.type == "EXPENSE") {
                val ym = txMonthFormat.format(Date(tx.date))
                if (ym == currentYm) {
                    txDayCal.timeInMillis = tx.date
                    val day = txDayCal.get(Calendar.DAY_OF_MONTH)
                    dayMap[day] = (dayMap[day] ?: 0.0) + tx.amount
                }
            }
        }

        dayMap.map { (day, amount) ->
            DailySpending(
                dayOfMonth = day,
                label = day.toString(),
                amount = amount
            )
        }.sortedBy { it.dayOfMonth }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- CRUD Operations ---

    fun saveTransaction(
        id: Long = 0L,
        type: String,
        amount: Double,
        category: String,
        date: Long,
        notes: String,
        paymentMethod: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            if (amount <= 0.0) return@launch
            val tx = TransactionEntity(
                id = id,
                type = type,
                amount = amount,
                category = category,
                date = date,
                notes = notes.trim(),
                paymentMethod = paymentMethod
            )
            if (id == 0L) {
                repository.insertTransaction(tx)
            } else {
                repository.updateTransaction(tx)
            }
            onSuccess()
        }
    }

    fun deleteTransaction(transaction: TransactionEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            onComplete()
        }
    }

    // --- Budgets ---
    fun saveBudget(
        id: Long = 0L,
        category: String,
        month: String,
        limit: Double,
        isEnabled: Boolean = true,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            if (limit <= 0.0) return@launch
            val b = BudgetEntity(
                id = id,
                category = category,
                month = month,
                monthlyLimit = limit,
                isEnabled = isEnabled
            )
            if (id == 0L) {
                repository.insertBudget(b)
            } else {
                repository.updateBudget(b)
            }
            onSuccess()
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }

    fun toggleBudget(budget: BudgetEntity) {
        viewModelScope.launch {
            repository.updateBudget(budget.copy(isEnabled = !budget.isEnabled))
        }
    }

    // --- Savings Goals ---
    fun saveSavingsGoal(
        id: Long = 0L,
        name: String,
        targetAmount: Double,
        savedAmount: Double,
        targetDate: Long,
        notes: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            if (name.isBlank() || targetAmount <= 0.0) return@launch
            val goal = SavingsGoalEntity(
                id = id,
                name = name.trim(),
                targetAmount = targetAmount,
                savedAmount = savedAmount.coerceAtLeast(0.0),
                targetDate = targetDate,
                notes = notes.trim()
            )
            if (id == 0L) {
                repository.insertGoal(goal)
            } else {
                repository.updateGoal(goal)
            }
            onSuccess()
        }
    }

    fun addMoneyToGoal(goal: SavingsGoalEntity, amount: Double) {
        viewModelScope.launch {
            if (amount <= 0.0) return@launch
            repository.updateGoal(goal.copy(savedAmount = goal.savedAmount + amount))
        }
    }

    fun withdrawMoneyFromGoal(goal: SavingsGoalEntity, amount: Double) {
        viewModelScope.launch {
            if (amount <= 0.0) return@launch
            val newAmount = (goal.savedAmount - amount).coerceAtLeast(0.0)
            repository.updateGoal(goal.copy(savedAmount = newAmount))
        }
    }

    fun deleteSavingsGoal(goal: SavingsGoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    // --- Recurring ---
    fun saveRecurring(
        id: Long = 0L,
        title: String,
        type: String,
        amount: Double,
        category: String,
        frequency: String,
        nextDueDate: Long,
        paymentMethod: String,
        notes: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            if (title.isBlank() || amount <= 0.0) return@launch
            val item = RecurringTransactionEntity(
                id = id,
                title = title.trim(),
                type = type,
                amount = amount,
                category = category,
                frequency = frequency,
                nextDueDate = nextDueDate,
                paymentMethod = paymentMethod,
                notes = notes.trim(),
                isActive = true
            )
            if (id == 0L) {
                repository.insertRecurring(item)
            } else {
                repository.updateRecurring(item)
            }
            onSuccess()
        }
    }

    fun deleteRecurring(recurring: RecurringTransactionEntity) {
        viewModelScope.launch {
            repository.deleteRecurring(recurring)
        }
    }

    fun toggleRecurring(recurring: RecurringTransactionEntity) {
        viewModelScope.launch {
            repository.updateRecurring(recurring.copy(isActive = !recurring.isActive))
        }
    }

    // --- Splits ---
    fun saveSplit(
        id: Long = 0L,
        title: String,
        totalAmount: Double,
        peopleCount: Int,
        myShare: Double,
        payerName: String,
        participantsSummary: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            if (title.isBlank() || totalAmount <= 0.0 || peopleCount <= 0) return@launch
            val split = SplitExpenseEntity(
                id = id,
                title = title.trim(),
                totalAmount = totalAmount,
                numberOfPeople = peopleCount,
                myShare = myShare,
                payerName = payerName.trim(),
                participantsSummary = participantsSummary.trim()
            )
            if (id == 0L) {
                repository.insertSplit(split)
            } else {
                repository.updateSplit(split)
            }
            onSuccess()
        }
    }

    fun toggleSplitSettled(split: SplitExpenseEntity) {
        viewModelScope.launch {
            repository.updateSplit(split.copy(isSettled = !split.isSettled))
        }
    }

    fun deleteSplit(split: SplitExpenseEntity) {
        viewModelScope.launch {
            repository.deleteSplit(split)
        }
    }

    // --- Custom Categories ---
    fun addCategory(name: String, type: String, colorHex: String, iconName: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            if (name.isBlank()) return@launch
            repository.insertCategory(
                CategoryEntity(
                    name = name.trim(),
                    type = type,
                    iconName = iconName,
                    colorHex = colorHex,
                    isDefault = false
                )
            )
            onSuccess()
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            if (!category.isDefault) {
                repository.deleteCategory(category)
            }
        }
    }

    // --- Demo Data Seeder for quick testing ---
    fun seedDemoData(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val day = 24 * 60 * 60 * 1000L

            val demoList = listOf(
                TransactionEntity(type = "INCOME", amount = 65000.0, category = "Salary", date = now - 1 * day, notes = "Monthly Salary Deposit", paymentMethod = "BANK_TRANSFER"),
                TransactionEntity(type = "EXPENSE", amount = 14500.0, category = "Rent", date = now - 2 * day, notes = "Apartment Rent", paymentMethod = "UPI"),
                TransactionEntity(type = "EXPENSE", amount = 3200.0, category = "Groceries", date = now - 3 * day, notes = "Supermarket supplies", paymentMethod = "UPI"),
                TransactionEntity(type = "EXPENSE", amount = 850.0, category = "Food", date = now, notes = "Dinner with friends", paymentMethod = "UPI"),
                TransactionEntity(type = "EXPENSE", amount = 1200.0, category = "Bills", date = now - 4 * day, notes = "Electricity & Wi-Fi", paymentMethod = "CARD"),
                TransactionEntity(type = "EXPENSE", amount = 450.0, category = "Travel", date = now - 1 * day, notes = "Metro & Auto fare", paymentMethod = "CASH"),
                TransactionEntity(type = "INCOME", amount = 8000.0, category = "Freelance", date = now - 5 * day, notes = "Design project milestone", paymentMethod = "UPI"),
                TransactionEntity(type = "EXPENSE", amount = 2100.0, category = "Shopping", date = now - 6 * day, notes = "Sneakers sale", paymentMethod = "CARD")
            )
            for (item in demoList) {
                repository.insertTransaction(item)
            }

            // Also add demo budget
            val ym = _selectedMonth.value
            repository.insertBudget(
                BudgetEntity(category = "ALL", month = ym, monthlyLimit = 40000.0, isEnabled = true)
            )
            repository.insertBudget(
                BudgetEntity(category = "Food", month = ym, monthlyLimit = 8000.0, isEnabled = true)
            )

            // Add demo savings goal
            repository.insertGoal(
                SavingsGoalEntity(
                    name = "Emergency Fund",
                    targetAmount = 100000.0,
                    savedAmount = 35000.0,
                    targetDate = now + 180 * day,
                    notes = "6 months living expenses"
                )
            )
            repository.insertGoal(
                SavingsGoalEntity(
                    name = "New Laptop",
                    targetAmount = 75000.0,
                    savedAmount = 45000.0,
                    targetDate = now + 60 * day,
                    notes = "MacBook Air M3"
                )
            )

            // Add demo recurring
            repository.insertRecurring(
                RecurringTransactionEntity(
                    title = "Netflix & Spotify",
                    type = "EXPENSE",
                    amount = 699.0,
                    category = "Subscriptions",
                    frequency = "MONTHLY",
                    nextDueDate = now + 12 * day,
                    paymentMethod = "CARD",
                    notes = "Entertainment subscription"
                )
            )

            onComplete()
        }
    }

    fun clearAllData(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.clearAllTransactions()
            onComplete()
        }
    }
}
