package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.BudgetEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.ui.components.AddBudgetDialog
import com.example.ui.components.AddEditTransactionDialog
import com.example.ui.components.AddRecurringDialog
import com.example.ui.components.AddSavingsGoalDialog
import com.example.ui.components.AddSplitExpenseDialog
import com.example.ui.components.GoalActionDialog
import com.example.ui.components.MainTab
import com.example.ui.components.ManageRecurringDialog
import com.example.ui.components.ManageSplitsDialog
import com.example.ui.components.PinLockScreen
import com.example.ui.components.PocketLedgerBottomBar
import com.example.ui.components.PocketLedgerTopBar
import com.example.ui.components.QuickAddDialog
import com.example.ui.screens.budget.BudgetAndGoalsScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.reports.ReportsScreen
import com.example.ui.screens.tools.ToolsAndSettingsScreen
import com.example.ui.screens.transactions.TransactionsScreen
import com.example.ui.theme.PocketLedgerTheme
import com.example.ui.theme.PocketPrimary
import com.example.ui.theme.PocketWhite
import com.example.ui.viewmodel.MainViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocketLedgerTheme {
                PocketLedgerApp()
            }
        }
    }
}

@Composable
fun PocketLedgerApp(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // User preferences & app lock state
    val isOnboardingDone by viewModel.userPrefs.isOnboardingCompleted.collectAsStateWithLifecycle()
    val isPinEnabled by viewModel.userPrefs.isPinEnabled.collectAsStateWithLifecycle()
    val pinCode by viewModel.userPrefs.pinCode.collectAsStateWithLifecycle()
    val isUnlocked by viewModel.isUnlocked.collectAsStateWithLifecycle()

    val currencySymbol by viewModel.userPrefs.currencySymbol.collectAsStateWithLifecycle()
    val currencyCode by viewModel.userPrefs.currencyCode.collectAsStateWithLifecycle()
    val useIndianFormat by viewModel.userPrefs.useIndianFormat.collectAsStateWithLifecycle()
    val startingBalance by viewModel.userPrefs.startingBalance.collectAsStateWithLifecycle()

    // Navigation Tab state
    var currentTab by remember { mutableStateOf(MainTab.HOME) }

    // Back button behavior: go back to Home tab if not already on Home
    BackHandler(enabled = currentTab != MainTab.HOME) {
        currentTab = MainTab.HOME
    }

    // Dialog & Modal visibility states
    var showAddEditTxDialog by remember { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
    var showQuickAddDialog by remember { mutableStateOf(false) }

    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var editingBudget by remember { mutableStateOf<BudgetEntity?>(null) }

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<SavingsGoalEntity?>(null) }
    var goalActionTarget by remember { mutableStateOf<Pair<SavingsGoalEntity, Boolean>?>(null) } // Pair(Goal, isDeposit)

    var showManageRecurring by remember { mutableStateOf(false) }
    var showAddRecurringDialog by remember { mutableStateOf(false) }

    var showManageSplits by remember { mutableStateOf(false) }
    var showAddSplitDialog by remember { mutableStateOf(false) }

    // Data from ViewModel
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val categories by viewModel.allCategories.collectAsStateWithLifecycle()
    val budgets by viewModel.allBudgets.collectAsStateWithLifecycle()
    val savingsGoals by viewModel.allGoals.collectAsStateWithLifecycle()
    val recurringItems by viewModel.allRecurring.collectAsStateWithLifecycle()
    val splitItems by viewModel.allSplits.collectAsStateWithLifecycle()

    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val selectedMonthName = viewModel.getMonthDisplayName(selectedMonth)
    val dashboardSummary by viewModel.dashboardSummary.collectAsStateWithLifecycle()
    val monthCategoryBreakdown by viewModel.monthCategoryBreakdown.collectAsStateWithLifecycle()
    val monthDailySpending by viewModel.monthDailySpending.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterType by viewModel.filterType.collectAsStateWithLifecycle()
    val filterCategory by viewModel.filterCategory.collectAsStateWithLifecycle()
    val filterPaymentMethod by viewModel.filterPaymentMethod.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()

    // 1. PIN Lock Screen check
    if (isPinEnabled && !isUnlocked) {
        PinLockScreen(
            correctPin = pinCode,
            onSuccess = { viewModel.unlockApp() }
        )
        return
    }

    // 2. Onboarding Flow check
    if (!isOnboardingDone) {
        OnboardingScreen(
            onComplete = { symbol, code, initBal ->
                viewModel.userPrefs.setCurrency(symbol, code)
                viewModel.userPrefs.setStartingBalance(initBal)
                viewModel.userPrefs.completeOnboarding()
                scope.launch {
                    snackbarHostState.showSnackbar("Welcome to PocketLedger! 100% Offline & Private.")
                }
            }
        )
        return
    }

    // 3. Main App Scaffold
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            PocketLedgerTopBar(
                onQuickAddClick = { showQuickAddDialog = true },
                onLockClick = if (isPinEnabled) { { viewModel.lockApp() } } else null
            )
        },
        bottomBar = {
            PocketLedgerBottomBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        },
        floatingActionButton = {
            if (currentTab == MainTab.HOME || currentTab == MainTab.TRANSACTIONS) {
                ExtendedFloatingActionButton(
                    onClick = {
                        editingTransaction = null
                        showAddEditTxDialog = true
                    },
                    containerColor = PocketPrimary,
                    contentColor = PocketWhite,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Entry",
                            tint = PocketWhite
                        )
                    },
                    text = {
                        Text(
                            text = "Add Entry",
                            fontWeight = FontWeight.Bold,
                            color = PocketWhite
                        )
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        when (currentTab) {
            MainTab.HOME -> {
                DashboardScreen(
                    summary = dashboardSummary,
                    selectedMonthName = selectedMonthName,
                    recentTransactions = allTransactions,
                    categorySpending = monthCategoryBreakdown,
                    dailySpending = monthDailySpending,
                    savingsGoals = savingsGoals,
                    currencySymbol = currencySymbol,
                    formatCurrency = { viewModel.userPrefs.formatCurrency(it) },
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() },
                    onAddTransactionClick = {
                        editingTransaction = null
                        showAddEditTxDialog = true
                    },
                    onQuickAddClick = { showQuickAddDialog = true },
                    onViewAllTransactions = { currentTab = MainTab.TRANSACTIONS },
                    onManageBudgetClick = { currentTab = MainTab.BUDGET },
                    onTransactionClick = { tx ->
                        editingTransaction = tx
                        showAddEditTxDialog = true
                    },
                    onSeedDemoData = {
                        viewModel.seedDemoData {
                            scope.launch {
                                snackbarHostState.showSnackbar("Sample demo transactions loaded successfully!")
                            }
                        }
                    },
                    modifier = screenModifier
                )
            }

            MainTab.TRANSACTIONS -> {
                TransactionsScreen(
                    transactions = filteredTransactions,
                    categories = categories,
                    searchQuery = searchQuery,
                    filterType = filterType,
                    filterCategory = filterCategory,
                    filterPaymentMethod = filterPaymentMethod,
                    sortOrder = sortOrder,
                    formatCurrency = { viewModel.userPrefs.formatCurrency(it) },
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onFilterTypeChange = { viewModel.setFilterType(it) },
                    onFilterCategoryChange = { viewModel.setFilterCategory(it) },
                    onFilterPaymentMethodChange = { viewModel.setFilterPaymentMethod(it) },
                    onSortOrderChange = { viewModel.setSortOrder(it) },
                    onTransactionClick = { tx ->
                        editingTransaction = tx
                        showAddEditTxDialog = true
                    },
                    onDeleteTransaction = { tx ->
                        viewModel.deleteTransaction(tx) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Transaction deleted successfully")
                            }
                        }
                    },
                    onAddTransactionClick = {
                        editingTransaction = null
                        showAddEditTxDialog = true
                    },
                    modifier = screenModifier
                )
            }

            MainTab.BUDGET -> {
                BudgetAndGoalsScreen(
                    budgets = budgets,
                    savingsGoals = savingsGoals,
                    transactions = allTransactions,
                    selectedMonth = selectedMonth,
                    selectedMonthName = selectedMonthName,
                    currencySymbol = currencySymbol,
                    formatCurrency = { viewModel.userPrefs.formatCurrency(it) },
                    onAddBudgetClick = {
                        editingBudget = null
                        showAddBudgetDialog = true
                    },
                    onEditBudgetClick = { b ->
                        editingBudget = b
                        showAddBudgetDialog = true
                    },
                    onToggleBudget = { viewModel.toggleBudget(it) },
                    onDeleteBudget = { viewModel.deleteBudget(it) },
                    onAddGoalClick = {
                        editingGoal = null
                        showAddGoalDialog = true
                    },
                    onEditGoalClick = { g ->
                        editingGoal = g
                        showAddGoalDialog = true
                    },
                    onAddMoneyToGoal = { g ->
                        goalActionTarget = Pair(g, true)
                    },
                    onWithdrawMoneyFromGoal = { g ->
                        goalActionTarget = Pair(g, false)
                    },
                    onDeleteGoal = { viewModel.deleteSavingsGoal(it) },
                    modifier = screenModifier
                )
            }

            MainTab.REPORTS -> {
                ReportsScreen(
                    summary = dashboardSummary,
                    selectedMonth = selectedMonth,
                    selectedMonthName = selectedMonthName,
                    transactions = allTransactions,
                    categorySpending = monthCategoryBreakdown,
                    dailySpending = monthDailySpending,
                    currencySymbol = currencySymbol,
                    formatCurrency = { viewModel.userPrefs.formatCurrency(it) },
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() },
                    modifier = screenModifier
                )
            }

            MainTab.SETTINGS -> {
                ToolsAndSettingsScreen(
                    currencySymbol = currencySymbol,
                    currencyCode = currencyCode,
                    useIndianFormat = useIndianFormat,
                    startingBalance = startingBalance,
                    isPinEnabled = isPinEnabled,
                    recurringItems = recurringItems,
                    splitItems = splitItems,
                    categories = categories,
                    formatCurrency = { viewModel.userPrefs.formatCurrency(it) },
                    onSetCurrency = { sym, code ->
                        viewModel.userPrefs.setCurrency(sym, code)
                    },
                    onToggleIndianFormat = {
                        viewModel.userPrefs.setIndianFormat(it)
                    },
                    onSetStartingBalance = {
                        viewModel.userPrefs.setStartingBalance(it)
                    },
                    onSetPin = { pin ->
                        viewModel.userPrefs.setPin(pin)
                        scope.launch {
                            snackbarHostState.showSnackbar("4-digit PIN protection enabled!")
                        }
                    },
                    onDisablePin = {
                        viewModel.userPrefs.disablePin()
                        scope.launch {
                            snackbarHostState.showSnackbar("PIN lock disabled")
                        }
                    },
                    onOpenRecurring = { showManageRecurring = true },
                    onOpenSplit = { showManageSplits = true },
                    onExportCsv = {
                        scope.launch {
                            val csvContent = viewModel.repository.exportToCsv(allTransactions)
                            shareText(context, csvContent, "Share PocketLedger Transactions CSV")
                        }
                    },
                    onExportBackupJson = {
                        scope.launch {
                            val jsonContent = viewModel.repository.exportBackupJson(allTransactions, budgets, savingsGoals)
                            shareText(context, jsonContent, "Share PocketLedger JSON Backup")
                        }
                    },
                    onRestoreBackupJson = { jsonStr ->
                        scope.launch {
                            val result = viewModel.repository.restoreFromJson(jsonStr)
                            if (result.isSuccess) {
                                snackbarHostState.showSnackbar("Restored ${result.getOrNull()} transactions successfully!")
                            } else {
                                snackbarHostState.showSnackbar("Failed to parse backup: ${result.exceptionOrNull()?.localizedMessage}")
                            }
                        }
                    },
                    onAddCategory = { name, type ->
                        viewModel.addCategory(name, type, "#2563EB", "category") {
                            scope.launch {
                                snackbarHostState.showSnackbar("Category '$name' added!")
                            }
                        }
                    },
                    onDeleteCategory = { cat ->
                        viewModel.deleteCategory(cat)
                    },
                    onLoadDemoData = {
                        viewModel.seedDemoData {
                            scope.launch {
                                snackbarHostState.showSnackbar("Sample demo data loaded!")
                            }
                        }
                    },
                    onClearAllData = {
                        viewModel.clearAllData {
                            scope.launch {
                                snackbarHostState.showSnackbar("All transactions cleared")
                            }
                        }
                    },
                    modifier = screenModifier
                )
            }
        }
    }

    // Dialog: Add / Edit Transaction
    if (showAddEditTxDialog) {
        AddEditTransactionDialog(
            initialTransaction = editingTransaction,
            categories = categories,
            currencySymbol = currencySymbol,
            onDismiss = {
                showAddEditTxDialog = false
                editingTransaction = null
            },
            onSave = { type, amount, category, date, notes, paymentMethod ->
                viewModel.saveTransaction(
                    id = editingTransaction?.id ?: 0L,
                    type = type,
                    amount = amount,
                    category = category,
                    date = date,
                    notes = notes,
                    paymentMethod = paymentMethod,
                    onSuccess = {
                        showAddEditTxDialog = false
                        editingTransaction = null
                        scope.launch {
                            snackbarHostState.showSnackbar("Transaction recorded successfully!")
                        }
                    }
                )
            },
            onDelete = { tx ->
                viewModel.deleteTransaction(tx) {
                    showAddEditTxDialog = false
                    editingTransaction = null
                    scope.launch {
                        snackbarHostState.showSnackbar("Transaction deleted")
                    }
                }
            }
        )
    }

    // Dialog: Quick Add
    if (showQuickAddDialog) {
        QuickAddDialog(
            categories = categories,
            currencySymbol = currencySymbol,
            onDismiss = { showQuickAddDialog = false },
            onQuickSave = { amount, category, paymentMethod ->
                viewModel.saveTransaction(
                    type = "EXPENSE",
                    amount = amount,
                    category = category,
                    date = System.currentTimeMillis(),
                    notes = "Quick Add",
                    paymentMethod = paymentMethod,
                    onSuccess = {
                        showQuickAddDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Quick Expense ($currencySymbol$amount) recorded!")
                        }
                    }
                )
            }
        )
    }

    // Dialog: Add / Edit Budget
    if (showAddBudgetDialog) {
        AddBudgetDialog(
            initialBudget = editingBudget,
            categories = categories,
            currentMonth = selectedMonth,
            currencySymbol = currencySymbol,
            onDismiss = {
                showAddBudgetDialog = false
                editingBudget = null
            },
            onSave = { category, limit ->
                viewModel.saveBudget(
                    id = editingBudget?.id ?: 0L,
                    category = category,
                    month = selectedMonth,
                    limit = limit,
                    onSuccess = {
                        showAddBudgetDialog = false
                        editingBudget = null
                        scope.launch {
                            snackbarHostState.showSnackbar("Budget limit saved!")
                        }
                    }
                )
            }
        )
    }

    // Dialog: Add / Edit Savings Goal
    if (showAddGoalDialog) {
        AddSavingsGoalDialog(
            initialGoal = editingGoal,
            currencySymbol = currencySymbol,
            onDismiss = {
                showAddGoalDialog = false
                editingGoal = null
            },
            onSave = { name, targetAmount, initialSaved, notes ->
                viewModel.saveSavingsGoal(
                    id = editingGoal?.id ?: 0L,
                    name = name,
                    targetAmount = targetAmount,
                    savedAmount = initialSaved,
                    targetDate = 0L,
                    notes = notes,
                    onSuccess = {
                        showAddGoalDialog = false
                        editingGoal = null
                        scope.launch {
                            snackbarHostState.showSnackbar("Savings goal saved!")
                        }
                    }
                )
            }
        )
    }

    // Dialog: Deposit or Withdraw from Goal
    if (goalActionTarget != null) {
        val (goal, isDeposit) = goalActionTarget!!
        GoalActionDialog(
            goal = goal,
            isDeposit = isDeposit,
            currencySymbol = currencySymbol,
            onDismiss = { goalActionTarget = null },
            onConfirm = { amount ->
                if (isDeposit) {
                    viewModel.addMoneyToGoal(goal, amount)
                    scope.launch {
                        snackbarHostState.showSnackbar("Added $currencySymbol$amount to '${goal.name}'!")
                    }
                } else {
                    viewModel.withdrawMoneyFromGoal(goal, amount)
                    scope.launch {
                        snackbarHostState.showSnackbar("Withdrew $currencySymbol$amount from '${goal.name}'")
                    }
                }
                goalActionTarget = null
            }
        )
    }

    // Dialog: Manage Recurring
    if (showManageRecurring) {
        ManageRecurringDialog(
            recurringList = recurringItems,
            currencySymbol = currencySymbol,
            formatCurrency = { viewModel.userPrefs.formatCurrency(it) },
            onDismiss = { showManageRecurring = false },
            onAddNew = {
                showAddRecurringDialog = true
            },
            onToggle = { viewModel.toggleRecurring(it) },
            onDelete = { viewModel.deleteRecurring(it) }
        )
    }

    // Dialog: Add New Recurring
    if (showAddRecurringDialog) {
        AddRecurringDialog(
            currencySymbol = currencySymbol,
            onDismiss = { showAddRecurringDialog = false },
            onSave = { title, type, amount, category, frequency, paymentMethod ->
                viewModel.saveRecurring(
                    title = title,
                    type = type,
                    amount = amount,
                    category = category,
                    frequency = frequency,
                    nextDueDate = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000,
                    paymentMethod = paymentMethod,
                    notes = "",
                    onSuccess = {
                        showAddRecurringDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Recurring reminder added!")
                        }
                    }
                )
            }
        )
    }

    // Dialog: Manage Splits
    if (showManageSplits) {
        ManageSplitsDialog(
            splitList = splitItems,
            currencySymbol = currencySymbol,
            formatCurrency = { viewModel.userPrefs.formatCurrency(it) },
            onDismiss = { showManageSplits = false },
            onAddNew = { showAddSplitDialog = true },
            onToggleSettled = { viewModel.toggleSplitSettled(it) },
            onDelete = { viewModel.deleteSplit(it) }
        )
    }

    // Dialog: Add New Split
    if (showAddSplitDialog) {
        AddSplitExpenseDialog(
            currencySymbol = currencySymbol,
            onDismiss = { showAddSplitDialog = false },
            onSave = { title, totalAmount, peopleCount, myShare, payer, participants ->
                viewModel.saveSplit(
                    title = title,
                    totalAmount = totalAmount,
                    peopleCount = peopleCount,
                    myShare = myShare,
                    payerName = payer,
                    participantsSummary = participants,
                    onSuccess = {
                        showAddSplitDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Split expense created!")
                        }
                    }
                )
            }
        )
    }
}

private fun shareText(context: Context, text: String, title: String) {
    try {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    } catch (_: Exception) {}
}
