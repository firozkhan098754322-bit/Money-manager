package com.example.ui.screens.tools

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.RecurringTransactionEntity
import com.example.data.model.SplitExpenseEntity
import com.example.data.model.TransactionEntity
import com.example.ui.theme.PocketBorder
import com.example.ui.theme.PocketExpenseBg
import com.example.ui.theme.PocketExpenseRed
import com.example.ui.theme.PocketIncomeBg
import com.example.ui.theme.PocketIncomeGreen
import com.example.ui.theme.PocketPrimary
import com.example.ui.theme.PocketPrimaryContainer
import com.example.ui.theme.PocketSecondary
import com.example.ui.theme.PocketTextDarkNavy
import com.example.ui.theme.PocketTextMuted
import com.example.ui.theme.PocketWhite
import java.util.Locale

@Composable
fun ToolsAndSettingsScreen(
    currencySymbol: String,
    currencyCode: String,
    useIndianFormat: Boolean,
    startingBalance: Double,
    isPinEnabled: Boolean,
    recurringItems: List<RecurringTransactionEntity>,
    splitItems: List<SplitExpenseEntity>,
    categories: List<CategoryEntity>,
    formatCurrency: (Double) -> String,
    onSetCurrency: (symbol: String, code: String) -> Unit,
    onToggleIndianFormat: (Boolean) -> Unit,
    onSetStartingBalance: (Double) -> Unit,
    onSetPin: (String) -> Unit,
    onDisablePin: () -> Unit,
    onOpenRecurring: () -> Unit,
    onOpenSplit: () -> Unit,
    onExportCsv: () -> Unit,
    onExportBackupJson: () -> Unit,
    onRestoreBackupJson: (String) -> Unit,
    onAddCategory: (name: String, type: String) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit,
    onLoadDemoData: () -> Unit,
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showBalanceDialog by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(2.dp)) }

        // Section 1: Financial Utilities
        item {
            Text(
                text = "Financial Tools",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PocketTextDarkNavy
                )
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Recurring item card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PocketWhite)
                        .border(1.dp, PocketBorder, RoundedCornerShape(16.dp))
                        .clickable { onOpenRecurring() }
                        .padding(14.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PocketPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Autorenew,
                                contentDescription = null,
                                tint = PocketPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Recurring Bills",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PocketTextDarkNavy
                            )
                        )
                        Text(
                            text = "${recurringItems.size} active reminders",
                            style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted)
                        )
                    }
                }

                // Split expenses card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PocketWhite)
                        .border(1.dp, PocketBorder, RoundedCornerShape(16.dp))
                        .clickable { onOpenSplit() }
                        .padding(14.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PocketPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = PocketPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Split Expenses",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PocketTextDarkNavy
                            )
                        )
                        Text(
                            text = "${splitItems.size} recorded splits",
                            style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted)
                        )
                    }
                }
            }
        }

        // Section 2: Preferences
        item {
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PocketTextDarkNavy
                )
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PocketWhite)
                    .border(1.dp, PocketBorder, RoundedCornerShape(16.dp))
                    .padding(vertical = 4.dp)
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.Payments,
                        title = "Currency",
                        subtitle = "$currencyCode ($currencySymbol)",
                        onClick = { showCurrencyDialog = true }
                    )

                    SettingsRowWithToggle(
                        icon = Icons.Default.Tune,
                        title = "Indian Number System",
                        subtitle = "Format as ₹1,25,000 instead of 125,000",
                        checked = useIndianFormat,
                        onCheckedChange = onToggleIndianFormat
                    )

                    SettingsRow(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = "Starting Balance",
                        subtitle = formatCurrency(startingBalance),
                        onClick = { showBalanceDialog = true }
                    )

                    SettingsRowWithToggle(
                        icon = Icons.Default.Lock,
                        title = "PIN App Lock",
                        subtitle = if (isPinEnabled) "4-digit PIN is active" else "Protect app on launch",
                        checked = isPinEnabled,
                        onCheckedChange = { checked ->
                            if (checked) {
                                showPinDialog = true
                            } else {
                                onDisablePin()
                            }
                        }
                    )
                }
            }
        }

        // Section 3: Backup & Export
        item {
            Text(
                text = "Backup & Local Data",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PocketTextDarkNavy
                )
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PocketWhite)
                    .border(1.dp, PocketBorder, RoundedCornerShape(16.dp))
                    .padding(vertical = 4.dp)
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.FileDownload,
                        title = "Export to CSV Spreadsheet",
                        subtitle = "Share or open in Excel / Sheets",
                        onClick = onExportCsv
                    )

                    SettingsRow(
                        icon = Icons.Default.CloudUpload,
                        title = "Backup to JSON File",
                        subtitle = "Export complete offline database",
                        onClick = onExportBackupJson
                    )

                    SettingsRow(
                        icon = Icons.Default.CloudDownload,
                        title = "Restore from JSON File",
                        subtitle = "Restore previously saved transactions",
                        onClick = { showRestoreDialog = true }
                    )

                    SettingsRow(
                        icon = Icons.Default.Category,
                        title = "Manage Categories",
                        subtitle = "${categories.size} categories available",
                        onClick = { showAddCategoryDialog = true }
                    )
                }
            }
        }

        // Section 4: Data Management & Testing
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PocketWhite)
                    .border(1.dp, PocketBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Data Actions",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PocketTextDarkNavy
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onLoadDemoData,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PocketPrimaryContainer)
                        ) {
                            Text("Load Demo Data", color = PocketPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = { showClearConfirm = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PocketExpenseBg)
                        ) {
                            Text("Clear All Data", color = PocketExpenseRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Section 5: Privacy & About
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PocketPrimaryContainer.copy(alpha = 0.5f))
                    .border(1.dp, PocketBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = PocketPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "100% Offline & Private",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PocketTextDarkNavy
                            )
                        )
                    }
                    Text(
                        text = "PocketLedger operates entirely on your device. Your income, expenses, and notes never leave this phone and no analytics or accounts are tracked.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = PocketTextDarkNavy.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        // Section 6: About Screen & Credits
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PocketWhite)
                    .border(1.dp, PocketBorder, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PocketLedger",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = PocketTextDarkNavy,
                            fontSize = 22.sp
                        )
                    )
                    Text(
                        text = "Your Money. Your Control.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PocketPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(PocketPrimaryContainer)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Developed by Mohsin",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PocketPrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Version 1.0.0 • Offline Edition",
                        style = MaterialTheme.typography.labelSmall.copy(color = PocketTextMuted)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Currency Selection Dialog
    if (showCurrencyDialog) {
        val currencyList = listOf(
            Pair("₹", "INR (Indian Rupee)"),
            Pair("$", "USD (US Dollar)"),
            Pair("€", "EUR (Euro)"),
            Pair("£", "GBP (British Pound)"),
            Pair("¥", "JPY (Japanese Yen)"),
            Pair("A$", "AUD (Australian Dollar)"),
            Pair("C$", "CAD (Canadian Dollar)"),
            Pair("AED", "AED (UAE Dirham)")
        )
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Select Currency", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    currencyList.forEach { (sym, name) ->
                        val isSelected = currencySymbol == sym
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PocketPrimaryContainer else Color.Transparent)
                                .clickable {
                                    val code = name.take(3)
                                    onSetCurrency(sym, code)
                                    showCurrencyDialog = false
                                }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            Text(text = sym, fontWeight = FontWeight.Bold, color = PocketPrimary)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Close", color = PocketTextDarkNavy)
                }
            }
        )
    }

    // Starting Balance Dialog
    if (showBalanceDialog) {
        var balText by remember { mutableStateOf(String.format(Locale.US, "%.0f", startingBalance)) }
        AlertDialog(
            onDismissRequest = { showBalanceDialog = false },
            title = { Text("Set Starting Balance", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = balText,
                    onValueChange = { balText = it },
                    label = { Text("Initial Cash / Bank Balance ($currencySymbol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PocketPrimary)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = balText.toDoubleOrNull() ?: 0.0
                        onSetStartingBalance(parsed)
                        showBalanceDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PocketPrimary)
                ) {
                    Text("Save", color = PocketWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBalanceDialog = false }) {
                    Text("Cancel", color = PocketTextDarkNavy)
                }
            }
        )
    }

    // PIN Setup Dialog
    if (showPinDialog) {
        var pinInput by remember { mutableStateOf("") }
        var pinError by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Set 4-Digit App PIN", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter a 4-digit PIN to secure PocketLedger:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { ch -> ch.isDigit() }) {
                                pinInput = it
                                pinError = null
                            }
                        },
                        label = { Text("4-digit PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PocketPrimary)
                    )
                    if (pinError != null) {
                        Text(pinError!!, color = PocketExpenseRed, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pinInput.length != 4) {
                            pinError = "PIN must be exactly 4 digits"
                        } else {
                            onSetPin(pinInput)
                            showPinDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PocketPrimary)
                ) {
                    Text("Enable PIN", color = PocketWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("Cancel", color = PocketTextDarkNavy)
                }
            }
        )
    }

    // JSON Restore Dialog
    if (showRestoreDialog) {
        var jsonText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restore From JSON Backup", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Paste your exported PocketLedger JSON backup content below:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = jsonText,
                        onValueChange = { jsonText = it },
                        placeholder = { Text("{\"app\":\"PocketLedger\", ...}") },
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PocketPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (jsonText.isNotBlank()) {
                            onRestoreBackupJson(jsonText)
                            showRestoreDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PocketPrimary)
                ) {
                    Text("Restore", color = PocketWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Cancel", color = PocketTextDarkNavy)
                }
            }
        )
    }

    // Clear All Confirmation
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear All Transactions?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently delete all stored transactions from this device. Are you sure?") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PocketExpenseRed)
                ) {
                    Text("Clear Everything", color = PocketWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel", color = PocketTextDarkNavy)
                }
            }
        )
    }

    // Add Category Dialog
    if (showAddCategoryDialog) {
        var catName by remember { mutableStateOf("") }
        var catType by remember { mutableStateOf("EXPENSE") }
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add Custom Category", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = catName,
                        onValueChange = { catName = it },
                        label = { Text("Category Name") },
                        placeholder = { Text("e.g. Pet Care, Charity") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PocketPrimary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { catType = "EXPENSE" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (catType == "EXPENSE") PocketExpenseRed else PocketBorder
                            )
                        ) {
                            Text("Expense", color = if (catType == "EXPENSE") PocketWhite else PocketTextDarkNavy)
                        }
                        Button(
                            onClick = { catType = "INCOME" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (catType == "INCOME") PocketIncomeGreen else PocketBorder
                            )
                        ) {
                            Text("Income", color = if (catType == "INCOME") PocketWhite else PocketTextDarkNavy)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (catName.isNotBlank()) {
                            onAddCategory(catName, catType)
                            showAddCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PocketPrimary)
                ) {
                    Text("Add", color = PocketWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancel", color = PocketTextDarkNavy)
                }
            }
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PocketPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PocketPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PocketTextDarkNavy
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted)
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = PocketTextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun SettingsRowWithToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PocketPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PocketPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PocketTextDarkNavy
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = PocketTextMuted)
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PocketWhite,
                checkedTrackColor = PocketPrimary
            )
        )
    }
}
