package com.example.ui.screens.dashboard

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.ui.components.BudgetProgressBar
import com.example.ui.components.CategoryIconHelper
import com.example.ui.components.InsightCard
import com.example.ui.components.SpendingBarChart
import com.example.ui.components.TransactionItemRow
import com.example.ui.theme.PocketBorder
import com.example.ui.theme.PocketExpenseBg
import com.example.ui.theme.PocketExpenseRed
import com.example.ui.theme.PocketIncomeBg
import com.example.ui.theme.PocketIncomeGreen
import com.example.ui.theme.PocketPrimary
import com.example.ui.theme.PocketPrimaryContainer
import com.example.ui.theme.PocketTextBlueGray
import com.example.ui.theme.PocketTextDarkNavy
import com.example.ui.theme.PocketTextMuted
import com.example.ui.theme.PocketWhite
import com.example.ui.viewmodel.CategorySpending
import com.example.ui.viewmodel.DailySpending
import com.example.ui.viewmodel.DashboardSummary

@Composable
fun DashboardScreen(
    summary: DashboardSummary,
    selectedMonthName: String,
    recentTransactions: List<TransactionEntity>,
    categorySpending: List<CategorySpending>,
    dailySpending: List<DailySpending>,
    savingsGoals: List<SavingsGoalEntity>,
    currencySymbol: String,
    formatCurrency: (Double) -> String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddTransactionClick: () -> Unit,
    onQuickAddClick: () -> Unit,
    onViewAllTransactions: () -> Unit,
    onManageBudgetClick: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onSeedDemoData: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(2.dp)) }

        // 1. KEY CARD: Total Net Balance Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(PocketWhite)
                    .border(1.dp, PocketBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    // Month Switcher Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(PocketPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Month",
                                    tint = PocketPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedMonthName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PocketTextDarkNavy
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onPreviousMonth,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Previous Month",
                                    tint = PocketTextBlueGray
                                )
                            }
                            IconButton(
                                onClick = onNextMonth,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Next Month",
                                    tint = PocketTextBlueGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Current Net Balance",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = PocketTextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Text(
                        text = formatCurrency(summary.currentBalance),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (summary.currentBalance >= 0) PocketTextDarkNavy else PocketExpenseRed,
                            fontSize = 34.sp
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Velocity stats: Today & This Week
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Today's Spending: ",
                                style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted)
                            )
                            Text(
                                text = formatCurrency(summary.todaySpending),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PocketTextDarkNavy
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Past 7 Days: ",
                                style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted)
                            )
                            Text(
                                text = formatCurrency(summary.weekSpending),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PocketTextDarkNavy
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. KEY CARDS: Income & Expense Highlights
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Key Card: Income
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(PocketIncomeBg)
                        .border(1.dp, PocketIncomeGreen.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Income",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PocketIncomeGreen
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(PocketIncomeGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = "Income",
                                    tint = PocketIncomeGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "+${formatCurrency(summary.monthIncome)}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = PocketIncomeGreen,
                                fontSize = 20.sp
                            )
                        )

                        Text(
                            text = "This month",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PocketIncomeGreen.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Key Card: Expenses
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(PocketExpenseBg)
                        .border(1.dp, PocketExpenseRed.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Expenses",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PocketExpenseRed
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(PocketExpenseRed.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingDown,
                                    contentDescription = "Expense",
                                    tint = PocketExpenseRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "-${formatCurrency(summary.monthExpense)}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = PocketExpenseRed,
                                fontSize = 20.sp
                            )
                        )

                        Text(
                            text = "This month",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PocketExpenseRed.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // 3. Smart Spending Insight Card
        item {
            InsightCard(insight = summary.insightText)
        }

        // 4. Monthly Budget Progress Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(PocketWhite)
                    .border(1.dp, PocketBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                if (summary.overallMonthlyBudget > 0) {
                    val isOver = summary.monthExpense > summary.overallMonthlyBudget
                    val diff = if (isOver) summary.monthExpense - summary.overallMonthlyBudget else summary.remainingBudget
                    BudgetProgressBar(
                        title = "Monthly Budget ($selectedMonthName)",
                        spentAmountFormatted = formatCurrency(summary.monthExpense),
                        totalBudgetFormatted = formatCurrency(summary.overallMonthlyBudget),
                        percentage = summary.budgetPercentage,
                        remainingFormatted = formatCurrency(diff),
                        isOverBudget = isOver
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Monthly Budget",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PocketTextDarkNavy
                                )
                            )
                            Text(
                                text = "No monthly limit set yet",
                                style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted)
                            )
                        }
                        Button(
                            onClick = onManageBudgetClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PocketPrimaryContainer)
                        ) {
                            Text(
                                text = "Set Budget",
                                color = PocketPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // 5. Daily Spending Trend Chart
        item {
            SpendingBarChart(
                dailyData = dailySpending,
                currencySymbol = currencySymbol
            )
        }

        // 6. Category Breakdown Summary
        if (categorySpending.isNotEmpty()) {
            item {
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
                            Text(
                                text = "Top Expense Categories",
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

                        Spacer(modifier = Modifier.height(12.dp))

                        categorySpending.take(4).forEach { cat ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(CategoryIconHelper.parseColor(cat.colorHex).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = CategoryIconHelper.getIcon(cat.iconName, cat.category),
                                        contentDescription = null,
                                        tint = CategoryIconHelper.parseColor(cat.colorHex),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = cat.category,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = PocketTextDarkNavy
                                            )
                                        )
                                        Text(
                                            text = formatCurrency(cat.amount),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = PocketTextDarkNavy
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(PocketPrimaryContainer)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth((cat.percentage / 100f).coerceIn(0f, 1f))
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(CategoryIconHelper.parseColor(cat.colorHex))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. KEY SECTION: Recent Transactions Header & List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PocketTextDarkNavy
                    )
                )
                if (recentTransactions.isNotEmpty()) {
                    TextButton(onClick = onViewAllTransactions) {
                        Text(
                            text = "See All (${recentTransactions.size})",
                            color = PocketPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (recentTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(PocketWhite)
                        .border(1.dp, PocketBorder, RoundedCornerShape(18.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = PocketTextMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No transactions yet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PocketTextDarkNavy
                            )
                        )
                        Text(
                            text = "Tap the + Add Entry button below to record your first transaction",
                            style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onAddTransactionClick,
                                colors = ButtonDefaults.buttonColors(containerColor = PocketPrimary)
                            ) {
                                Text("Add First Entry", color = PocketWhite, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = onSeedDemoData
                            ) {
                                Text("Load Demo Data", color = PocketPrimary)
                            }
                        }
                    }
                }
            }
        } else {
            items(recentTransactions.take(6)) { tx ->
                TransactionItemRow(
                    transaction = tx,
                    formattedAmount = formatCurrency(tx.amount),
                    onClick = { onTransactionClick(tx) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(88.dp)) }
    }
}
