package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_targets")
data class FocusTarget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val targetDate: String, // format: "yyyy-MM-dd"
    val colorHex: String,   // format: "#RRGGBB"
    val createdAt: Long = System.currentTimeMillis()
)
