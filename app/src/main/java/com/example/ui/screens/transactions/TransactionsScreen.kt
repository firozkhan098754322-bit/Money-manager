package com.example.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionEntity
import com.example.ui.components.TransactionItemRow
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

@Composable
fun TransactionsScreen(
    transactions: List<TransactionEntity>,
    categories: List<CategoryEntity>,
    searchQuery: String,
    filterType: String,
    filterCategory: String,
    filterPaymentMethod: String,
    sortOrder: String,
    formatCurrency: (Double) -> String,
    onSearchChange: (String) -> Unit,
    onFilterTypeChange: (String) -> Unit,
    onFilterCategoryChange: (String) -> Unit,
    onFilterPaymentMethodChange: (String) -> Unit,
    onSortOrderChange: (String) -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    onAddTransactionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<TransactionEntity?>(null) }

    // Summary calculations for filtered list
    val totalIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val net = totalIncome - totalExpense

    val paymentMethods = listOf("ALL", "UPI", "Cash", "Card", "Bank Transfer", "Other")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search transactions, notes, amount...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = PocketPrimary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = PocketTextMuted
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PocketPrimary,
                unfocusedBorderColor = PocketBorder,
                focusedContainerColor = PocketWhite,
                unfocusedContainerColor = PocketWhite
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Type filter chips & Sort dropdown button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("ALL", "EXPENSE", "INCOME").forEach { type ->
                    val isSelected = filterType == type
                    val label = when (type) {
                        "ALL" -> "All"
                        "EXPENSE" -> "Expenses"
                        "INCOME" -> "Income"
                        else -> type
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterTypeChange(type) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (type == "EXPENSE") PocketExpenseRed else if (type == "INCOME") PocketIncomeGreen else PocketPrimary,
                            selectedLabelColor = PocketWhite,
                            containerColor = PocketWhite,
                            labelColor = PocketTextDarkNavy
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = PocketBorder
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Payment method filters
                paymentMethods.filter { it != "ALL" }.forEach { method ->
                    val isSelected = filterPaymentMethod.equals(method, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) onFilterPaymentMethodChange("ALL")
                            else onFilterPaymentMethodChange(method)
                        },
                        label = { Text(method) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PocketPrimaryContainer,
                            selectedLabelColor = PocketPrimary,
                            containerColor = PocketWhite,
                            labelColor = PocketTextDarkNavy
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = PocketBorder,
                            selectedBorderColor = PocketPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            // Sort button
            Box {
                IconButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PocketWhite)
                        .border(1.dp, PocketBorder, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort",
                        tint = PocketPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                    modifier = Modifier.background(PocketWhite)
                ) {
                    DropdownMenuItem(
                        text = { Text("Newest First", fontWeight = if (sortOrder == "NEWEST") FontWeight.Bold else FontWeight.Normal) },
                        onClick = { onSortOrderChange("NEWEST"); showSortMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Oldest First", fontWeight = if (sortOrder == "OLDEST") FontWeight.Bold else FontWeight.Normal) },
                        onClick = { onSortOrderChange("OLDEST"); showSortMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Highest Amount", fontWeight = if (sortOrder == "HIGHEST") FontWeight.Bold else FontWeight.Normal) },
                        onClick = { onSortOrderChange("HIGHEST"); showSortMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Lowest Amount", fontWeight = if (sortOrder == "LOWEST") FontWeight.Bold else FontWeight.Normal) },
                        onClick = { onSortOrderChange("LOWEST"); showSortMenu = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Summary Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PocketWhite)
                .border(1.dp, PocketBorder, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${transactions.size} transactions",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PocketTextMuted,
                        fontWeight = FontWeight.Medium
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "+${formatCurrency(totalIncome)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PocketIncomeGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "-${formatCurrency(totalExpense)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PocketExpenseRed,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Transaction list
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = PocketTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No matching transactions found" else "No transactions yet",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PocketTextDarkNavy
                        )
                    )
                    Text(
                        text = if (searchQuery.isNotEmpty()) "Try adjusting your search terms or filters" else "Tap below to add your first transaction",
                        style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (searchQuery.isEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onAddTransactionClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PocketPrimary)
                        ) {
                            Text("Add Transaction", color = PocketWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(transactions, key = { it.id }) { tx ->
                    TransactionItemRow(
                        transaction = tx,
                        formattedAmount = formatCurrency(tx.amount),
                        onClick = { onTransactionClick(tx) },
                        onDelete = { transactionToDelete = tx }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // Delete Confirmation Dialog
    if (transactionToDelete != null) {
        val tx = transactionToDelete!!
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Delete Transaction?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this ${tx.category} transaction of ${formatCurrency(tx.amount)}? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTransaction(tx)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PocketExpenseRed)
                ) {
                    Text("Delete", color = PocketWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Cancel", color = PocketTextDarkNavy)
                }
            }
        )
    }
}
