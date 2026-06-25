package com.example.data

import kotlinx.coroutines.flow.Flow

class FocusTargetRepository(private val focusTargetDao: FocusTargetDao) {
    val allTargets: Flow<List<FocusTarget>> = focusTargetDao.getAllTargets()

    suspend fun insert(target: FocusTarget) {
        focusTargetDao.insertTarget(target)
    }

    suspend fun update(target: FocusTarget) {
        focusTargetDao.updateTarget(target)
    }

    suspend fun delete(target: FocusTarget) {
        focusTargetDao.deleteTarget(target)
    }

    suspend fun deleteById(id: Int) {
        focusTargetDao.deleteTargetById(id)
    }
}
