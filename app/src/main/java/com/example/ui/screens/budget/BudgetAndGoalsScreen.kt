package com.example.ui.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BudgetEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.ui.components.BudgetProgressBar
import com.example.ui.theme.PocketBorder
import com.example.ui.theme.PocketExpenseBg
import com.example.ui.theme.PocketExpenseRed
import com.example.ui.theme.PocketIncomeBg
import com.example.ui.theme.PocketIncomeGreen
import com.example.ui.theme.PocketPrimary
import com.example.ui.theme.PocketPrimaryContainer
import com.example.ui.theme.PocketTextDarkNavy
import com.example.ui.theme.PocketTextMuted
import com.example.ui.theme.PocketWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BudgetAndGoalsScreen(
    budgets: List<BudgetEntity>,
    savingsGoals: List<SavingsGoalEntity>,
    transactions: List<TransactionEntity>,
    selectedMonth: String,
    selectedMonthName: String,
    currencySymbol: String,
    formatCurrency: (Double) -> String,
    onAddBudgetClick: () -> Unit,
    onEditBudgetClick: (BudgetEntity) -> Unit,
    onToggleBudget: (BudgetEntity) -> Unit,
    onDeleteBudget: (BudgetEntity) -> Unit,
    onAddGoalClick: () -> Unit,
    onEditGoalClick: (SavingsGoalEntity) -> Unit,
    onAddMoneyToGoal: (SavingsGoalEntity) -> Unit,
    onWithdrawMoneyFromGoal: (SavingsGoalEntity) -> Unit,
    onDeleteGoal: (SavingsGoalEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Budgets, 1: Savings Goals
    var budgetToDelete by remember { mutableStateOf<BudgetEntity?>(null) }
    var goalToDelete by remember { mutableStateOf<SavingsGoalEntity?>(null) }

    val txMonthFormat = SimpleDateFormat("yyyy-MM", Locale.US)
    val currentMonthExpenses = transactions.filter {
        it.type == "EXPENSE" && txMonthFormat.format(Date(it.date)) == selectedMonth
    }

    val overallExpense = currentMonthExpenses.sumOf { it.amount }
    val categoryExpenses = currentMonthExpenses.groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Segmented Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = PocketWhite,
            contentColor = PocketPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PocketPrimary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, PocketBorder, RoundedCornerShape(14.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "Budgets",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 0) PocketPrimary else PocketTextDarkNavy
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "Savings Goals",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == 1) PocketPrimary else PocketTextDarkNavy
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            // --- Budgets Tab ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Monthly Budgets",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PocketTextDarkNavy
                        )
                    )
                    Text(
                        text = selectedMonthName,
                        style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted)
                    )
                }
                Button(
                    onClick = onAddBudgetClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PocketPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Budget", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val currentMonthBudgets = budgets.filter { it.month == selectedMonth }

            if (currentMonthBudgets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = null,
                            tint = PocketTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No budgets set for $selectedMonthName",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PocketTextDarkNavy
                            )
                        )
                        Text(
                            text = "Set an overall monthly limit or category budgets to stay on track",
                            style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddBudgetClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PocketPrimary)
                        ) {
                            Text("Create Budget", color = PocketWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentMonthBudgets, key = { it.id }) { budget ->
                        val isOverall = budget.category == "ALL"
                        val spent = if (isOverall) overallExpense else (categoryExpenses[budget.category] ?: 0.0)
                        val pct = if (budget.monthlyLimit > 0) ((spent / budget.monthlyLimit) * 100).toFloat() else 0f
                        val isOver = spent > budget.monthlyLimit
                        val diff = if (isOver) spent - budget.monthlyLimit else budget.monthlyLimit - spent

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(PocketWhite)
                                .border(1.dp, PocketBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isOverall) "Overall Monthly Budget" else "${budget.category} Budget",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = PocketTextDarkNavy
                                            )
                                        )
                                        Text(
                                            text = if (budget.isEnabled) "Active" else "Disabled",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (budget.isEnabled) PocketIncomeGreen else PocketTextMuted,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Switch(
                                            checked = budget.isEnabled,
                                            onCheckedChange = { onToggleBudget(budget) },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = PocketWhite,
                                                checkedTrackColor = PocketPrimary
                                            )
                                        )
                                        IconButton(onClick = { onEditBudgetClick(budget) }) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Budget",
                                                tint = PocketPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(onClick = { budgetToDelete = budget }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Budget",
                                                tint = PocketTextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                BudgetProgressBar(
                                    title = if (isOverall) "Total Spending" else "Category Spending",
                                    spentAmountFormatted = formatCurrency(spent),
                                    totalBudgetFormatted = formatCurrency(budget.monthlyLimit),
                                    percentage = pct,
                                    remainingFormatted = formatCurrency(diff),
                                    isOverBudget = isOver
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        } else {
            // --- Savings Goals Tab ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Savings Goals",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PocketTextDarkNavy
                    )
                )
                Button(
                    onClick = onAddGoalClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PocketPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Goal", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (savingsGoals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = PocketTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No savings goals yet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PocketTextDarkNavy
                            )
                        )
                        Text(
                            text = "Set targets for emergency funds, vacations, gadgets, or vehicles",
                            style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddGoalClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PocketPrimary)
                        ) {
                            Text("Create Savings Goal", color = PocketWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(savingsGoals, key = { it.id }) { goal ->
                        val pct = if (goal.targetAmount > 0) ((goal.savedAmount / goal.targetAmount) * 100).toInt() else 0
                        val remaining = (goal.targetAmount - goal.savedAmount).coerceAtLeast(0.0)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(PocketWhite)
                                .border(1.dp, PocketBorder, RoundedCornerShape(18.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = goal.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = PocketTextDarkNavy
                                            )
                                        )
                                        if (goal.notes.isNotBlank()) {
                                            Text(
                                                text = goal.notes,
                                                style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted)
                                            )
                                        }
                                    }

                                    Row {
                                        IconButton(onClick = { onEditGoalClick(goal) }) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Goal",
                                                tint = PocketPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(onClick = { goalToDelete = goal }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Goal",
                                                tint = PocketTextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${formatCurrency(goal.savedAmount)} saved",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PocketIncomeGreen
                                        )
                                    )
                                    Text(
                                        text = "Target: ${formatCurrency(goal.targetAmount)}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = PocketTextDarkNavy
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(PocketPrimaryContainer)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth((pct / 100f).coerceIn(0f, 1f))
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(if (pct >= 100) PocketIncomeGreen else PocketPrimary)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$pct% achieved • ${formatCurrency(remaining)} left",
                                        style = MaterialTheme.typography.labelSmall.copy(color = PocketTextMuted)
                                    )

                                    // Quick deposit & withdraw action buttons
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(PocketExpenseBg)
                                                .clickable { onWithdrawMoneyFromGoal(goal) }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                "- Withdraw",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PocketExpenseRed
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(PocketIncomeBg)
                                                .clickable { onAddMoneyToGoal(goal) }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                "+ Add Money",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PocketIncomeGreen
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    // Delete Budget Confirmation Dialog
    if (budgetToDelete != null) {
        val b = budgetToDelete!!
        AlertDialog(
            onDismissRequest = { budgetToDelete = null },
            title = { Text("Delete Budget?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this budget limit of ${formatCurrency(b.monthlyLimit)}?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteBudget(b)
                        budgetToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PocketExpenseRed)
                ) {
                    Text("Delete", color = PocketWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { budgetToDelete = null }) {
                    Text("Cancel", color = PocketTextDarkNavy)
                }
            }
        )
    }

    // Delete Goal Confirmation Dialog
    if (goalToDelete != null) {
        val g = goalToDelete!!
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = { Text("Delete Savings Goal?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete the goal '${g.name}'? Recorded progress will be lost.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteGoal(g)
                        goalToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PocketExpenseRed)
                ) {
                    Text("Delete", color = PocketWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) {
                    Text("Cancel", color = PocketTextDarkNavy)
                }
            }
        )
    }
}
