package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryEntity
import com.example.ui.theme.PocketBorder
import com.example.ui.theme.PocketExpenseRed
import com.example.ui.theme.PocketPrimary
import com.example.ui.theme.PocketPrimaryContainer
import com.example.ui.theme.PocketTextDarkNavy
import com.example.ui.theme.PocketTextMuted
import com.example.ui.theme.PocketWhite
import java.util.Locale

@Composable
fun AddBudgetDialog(
    initialBudget: BudgetEntity? = null,
    categories: List<CategoryEntity>,
    currentMonth: String,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (category: String, limit: Double) -> Unit
) {
    var selectedCategory by remember {
        mutableStateOf(initialBudget?.category ?: "ALL")
    }
    var limitText by remember {
        mutableStateOf(if (initialBudget != null) String.format(Locale.US, "%.0f", initialBudget.monthlyLimit) else "")
    }
    var error by remember { mutableStateOf<String?>(null) }

    val categoryOptions = listOf("ALL") + categories.filter { it.type == "EXPENSE" }.map { it.name }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            color = PocketWhite,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialBudget == null) "Set Budget" else "Edit Budget",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PocketTextDarkNavy
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = PocketTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Applies to: $currentMonth",
                    style = MaterialTheme.typography.bodySmall.copy(color = PocketPrimary, fontWeight = FontWeight.SemiBold)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Scope / Category",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PocketTextDarkNavy
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryOptions.forEach { cat ->
                        val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                        val displayName = if (cat == "ALL") "Overall Monthly" else cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PocketPrimary else PocketWhite)
                                .border(
                                    1.dp,
                                    if (isSelected) PocketPrimary else PocketBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = displayName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) PocketWhite else PocketTextDarkNavy
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = limitText,
                    onValueChange = {
                        limitText = it
                        error = null
                    },
                    label = { Text("Budget Limit ($currencySymbol)") },
                    placeholder = { Text("e.g., 25000") },
                    leadingIcon = {
                        Text(
                            text = currencySymbol,
                            fontWeight = FontWeight.Bold,
                            color = PocketPrimary,
                            fontSize = 18.sp
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PocketPrimary,
                        unfocusedBorderColor = PocketBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (error != null) {
                    Text(
                        text = error!!,
                        color = PocketExpenseRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val parsed = limitText.toDoubleOrNull()
                        if (parsed == null || parsed <= 0.0) {
                            error = "Please enter a valid budget limit"
                        } else {
                            onSave(selectedCategory, parsed)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PocketPrimary)
                ) {
                    Text(
                        text = "Save Budget",
                        fontWeight = FontWeight.Bold,
                        color = PocketWhite
                    )
                }
            }
        }
    }
}
