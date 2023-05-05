package com.example.radiostations.data.repository

import com.example.radiostations.common.BaseCoroutineScope
import com.example.radiostations.data.db.dao.CategoryDao
import com.example.radiostations.data.remote.client.radio.RadioClientImpl
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val radioCommunicator: RadioClientImpl,
    private val categoryDao: CategoryDao
) : CategoryRepository, BaseCoroutineScope() {

    override suspend fun loadCategories() {
        radioCommunicator.loadCategories()
    }
}