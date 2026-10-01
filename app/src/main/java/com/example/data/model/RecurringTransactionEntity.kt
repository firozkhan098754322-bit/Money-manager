package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val type: String, // "EXPENSE" or "INCOME"
    val amount: Double,
    val category: String,
    val frequency: String, // "WEEKLY", "MONTHLY", "YEARLY"
    val nextDueDate: Long,
    val paymentMethod: String = "UPI",
    val notes: String = "",
    val isActive: Boolean = true
)
