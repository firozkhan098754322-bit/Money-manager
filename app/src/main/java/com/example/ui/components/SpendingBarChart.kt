package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PocketBorder
import com.example.ui.theme.PocketPrimary
import com.example.ui.theme.PocketPrimaryContainer
import com.example.ui.theme.PocketTextDarkNavy
import com.example.ui.theme.PocketTextMuted
import com.example.ui.viewmodel.DailySpending
import java.util.Locale

@Composable
fun SpendingBarChart(
    dailyData: List<DailySpending>,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val nonZeroDays = dailyData.filter { it.amount > 0.0 }
    val maxSpending = (dailyData.maxOfOrNull { it.amount } ?: 0.0).coerceAtLeast(100.0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Spending Trend",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PocketTextDarkNavy
                    )
                )
                Text(
                    text = "Peak: $currencySymbol${String.format(Locale.US, "%.0f", dailyData.maxOfOrNull { it.amount } ?: 0.0)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PocketTextMuted
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (nonZeroDays.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No spending recorded in this period yet",
                        style = MaterialTheme.typography.bodyMedium.copy(color = PocketTextMuted)
                    )
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    val bottomPadding = 18.dp.toPx()
                    val chartHeight = height - bottomPadding

                    // Draw 3 subtle horizontal guidelines
                    val gridColor = PocketBorder.copy(alpha = 0.7f)
                    for (i in 0..2) {
                        val y = chartHeight * (i / 2f)
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    val totalBars = dailyData.size.coerceAtLeast(1)
                    val slotWidth = width / totalBars
                    val barWidth = (slotWidth * 0.65f).coerceIn(4.dp.toPx(), 18.dp.toPx())

                    dailyData.forEachIndexed { index, item ->
                        val barHeight = ((item.amount / maxSpending) * chartHeight).toFloat()
                        val x = index * slotWidth + (slotWidth - barWidth) / 2f
                        val y = chartHeight - barHeight

                        val barColor = if (item.amount == maxSpending && item.amount > 0) {
                            PocketPrimary
                        } else if (item.amount > 0) {
                            PocketPrimary.copy(alpha = 0.75f)
                        } else {
                            PocketPrimaryContainer.copy(alpha = 0.4f)
                        }

                        // Draw background track for bar
                        drawRoundRect(
                            color = PocketPrimaryContainer.copy(alpha = 0.35f),
                            topLeft = Offset(x, 0f),
                            size = Size(barWidth, chartHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )

                        // Draw actual spending bar
                        if (barHeight > 0) {
                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                            )
                        }
                    }
                }

                // Days timeline labels (1, 5, 10, 15, 20, 25, 30)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val keyDays = listOf(1, 5, 10, 15, 20, 25, if (dailyData.size >= 30) dailyData.size else 28)
                    keyDays.forEach { dayNum ->
                        if (dayNum <= dailyData.size) {
                            Text(
                                text = "D$dayNum",
                                fontSize = 10.sp,
                                color = PocketTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
