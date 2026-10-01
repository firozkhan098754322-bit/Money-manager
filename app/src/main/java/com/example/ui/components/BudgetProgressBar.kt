package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PocketExpenseRed
import com.example.ui.theme.PocketPrimary
import com.example.ui.theme.PocketPrimaryContainer
import com.example.ui.theme.PocketTextDarkNavy
import com.example.ui.theme.PocketTextMuted
import com.example.ui.theme.PocketWarningAmber

@Composable
fun BudgetProgressBar(
    title: String,
    spentAmountFormatted: String,
    totalBudgetFormatted: String,
    percentage: Float,
    remainingFormatted: String,
    isOverBudget: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (percentage / 100f).coerceIn(0f, 1f),
        label = "budgetProgress"
    )

    val progressColor = when {
        isOverBudget -> PocketExpenseRed
        percentage >= 80f -> PocketWarningAmber
        else -> PocketPrimary
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PocketTextDarkNavy
                )
            )
            Text(
                text = "$spentAmountFormatted / $totalBudgetFormatted",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = PocketTextMuted
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(PocketPrimaryContainer)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .background(progressColor)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${percentage.toInt()}% used",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isOverBudget) PocketExpenseRed else PocketTextMuted,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = if (isOverBudget) "Over budget by $remainingFormatted" else "$remainingFormatted left",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isOverBudget) PocketExpenseRed else PocketPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
