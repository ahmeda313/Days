package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FocusTarget
import com.example.data.FocusTargetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class FocusTargetViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FocusTargetRepository

    val allTargets: StateFlow<List<FocusTarget>>

    init {
        val database = AppDatabase.getDatabase(application)
        val dao = database.focusTargetDao()
        repository = FocusTargetRepository(dao)
        allTargets = repository.allTargets.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addTarget(title: String, targetDate: String, colorHex: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (title.isBlank()) {
            onError("Title cannot be empty")
            return
        }
        val dateObj = try {
            LocalDate.parse(targetDate)
        } catch (e: Exception) {
            onError("Invalid date format")
            return
        }
        if (!dateObj.isAfter(LocalDate.now())) {
            onError("Date must be in the future")
            return
        }

        viewModelScope.launch {
            repository.insert(
                FocusTarget(
                    title = title.trim(),
                    targetDate = targetDate,
                    colorHex = colorHex
                )
            )
            onSuccess()
        }
    }

    fun updateTarget(id: Int, title: String, targetDate: String, colorHex: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (title.isBlank()) {
            onError("Title cannot be empty")
            return
        }
        val dateObj = try {
            LocalDate.parse(targetDate)
        } catch (e: Exception) {
            onError("Invalid date format")
            return
        }
        if (!dateObj.isAfter(LocalDate.now())) {
            onError("Date must be in the future")
            return
        }

        viewModelScope.launch {
            repository.update(
                FocusTarget(
                    id = id,
                    title = title.trim(),
                    targetDate = targetDate,
                    colorHex = colorHex
                )
            )
            onSuccess()
        }
    }

    fun deleteTarget(target: FocusTarget) {
        viewModelScope.launch {
            repository.delete(target)
        }
    }

    fun deleteTargetById(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }
}
