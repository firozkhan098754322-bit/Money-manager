package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.RecurringTransactionEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.SplitExpenseEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        SavingsGoalEntity::class,
        RecurringTransactionEntity::class,
        SplitExpenseEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun recurringTransactionDao(): RecurringTransactionDao
    abstract fun splitExpenseDao(): SplitExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pocketledger_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val DEFAULT_EXPENSE_CATEGORIES = listOf(
            CategoryEntity(name = "Food", type = "EXPENSE", iconName = "restaurant", colorHex = "#F59E0B"),
            CategoryEntity(name = "Travel", type = "EXPENSE", iconName = "commute", colorHex = "#3B82F6"),
            CategoryEntity(name = "Shopping", type = "EXPENSE", iconName = "shopping_bag", colorHex = "#EC4899"),
            CategoryEntity(name = "Bills", type = "EXPENSE", iconName = "receipt_long", colorHex = "#EF4444"),
            CategoryEntity(name = "Education", type = "EXPENSE", iconName = "school", colorHex = "#8B5CF6"),
            CategoryEntity(name = "Health", type = "EXPENSE", iconName = "medical_services", colorHex = "#10B981"),
            CategoryEntity(name = "Entertainment", type = "EXPENSE", iconName = "sports_esports", colorHex = "#6366F1"),
            CategoryEntity(name = "Groceries", type = "EXPENSE", iconName = "shopping_cart", colorHex = "#14B8A6"),
            CategoryEntity(name = "Rent", type = "EXPENSE", iconName = "home", colorHex = "#0EA5E9"),
            CategoryEntity(name = "Subscriptions", type = "EXPENSE", iconName = "subscriptions", colorHex = "#A855F7"),
            CategoryEntity(name = "Other", type = "EXPENSE", iconName = "more_horiz", colorHex = "#64748B")
        )

        val DEFAULT_INCOME_CATEGORIES = listOf(
            CategoryEntity(name = "Salary", type = "INCOME", iconName = "payments", colorHex = "#10B981"),
            CategoryEntity(name = "Freelance", type = "INCOME", iconName = "laptop", colorHex = "#06B6D4"),
            CategoryEntity(name = "Business", type = "INCOME", iconName = "storefront", colorHex = "#3B82F6"),
            CategoryEntity(name = "Gift", type = "INCOME", iconName = "card_giftcard", colorHex = "#EC4899"),
            CategoryEntity(name = "Other Income", type = "INCOME", iconName = "savings", colorHex = "#8B5CF6")
        )

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialCategories(database.categoryDao())
                    }
                }
            }

            suspend fun populateInitialCategories(dao: CategoryDao) {
                dao.insertCategories(DEFAULT_EXPENSE_CATEGORIES + DEFAULT_INCOME_CATEGORIES)
            }
        }
    }
}
