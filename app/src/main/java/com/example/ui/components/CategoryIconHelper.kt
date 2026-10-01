package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconHelper {
    fun getIcon(iconName: String, categoryName: String = ""): ImageVector {
        val key = iconName.lowercase().trim().ifEmpty { categoryName.lowercase().trim() }
        return when {
            key.contains("food") || key.contains("restaurant") || key.contains("fastfood") -> Icons.Default.Fastfood
            key.contains("travel") || key.contains("commute") || key.contains("car") || key.contains("cab") -> Icons.Default.DirectionsCar
            key.contains("shopping") -> Icons.Default.ShoppingCart
            key.contains("bill") || key.contains("receipt") -> Icons.Default.Receipt
            key.contains("education") || key.contains("school") -> Icons.Default.School
            key.contains("health") || key.contains("medical") || key.contains("hospital") -> Icons.Default.LocalHospital
            key.contains("entertainment") || key.contains("game") || key.contains("movie") -> Icons.Default.Movie
            key.contains("groceries") || key.contains("grocery") -> Icons.Default.ShoppingCart
            key.contains("rent") || key.contains("home") -> Icons.Default.Home
            key.contains("subscription") -> Icons.Default.Subscriptions
            key.contains("salary") || key.contains("work") -> Icons.Default.Payments
            key.contains("freelance") || key.contains("laptop") -> Icons.Default.Laptop
            key.contains("business") || key.contains("store") -> Icons.Default.Store
            key.contains("gift") -> Icons.Default.CardGiftcard
            key.contains("saving") -> Icons.Default.Savings
            key.contains("upi") || key.contains("qr") -> Icons.Default.QrCode
            key.contains("cash") -> Icons.Default.Money
            key.contains("card") -> Icons.Default.CreditCard
            key.contains("bank") -> Icons.Default.AccountBalance
            else -> Icons.Default.Category
        }
    }

    fun parseColor(hex: String, fallback: Color = Color(0xFF2563EB)): Color {
        return try {
            val cleanHex = if (hex.startsWith("#")) hex else "#$hex"
            Color(android.graphics.Color.parseColor(cleanHex))
        } catch (_: Exception) {
            fallback
        }
    }
}
