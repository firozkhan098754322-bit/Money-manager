package com.example.ui.screens.onboarding

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PocketBorder
import com.example.ui.theme.PocketIncomeGreen
import com.example.ui.theme.PocketPrimary
import com.example.ui.theme.PocketPrimaryContainer
import com.example.ui.theme.PocketSecondary
import com.example.ui.theme.PocketTextDarkNavy
import com.example.ui.theme.PocketTextMuted
import com.example.ui.theme.PocketWhite

@Composable
fun OnboardingScreen(
    onComplete: (currencySymbol: String, currencyCode: String, startingBalance: Double) -> Unit
) {
    var selectedSymbol by remember { mutableStateOf("₹") }
    var selectedCode by remember { mutableStateOf("INR") }
    var startingBalanceText by remember { mutableStateOf("") }

    val currencies = listOf(
        Pair("₹", "INR"),
        Pair("$", "USD"),
        Pair("€", "EUR"),
        Pair("£", "GBP"),
        Pair("¥", "JPY"),
        Pair("A$", "AUD")
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PocketWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button at top right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        val initialBal = startingBalanceText.toDoubleOrNull() ?: 0.0
                        onComplete(selectedSymbol, selectedCode, initialBal)
                    }
                ) {
                    Text("Skip", color = PocketTextMuted, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Logo & Title
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(PocketPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = PocketPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Welcome to PocketLedger",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = PocketTextDarkNavy
                )
            )

            Text(
                text = "Your Money. Your Control.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PocketPrimary,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3 Key Offline & Privacy Badges
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PocketPrimaryContainer.copy(alpha = 0.5f))
                    .border(1.dp, PocketBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureRow(
                    icon = Icons.Default.CloudOff,
                    title = "100% Offline-First",
                    desc = "Works anytime without internet connection."
                )
                FeatureRow(
                    icon = Icons.Default.NoAccounts,
                    title = "No Account or Sign-In",
                    desc = "No email, phone number, or login required."
                )
                FeatureRow(
                    icon = Icons.Default.Security,
                    title = "Complete Privacy",
                    desc = "All financial data is stored locally on this device."
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Select Currency
            Text(
                text = "Choose Default Currency",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PocketTextDarkNavy
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currencies.take(4).forEach { (sym, code) ->
                    val isSelected = selectedCode == code
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PocketPrimary else PocketWhite)
                            .border(
                                1.dp,
                                if (isSelected) PocketPrimary else PocketBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedSymbol = sym
                                selectedCode = code
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = sym,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) PocketWhite else PocketTextDarkNavy
                                )
                            )
                            Text(
                                text = code,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = if (isSelected) PocketWhite.copy(alpha = 0.8f) else PocketTextMuted
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Optional Starting Balance
            Text(
                text = "Starting Balance (Optional)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PocketTextDarkNavy
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = startingBalanceText,
                onValueChange = { startingBalanceText = it },
                label = { Text("Initial Cash / Bank Balance") },
                placeholder = { Text("0") },
                leadingIcon = {
                    Text(
                        text = selectedSymbol,
                        fontWeight = FontWeight.Bold,
                        color = PocketPrimary,
                        fontSize = 18.sp
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PocketPrimary,
                    unfocusedBorderColor = PocketBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Get Started Button
            Button(
                onClick = {
                    val initialBal = startingBalanceText.toDoubleOrNull() ?: 0.0
                    onComplete(selectedSymbol, selectedCode, initialBal)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PocketPrimary)
            ) {
                Text(
                    text = "Get Started",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PocketWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Developed by Mohsin",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = PocketTextMuted,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(PocketWhite),
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
                    fontWeight = FontWeight.Bold,
                    color = PocketTextDarkNavy
                )
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = PocketTextMuted,
                    fontSize = 12.sp
                )
            )
        }
    }
}
