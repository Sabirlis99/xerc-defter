package com.subhan.xercdefteri.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val categoryId: String,
    val note: String,
    val dateIso: String // yyyy-MM-dd
)
