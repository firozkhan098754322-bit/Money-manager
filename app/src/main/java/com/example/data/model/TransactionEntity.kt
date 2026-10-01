package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val type: String, // "EXPENSE" or "INCOME"
    val amount: Double,
    val category: String,
    val date: Long, // timestamp in millis
    val notes: String = "",
    val paymentMethod: String = "UPI", // "CASH", "UPI", "CARD", "BANK_TRANSFER", "OTHER"
    val createdAt: Long = System.currentTimeMillis()
)
