package com.example.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.ui.components.CategoryIconHelper
import com.example.ui.components.SpendingBarChart
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
import com.example.ui.viewmodel.CategorySpending
import com.example.ui.viewmodel.DailySpending
import com.example.ui.viewmodel.DashboardSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(
    summary: DashboardSummary,
    selectedMonth: String,
    selectedMonthName: String,
    transactions: List<TransactionEntity>,
    categorySpending: List<CategorySpending>,
    dailySpending: List<DailySpending>,
    currencySymbol: String,
    formatCurrency: (Double) -> String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val txMonthFormat = SimpleDateFormat("yyyy-MM", Locale.US)
    val monthTransactions = transactions.filter {
        txMonthFormat.format(Date(it.date)) == selectedMonth
    }
    val monthExpenses = monthTransactions.filter { it.type == "EXPENSE" }
    val monthIncome = monthTransactions.filter { it.type == "INCOME" }

    val totalIncome = monthIncome.sumOf { it.amount }
    val totalExpense = monthExpenses.sumOf { it.amount }
    val netSavings = totalIncome - totalExpense
    val savingsRate = if (totalIncome > 0) ((netSavings / totalIncome) * 100).coerceIn(-100.0, 100.0).toInt() else 0

    // Payment method distribution
    val paymentMethodDistribution = monthExpenses.groupBy { it.paymentMethod }
        .mapValues { it.value.sumOf { tx -> tx.amount } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(2.dp)) }

        // Month Selector Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PocketWhite)
                    .border(1.dp, PocketBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = PocketPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedMonthName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PocketTextDarkNavy
                            )
                        )
                    }
                    Row {
                        IconButton(onClick = onPreviousMonth, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Month",
                                tint = PocketPrimary
                            )
                        }
                        IconButton(onClick = onNextMonth, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Month",
                                tint = PocketPrimary
                            )
                        }
                    }
                }
            }
        }

        // Comparison Cards: Income vs Expense vs Net
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Income Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PocketIncomeBg)
                        .border(1.dp, PocketIncomeGreen.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = PocketIncomeGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Total Income",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = PocketIncomeGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = formatCurrency(totalIncome),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = PocketIncomeGreen
                            )
                        )
                    }
                }

                // Expense Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PocketExpenseBg)
                        .border(1.dp, PocketExpenseRed.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = PocketExpenseRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Total Expenses",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = PocketExpenseRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = formatCurrency(totalExpense),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = PocketExpenseRed
                            )
                        )
                    }
                }
            }
        }

        // Net Savings Summary Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PocketWhite)
                    .border(1.dp, PocketBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Net Monthly Savings",
                            style = MaterialTheme.typography.labelMedium.copy(color = PocketTextMuted)
                        )
                        Text(
                            text = formatCurrency(netSavings),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = if (netSavings >= 0) PocketIncomeGreen else PocketExpenseRed
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(PocketPrimaryContainer)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Savings Rate: $savingsRate%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PocketPrimary
                            )
                        )
                    }
                }
            }
        }

        // Daily Spending Chart
        item {
            SpendingBarChart(
                dailyData = dailySpending,
                currencySymbol = currencySymbol
            )
        }

        // Key Financial Metrics Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PocketWhite)
                    .border(1.dp, PocketBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Monthly Financial Highlights",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PocketTextDarkNavy
                        )
                    )

                    MetricRow(
                        label = "Highest Expense Category",
                        value = if (summary.highestCategoryName.isNotEmpty()) "${summary.highestCategoryName} (${formatCurrency(summary.highestCategoryAmount)})" else "None"
                    )
                    MetricRow(
                        label = "Largest Single Transaction",
                        value = formatCurrency(summary.highestTransactionAmount)
                    )
                    MetricRow(
                        label = "Daily Average Spending",
                        value = formatCurrency(summary.dailyAverage)
                    )
                    MetricRow(
                        label = "Recorded Transactions Count",
                        value = "${monthTransactions.size} transactions"
                    )
                }
            }
        }

        // Category Breakdown Full List
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PocketWhite)
                    .border(1.dp, PocketBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Spending by Category",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PocketTextDarkNavy
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (categorySpending.isEmpty()) {
                        Text(
                            text = "No category data available for this month",
                            style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted)
                        )
                    } else {
                        categorySpending.forEach { cat ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
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
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = cat.category,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = PocketTextDarkNavy
                                            )
                                        )
                                        Text(
                                            text = " (${cat.count})",
                                            style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted)
                                        )
                                    }
                                    Text(
                                        text = "${formatCurrency(cat.amount)} (${cat.percentage.toInt()}%)",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PocketTextDarkNavy
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
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

        // Payment Method Breakdown
        if (paymentMethodDistribution.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PocketWhite)
                        .border(1.dp, PocketBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Payment Methods Used",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PocketTextDarkNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        paymentMethodDistribution.forEach { (method, amount) ->
                            val pct = if (totalExpense > 0) ((amount / totalExpense) * 100).toInt() else 0
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = method,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = PocketTextDarkNavy)
                                )
                                Text(
                                    text = "${formatCurrency(amount)} ($pct%)",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PocketPrimary
                                    )
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

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = PocketTextDarkNavy
            )
        )
    }
}
