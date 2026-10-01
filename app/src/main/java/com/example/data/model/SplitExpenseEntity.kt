package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "split_expenses")
data class SplitExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val totalAmount: Double,
    val numberOfPeople: Int,
    val myShare: Double,
    val payerName: String = "You",
    val date: Long = System.currentTimeMillis(),
    val isSettled: Boolean = false,
    val notes: String = "",
    val participantsSummary: String = "" // e.g. "Rahul (₹200 - Unpaid), Priya (₹200 - Paid)"
)
