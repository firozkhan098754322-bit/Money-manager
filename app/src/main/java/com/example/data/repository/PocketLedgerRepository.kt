package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.RecurringTransactionEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.SplitExpenseEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PocketLedgerRepository(private val database: AppDatabase) {

    private val transactionDao = database.transactionDao()
    private val categoryDao = database.categoryDao()
    private val budgetDao = database.budgetDao()
    private val savingsGoalDao = database.savingsGoalDao()
    private val recurringDao = database.recurringTransactionDao()
    private val splitDao = database.splitExpenseDao()

    // --- Transactions ---
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate)

    fun searchTransactions(query: String): Flow<List<TransactionEntity>> =
        transactionDao.searchTransactions(query)

    suspend fun getTransactionById(id: Long): TransactionEntity? =
        transactionDao.getTransactionById(id)

    suspend fun insertTransaction(transaction: TransactionEntity): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        transactionDao.deleteTransaction(transaction)

    suspend fun deleteTransactionById(id: Long) =
        transactionDao.deleteTransactionById(id)

    suspend fun clearAllTransactions() =
        transactionDao.deleteAll()

    // --- Categories ---
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    fun getCategoriesByType(type: String): Flow<List<CategoryEntity>> =
        categoryDao.getCategoriesByType(type)

    suspend fun insertCategory(category: CategoryEntity): Long =
        categoryDao.insertCategory(category)

    suspend fun updateCategory(category: CategoryEntity) =
        categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: CategoryEntity) =
        categoryDao.deleteCategory(category)

    suspend fun ensureDefaultCategories() {
        withContext(Dispatchers.IO) {
            val count = categoryDao.getCount()
            if (count == 0) {
                categoryDao.insertCategories(
                    AppDatabase.DEFAULT_EXPENSE_CATEGORIES + AppDatabase.DEFAULT_INCOME_CATEGORIES
                )
            }
        }
    }

    // --- Budgets ---
    fun getBudgetsForMonth(month: String): Flow<List<BudgetEntity>> =
        budgetDao.getBudgetsForMonth(month)

    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()

    suspend fun insertBudget(budget: BudgetEntity): Long =
        budgetDao.insertBudget(budget)

    suspend fun updateBudget(budget: BudgetEntity) =
        budgetDao.updateBudget(budget)

    suspend fun deleteBudget(budget: BudgetEntity) =
        budgetDao.deleteBudget(budget)

    suspend fun deleteBudgetById(id: Long) =
        budgetDao.deleteBudgetById(id)

    // --- Savings Goals ---
    val allGoals: Flow<List<SavingsGoalEntity>> = savingsGoalDao.getAllGoals()

    suspend fun insertGoal(goal: SavingsGoalEntity): Long =
        savingsGoalDao.insertGoal(goal)

    suspend fun updateGoal(goal: SavingsGoalEntity) =
        savingsGoalDao.updateGoal(goal)

    suspend fun deleteGoal(goal: SavingsGoalEntity) =
        savingsGoalDao.deleteGoal(goal)

    suspend fun deleteGoalById(id: Long) =
        savingsGoalDao.deleteGoalById(id)

    // --- Recurring Transactions ---
    val allRecurring: Flow<List<RecurringTransactionEntity>> = recurringDao.getAllRecurring()
    val activeRecurring: Flow<List<RecurringTransactionEntity>> = recurringDao.getActiveRecurring()

    suspend fun insertRecurring(recurring: RecurringTransactionEntity): Long =
        recurringDao.insertRecurring(recurring)

    suspend fun updateRecurring(recurring: RecurringTransactionEntity) =
        recurringDao.updateRecurring(recurring)

    suspend fun deleteRecurring(recurring: RecurringTransactionEntity) =
        recurringDao.deleteRecurring(recurring)

    // --- Split Expenses ---
    val allSplits: Flow<List<SplitExpenseEntity>> = splitDao.getAllSplits()

    suspend fun insertSplit(split: SplitExpenseEntity): Long =
        splitDao.insertSplit(split)

    suspend fun updateSplit(split: SplitExpenseEntity) =
        splitDao.updateSplit(split)

    suspend fun deleteSplit(split: SplitExpenseEntity) =
        splitDao.deleteSplit(split)

    // --- CSV Export ---
    suspend fun exportToCsv(transactions: List<TransactionEntity>): String = withContext(Dispatchers.IO) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val sb = java.lang.StringBuilder()
        sb.append("ID,Type,Amount,Category,Date,PaymentMethod,Notes\n")
        for (tx in transactions) {
            val dateStr = dateFormat.format(Date(tx.date))
            val safeNotes = tx.notes.replace("\"", "\"\"")
            sb.append("${tx.id},${tx.type},${tx.amount},\"${tx.category}\",\"$dateStr\",${tx.paymentMethod},\"$safeNotes\"\n")
        }
        sb.toString()
    }

    // --- JSON Backup Export ---
    suspend fun exportBackupJson(
        transactions: List<TransactionEntity>,
        budgets: List<BudgetEntity>,
        goals: List<SavingsGoalEntity>
    ): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("app", "PocketLedger")
        root.put("version", 1)
        root.put("exportTime", System.currentTimeMillis())

        // Transactions
        val txArray = JSONArray()
        for (tx in transactions) {
            val obj = JSONObject()
            obj.put("id", tx.id)
            obj.put("type", tx.type)
            obj.put("amount", tx.amount)
            obj.put("category", tx.category)
            obj.put("date", tx.date)
            obj.put("notes", tx.notes)
            obj.put("paymentMethod", tx.paymentMethod)
            txArray.put(obj)
        }
        root.put("transactions", txArray)

        // Budgets
        val budgetArray = JSONArray()
        for (b in budgets) {
            val obj = JSONObject()
            obj.put("id", b.id)
            obj.put("category", b.category)
            obj.put("month", b.month)
            obj.put("monthlyLimit", b.monthlyLimit)
            obj.put("isEnabled", b.isEnabled)
            budgetArray.put(obj)
        }
        root.put("budgets", budgetArray)

        // Savings Goals
        val goalArray = JSONArray()
        for (g in goals) {
            val obj = JSONObject()
            obj.put("id", g.id)
            obj.put("name", g.name)
            obj.put("targetAmount", g.targetAmount)
            obj.put("savedAmount", g.savedAmount)
            obj.put("targetDate", g.targetDate)
            obj.put("notes", g.notes)
            goalArray.put(obj)
        }
        root.put("savingsGoals", goalArray)

        root.toString(2)
    }

    // --- JSON Backup Restore ---
    suspend fun restoreFromJson(jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (!root.has("transactions")) {
                return@withContext Result.failure(IllegalArgumentException("Invalid PocketLedger backup format"))
            }

            val txArray = root.getJSONArray("transactions")
            val transactions = mutableListOf<TransactionEntity>()
            for (i in 0 until txArray.length()) {
                val obj = txArray.getJSONObject(i)
                transactions.add(
                    TransactionEntity(
                        id = 0L, // autogenerate clean IDs
                        type = obj.getString("type"),
                        amount = obj.getDouble("amount"),
                        category = obj.getString("category"),
                        date = obj.getLong("date"),
                        notes = obj.optString("notes", ""),
                        paymentMethod = obj.optString("paymentMethod", "UPI"),
                        createdAt = System.currentTimeMillis()
                    )
                )
            }

            // Optional budgets restore
            if (root.has("budgets")) {
                val budgetArray = root.getJSONArray("budgets")
                for (i in 0 until budgetArray.length()) {
                    val obj = budgetArray.getJSONObject(i)
                    budgetDao.insertBudget(
                        BudgetEntity(
                            id = 0L,
                            category = obj.getString("category"),
                            month = obj.getString("month"),
                            monthlyLimit = obj.getDouble("monthlyLimit"),
                            isEnabled = obj.optBoolean("isEnabled", true)
                        )
                    )
                }
            }

            // Optional goals restore
            if (root.has("savingsGoals")) {
                val goalArray = root.getJSONArray("savingsGoals")
                for (i in 0 until goalArray.length()) {
                    val obj = goalArray.getJSONObject(i)
                    savingsGoalDao.insertGoal(
                        SavingsGoalEntity(
                            id = 0L,
                            name = obj.getString("name"),
                            targetAmount = obj.getDouble("targetAmount"),
                            savedAmount = obj.optDouble("savedAmount", 0.0),
                            targetDate = obj.optLong("targetDate", 0L),
                            notes = obj.optString("notes", "")
                        )
                    )
                }
            }

            transactionDao.insertAll(transactions)
            Result.success(transactions.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
