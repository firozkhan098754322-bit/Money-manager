package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val category: String, // "ALL" for overall monthly budget or specific category name
    val month: String, // "YYYY-MM"
    val monthlyLimit: Double,
    val isEnabled: Boolean = true
)
