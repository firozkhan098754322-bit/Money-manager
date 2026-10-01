package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.PocketBorder
import com.example.ui.theme.PocketExpenseRed
import com.example.ui.theme.PocketPrimary
import com.example.ui.theme.PocketPrimaryContainer
import com.example.ui.theme.PocketTextDarkNavy
import com.example.ui.theme.PocketTextMuted
import com.example.ui.theme.PocketWhite
import java.util.Locale

@Composable
fun AddSplitExpenseDialog(
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (title: String, totalAmount: Double, peopleCount: Int, myShare: Double, payer: String, participants: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var totalAmountText by remember { mutableStateOf("") }
    var peopleCountText by remember { mutableStateOf("3") }
    var payer by remember { mutableStateOf("You") }
    var participants by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val totalAmt = totalAmountText.toDoubleOrNull() ?: 0.0
    val count = peopleCountText.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val calculatedShare = if (count > 0 && totalAmt > 0) totalAmt / count else 0.0

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = PocketPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Split an Expense",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = PocketTextDarkNavy
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = PocketTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; error = null },
                    label = { Text("Expense Title") },
                    placeholder = { Text("e.g. Dinner, Weekend Trip, Movie tickets") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PocketPrimary,
                        unfocusedBorderColor = PocketBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = totalAmountText,
                        onValueChange = { totalAmountText = it; error = null },
                        label = { Text("Total Bill ($currencySymbol)") },
                        placeholder = { Text("1500") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PocketPrimary,
                            unfocusedBorderColor = PocketBorder
                        ),
                        modifier = Modifier.weight(1.3f)
                    )

                    OutlinedTextField(
                        value = peopleCountText,
                        onValueChange = { peopleCountText = it; error = null },
                        label = { Text("People") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PocketPrimary,
                            unfocusedBorderColor = PocketBorder
                        ),
                        modifier = Modifier.weight(0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Calculated Share Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PocketPrimaryContainer)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Equal share per person:",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PocketTextDarkNavy,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "$currencySymbol${String.format(Locale.US, "%.2f", calculatedShare)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PocketPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = participants,
                    onValueChange = { participants = it },
                    label = { Text("Who owes money? (Names/Notes)") },
                    placeholder = { Text("e.g. Rahul owes $currencySymbol${String.format(Locale.US, "%.0f", calculatedShare)}, Sneha owes $currencySymbol${String.format(Locale.US, "%.0f", calculatedShare)}") },
                    singleLine = false,
                    maxLines = 2,
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

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val amt = totalAmountText.toDoubleOrNull()
                        val pCount = peopleCountText.toIntOrNull()
                        if (title.isBlank()) {
                            error = "Please enter an expense title"
                        } else if (amt == null || amt <= 0.0) {
                            error = "Please enter a valid total amount"
                        } else if (pCount == null || pCount <= 0) {
                            error = "Please enter number of people"
                        } else {
                            val myShare = amt / pCount
                            onSave(title, amt, pCount, myShare, payer, participants)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PocketPrimary)
                ) {
                    Text(
                        text = "Save Split Record",
                        fontWeight = FontWeight.Bold,
                        color = PocketWhite
                    )
                }
            }
        }
    }
}
