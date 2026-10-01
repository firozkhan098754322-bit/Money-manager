package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PocketBorder
import com.example.ui.theme.PocketIncomeGreen
import com.example.ui.theme.PocketPrimary
import com.example.ui.theme.PocketPrimaryContainer
import com.example.ui.theme.PocketTextDarkNavy
import com.example.ui.theme.PocketTextMuted
import com.example.ui.theme.PocketWhite

@Composable
fun PocketLedgerTopBar(
    onQuickAddClick: () -> Unit,
    onLockClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(PocketWhite)
            .statusBarsPadding()
            .border(width = 0.5.dp, color = PocketBorder)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Identity
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "PocketLedger",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = PocketTextDarkNavy,
                            fontSize = 20.sp,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Offline badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PocketPrimaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(PocketIncomeGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "OFFLINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PocketPrimary
                                )
                            )
                        }
                    }
                }
                Text(
                    text = "Your Money. Your Control.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = PocketTextMuted,
                        fontSize = 11.sp
                    )
                )
            }

            // Quick Add action button in top bar
            IconButton(
                onClick = onQuickAddClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(PocketPrimaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = "Quick Add Transaction",
                    tint = PocketPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (onLockClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onLockClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PocketPrimaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock App",
                        tint = PocketPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
